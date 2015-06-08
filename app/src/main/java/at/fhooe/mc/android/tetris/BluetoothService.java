package at.fhooe.mc.android.tetris;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Platti on 02.06.2015.
 */
public class BluetoothService {
    private static final String TAG = "Tetris Log-Tag";
    private static BluetoothService service;

    public static BluetoothService getInstance(Context context, TetrisHandler handler) {
        if (service == null) {
            service = new BluetoothService(context, handler);
        }
        service.mHandler = handler;
        return service;
    }

    private final BluetoothAdapter mBluetoothAdapter;
    private TetrisHandler mHandler;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private Context context;
    private int state;

    public BluetoothService(Context context, TetrisHandler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        this.context = context;
        state = Constants.STATE_NONE;
    }


    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
            state = Constants.STATE_LISTEN;
        }
    }

    public synchronized void connect(MyBluetoothDevice device) {
        if (mConnectThread == null && mConnectedThread == null) {
            mConnectThread = new ConnectThread(mBluetoothAdapter.getRemoteDevice(device.getAddress()));
            mConnectThread.start();
            Log.i(TAG, "Trying to connect to " + device.getName());
            state = Constants.STATE_CONNECTING;
        }
    }

    public void write(TetrisProtocol data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(data);
            byte[] bytes = bos.toByteArray();
            mConnectedThread.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    public synchronized void manageConnectedSocket(BluetoothSocket socket, boolean isServer) {

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        Log.i("Bluetooth", "Socket available: " + String.valueOf(socket != null));
        mConnectedThread = new ConnectedThread(socket);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        state = Constants.STATE_CONNECTED;
        Intent i = new Intent(context, MultiplayerActivity.class);
        i.putExtra("server", isServer);
        context.startActivity(i);

        mConnectedThread.start();
        this.write(new TetrisProtocol("Verbunden mit  " + mBluetoothAdapter.getName()));
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.NAME, Constants.MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.i(TAG, "Somebody is connecting to your device");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getBaseContext(), "Somebody is connecting to your device...", Toast.LENGTH_LONG).show();
//                        }
//                    });
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket, true);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(Constants.MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Log.i(TAG, "Connecting without Exception to " + mmDevice.getName());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getBaseContext(), "Connecting to " + mmDevice.getName() + "...", Toast.LENGTH_LONG).show();
//                    }
//                });
            } catch (IOException connectException) {
                Log.e(TAG, "Error: Connection to " + mmDevice.getName());
                state = Constants.STATE_LISTEN;
                mHandler.obtainMessage(Constants.MESSAGE_TOAST, "Could not connect");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getBaseContext(), "Could not connect to " + mmDevice.getName(), Toast.LENGTH_LONG).show();
//                    }
//                });
                mConnectThread = null;
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket, false);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.i(TAG, "Getting streams failed");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Sending test message failed");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
