package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class TetrisMediaPlayer {

    private static TetrisMediaPlayer instance;
    private MediaPlayer player;
    private boolean stop;
    private int resid;
    private static final String TAG = "Tetris Media Player";

    private TetrisMediaPlayer(Context context, int resid) {
        player = MediaPlayer.create(context, resid);
        player.setLooping(true);
    }

    public static TetrisMediaPlayer getInstance(Context context, int resid) {
        if (instance != null && instance.resid != resid) {
            instance.player.stop();
            instance.player = null;
            instance = null;
        }
        if (instance == null) {
            instance = new TetrisMediaPlayer(context, resid);
            instance.resid = resid;
            Log.i(TAG, "Returning new instance");
        } else {
            Log.i(TAG, "Returning existing instance");
        }
        instance.stop = true;
        return instance;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
        Log.i(TAG, "Setting stop");
    }

    public void stop() throws IllegalStateException {
        if (stop && player != null && instance != null) {
            player.pause();
            Log.i(TAG, "Stopping player");
        } else {
            Log.i(TAG, "Stopping player not allowed");
        }
    }

    public void destroy() {
        if (!player.isPlaying()) {
            player = null;
            instance = null;
            Log.i(TAG, "Destroy player");
        } else {
            Log.i(TAG, "Destroy player not allowed");
        }

    }

    public void destroy(int resid) {
        if (!player.isPlaying() && resid == this.resid) {
            player = null;
            instance = null;
            Log.i(TAG, "Destroy player");
        } else {
            Log.i(TAG, "Destroy player not allowed");
        }

    }

    public void start(boolean fromBeginning) throws IllegalStateException {
        if (fromBeginning) {
            player.seekTo(0);
        }
        if (!player.isPlaying()) {
            player.start();
            Log.i(TAG, "Starting player");
        } else {
            Log.i(TAG, "Starting player but its already running");
        }
    }
}
