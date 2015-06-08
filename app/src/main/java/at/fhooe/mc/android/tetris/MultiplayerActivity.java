package at.fhooe.mc.android.tetris;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

public class MultiplayerActivity extends MainActivity {

    TetrisHandler mHandler;
    BluetoothService mService;
    boolean isServer;

    ArrayList<Integer> nextTetrominos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isServer = getIntent().getBooleanExtra("server", false);
        nextTetrominos = new ArrayList<Integer>();
        mHandler = new TetrisHandler(this, nextTetrominos);
        mService = BluetoothService.getInstance(this, mHandler);
    }

    @Override
    public void startGame() {
        if (isServer) {
            initDisplay();
            score = 0;
            level = 0;
            numberOfLinesCleared = 0;
            initTimerTask();
            timer = new Timer();
            timer.schedule(timerTask, 1000, 300);
            Log.i(TAG, "new Timer 1 (startGame)");
            timerRunning = 1;

            fillTetrominoArray();


            nextTetromino();
            newTetromino();
        } else {
            Toast.makeText(this, "Opponent has to start game!", Toast.LENGTH_LONG).show();
            mService.write(new TetrisProtocol("Please start the game!"));
        }
    }

    public void startGameClient() {
        initDisplay();
        score = 0;
        level = 0;
        numberOfLinesCleared = 0;
        initTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 1000, 300);
        Log.i(TAG, "new Timer 1 (startGame)");
        timerRunning = 1;

        nextTetromino();
        newTetromino();
    }

    @Override
    public void nextTetromino() {
        super.nextTetromino(nextTetrominos.get(0));
        nextTetrominos.remove(0);
        fillTetrominoArray();
    }

    public void fillTetrominoArray() {
        if (isServer) {
            int id;
//            do {
            id = (int) (Math.random() * 7);
            nextTetrominos.add(id);
            Log.i(TAG, "sent tetromino ID: " + id);
            mService.write(new TetrisProtocol(id));
//            } while (nextTetrominos.size() < 5);
        } else {
//            if (nextTetrominos.size() < 5) {
            mService.write(new TetrisProtocol(true, TetrisProtocol.REQUEST));
//            }
        }
    }
}
