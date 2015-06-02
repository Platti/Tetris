package at.fhooe.mc.android.tetris;

import android.os.Bundle;
import android.os.PersistableBundle;
import java.util.ArrayList;

public class MultiplayerActivity extends MainActivity {

    TetrisHandler mHandler;
    BluetoothService mService;
    boolean isServer;

    ArrayList<Integer> nextTetrominos;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        isServer = getIntent().getBooleanExtra("server", false);
        mHandler = new TetrisHandler(this, nextTetrominos);
        mService = BluetoothService.getInstance(this, mHandler);
        nextTetrominos = new ArrayList<Integer>();

        fillTetrominoArray();

    }

    @Override
    public void nextTetromino() {
        super.nextTetromino(nextTetrominos.get(0));
        nextTetrominos.remove(0);
        fillTetrominoArray();
    }

    public void fillTetrominoArray() {
        if(isServer){
            int id;
            while (nextTetrominos.size() < 5) {
                id = (int) (Math.random() * 7);
                nextTetrominos.add(id);
                mService.write(new TetrisProtocol(id));
            }
        } else {
            // neue tetrominos anfordern
        }
    }
}
