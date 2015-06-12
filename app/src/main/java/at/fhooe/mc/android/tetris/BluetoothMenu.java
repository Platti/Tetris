package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import at.fhooe.mc.android.tetris.R;

public class BluetoothMenu extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    DeviceArrayAdapter mArrayAdapter;
    BluetoothService service;
    public ProgressDialog connectingDialog;

    private final TetrisHandler mHandler = new TetrisHandler(this);

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(new MyBluetoothDevice(device.getName(), device.getAddress(), false));
            }
            ListView deviceList = (ListView) findViewById(R.id.listViewDevices);
            deviceList.setAdapter(mArrayAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_menu);

        mArrayAdapter = new DeviceArrayAdapter(this);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported), Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivity(discoverableIntent);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(new MyBluetoothDevice(device.getName(), device.getAddress(), true));
            }
        }

        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        Button b = (Button) findViewById(R.id.button_refresh);
        b.setOnClickListener(this);

        ListView deviceList = (ListView) findViewById(R.id.listViewDevices);
        deviceList.setAdapter(mArrayAdapter);
        deviceList.setOnItemClickListener(this);

        if (mBluetoothAdapter.isEnabled()) {
            service = BluetoothService.getInstance(this, mHandler);
            service.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            this.finish();
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            if (mBluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        mArrayAdapter.add(new MyBluetoothDevice(device.getName(), device.getAddress(), true));
                    }
                }

                service = BluetoothService.getInstance(this, mHandler);
                service.start();
            }
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivity(discoverableIntent);
        }
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_refresh: {
                mBluetoothAdapter.cancelDiscovery();
                mArrayAdapter.clear();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        mArrayAdapter.add(new MyBluetoothDevice(device.getName(), device.getAddress(), true));
                    }
                }
                mBluetoothAdapter.startDiscovery();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> _parent, View _view, int _pos, long _id) {
        MyBluetoothDevice device = (MyBluetoothDevice) _parent.getAdapter().getItem(_pos);
        connectingDialog = new ProgressDialog(this);
        connectingDialog.setTitle(getString(R.string.connecting));
        connectingDialog.setMessage(getString(R.string.connecting_to) + " " + device.getName());
        connectingDialog.show();
        service.connect(device);
    }
}