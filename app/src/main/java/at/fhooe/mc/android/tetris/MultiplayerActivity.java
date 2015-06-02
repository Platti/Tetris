package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;

import at.fhooe.mc.android.tetris.R;

public class MultiplayerActivity extends MainActivity {

    TetrisHandler mHandler;
    BluetoothService mService;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mHandler = new TetrisHandler(this);
        mService = BluetoothService.getInstance(this, mHandler);

    }
}
