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
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {
    private static final String TAG = "Tetris Log-Tag";
    private static BluetoothService service;

    /**
     * creates or returns a Singelton-Object of these class
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     * @return the singelton-Object of these class
     */
    public static BluetoothService getInstance(Context context, TetrisHandler handler) {
        if (service == null) {
            service = new BluetoothService(context, handler);
        }
        service.context = context;
        service.mHandler = handler;
        return service;
    }

    public final BluetoothAdapter mBluetoothAdapter;
    private TetrisHandler mHandler;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private Context context;
    private int state;

    /**
     * Constructor to initialize a new instance of BluetoothService
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Context context, TetrisHandler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        this.context = context;
        state = Constants.STATE_NONE;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
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

    /**
     * Stop all threads
     */
    public synchronized void stop() {
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
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(MyBluetoothDevice device) {
        if (mConnectThread == null && mConnectedThread == null) {
            mConnectThread = new ConnectThread(mBluetoothAdapter.getRemoteDevice(device.getAddress()));
            mConnectThread.start();
            Log.i(TAG, "Trying to connect to " + device.getName());
            state = Constants.STATE_CONNECTING;
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param data contains all information which is needed for the UI Activity
     */
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


    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param isServer to differentiate which device is Server(true) and Client(false)
     */
    public synchronized void manageConnectedSocket(BluetoothSocket socket, boolean isServer) {

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        TetrisMediaPlayer mediaPlayer;
        mediaPlayer = TetrisMediaPlayer.getInstance(context, R.raw.menu_theme);
        mediaPlayer.stop();

        state = Constants.STATE_CONNECTED;
        Intent i = new Intent(context, MultiplayerActivity.class);
        i.putExtra("server", isServer);
        context.startActivity(i);

        mConnectedThread.start();
        this.write(new TetrisProtocol(context.getString(R.string.connected_with) + " " + mBluetoothAdapter.getName()));
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
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

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
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
            } catch (IOException connectException) {
                Log.e(TAG, "Error: Connection to " + mmDevice.getName());
                state = Constants.STATE_LISTEN;
                ((BluetoothMenu) context).connectingDialog.dismiss();
                mHandler.obtainMessage(Constants.MESSAGE_TOAST, context.getString(R.string.could_not_connect_to) + " " + mmDevice.getName()).sendToTarget();
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
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
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
                    if (context instanceof MultiplayerActivity) {
                        ((MultiplayerActivity) context).opponentGameOver = true;
                        ((MultiplayerActivity) context).opponentScore = -1;
                        mHandler.obtainMessage(Constants.MESSAGE_TOAST, context.getString(R.string.connection_lost)).sendToTarget();
                    }
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
