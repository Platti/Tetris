package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
    boolean opponentGameOver;
    boolean myGameOver;
    boolean opponentReady;
    boolean waitingForOpponent;
    int opponentScore;
    DialogFragment dialog;

    ArrayList<Integer> nextTetrominos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isServer = getIntent().getBooleanExtra("server", false);
        nextTetrominos = new ArrayList<Integer>();
        mHandler = new TetrisHandler(this, nextTetrominos);
        mService = BluetoothService.getInstance(this, mHandler);

        if (!isServer) {
            mService.write(new TetrisProtocol(1, TetrisProtocol.READY));
        }
    }

    @Override
    public void startGame() {
        if (!myGameOver) {
            if (isServer) {
                if (opponentReady) {
                    initDisplay();
                    score = 0;
                    level = 0;
                    numberOfLinesCleared = 0;
                    initTimerTask();
                    timer = new Timer();
                    timer.schedule(timerTask, 1000, 300);
                    Log.i(TAG, "new Timer 1 (startGame)");
                    timerRunning = 1;

                    fillTetrominoArray(false);
                } else {
                    Toast.makeText(this, "Opponent not ready!", Toast.LENGTH_SHORT).show();
                    mService.write(new TetrisProtocol(2, TetrisProtocol.READY));
                }

            } else {
                Toast.makeText(this, "Opponent has to start game!", Toast.LENGTH_LONG).show();
                mService.write(new TetrisProtocol("Please start the game!"));
            }
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
        fillTetrominoArray(false);
    }

    public void fillTetrominoArray(boolean force) {
        if (isServer || opponentGameOver) {
            int id;
            if (nextTetrominos.size() < 5 || force) {
                id = (int) (Math.random() * 7);
                nextTetrominos.add(id);
                Log.i(TAG, "sent tetromino ID: " + id);
                mService.write(new TetrisProtocol(id));
            }
        } else {
            if (nextTetrominos.size() < 3) {
                mService.write(new TetrisProtocol(true, TetrisProtocol.REQUEST));
            }
        }
    }

    @Override
    public void gameOverTasks() {
        timer.cancel();
        timer.purge();
        timerRunning = 0;

        myGameOver = true;
        waitingForOpponent = true;

        Log.i(TAG, "save score in highscore table...");
        storeHighscore();

        if (!opponentGameOver) {
            mService.write(new TetrisProtocol(mService.mBluetoothAdapter.getName() + " finished with " + score + " points!", score, true));

            DialogFragment waitingDialog = new DialogFragment(){
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Waiting for opponent to finish the game.");
                    return builder.create();
                }
            };
            waitingDialog.show(getFragmentManager(), "waiting_dialog");

        } else {
            mService.write(new TetrisProtocol(null, score, true));
            showRevengeDialog();
        }
    }

    public void showRevengeDialog() {
        dialog = new RevengeDialog();
        Bundle args = new Bundle();
        args.putInt("myScore", score);
        args.putInt("opponentScore", opponentScore);
        args.putBoolean("server", isServer);
        dialog.setArguments(args);
        waitingForOpponent = false;
        try{
            dialog.show(getFragmentManager(), "revenge_dialog");
        } catch (IllegalStateException ex){
            Log.e(TAG,"Activity already destroyed");
        }
    }

    @Override
    protected void onDestroy() {
        if(!myGameOver || waitingForOpponent) {
            mService.write(new TetrisProtocol("Opponent backed out!", -1, true));
        }
        super.onDestroy();
    }
}

