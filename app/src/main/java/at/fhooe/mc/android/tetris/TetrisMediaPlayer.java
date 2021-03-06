package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * MediaPlayer to regulate the sound of the Tetris-App
 */
public class TetrisMediaPlayer {

    private static TetrisMediaPlayer instance;
    private MediaPlayer player;
    private boolean stop;
    private int resid;
    private static final String TAG = "Tetris Media Player";

    /**
     * Constructor to initialize new instance
     * @param context The UI Activity Context
     * @param resid
     */
    private TetrisMediaPlayer(Context context, int resid) {
        player = MediaPlayer.create(context, resid);
        player.setLooping(true);
    }

    /**
     * creates or returns a Singelton-Object of these class
     * @param context The UI Activity Context
     * @param resid
     * @return the singelton-Object of these class
     */
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

    /**
     * Sets the variable stop (boolean) true, if player should be stopped
     * @param stop defines if player is allowed to stop (ture), or not (false)
     */
    public void setStop(boolean stop) {
        this.stop = stop;
        Log.i(TAG, "Setting stop");
    }

    /**
     * Stops the media-player, if the variable stop is true.
     */
    public void stop() throws IllegalStateException {
        if (stop && player != null && instance != null) {
            player.pause();
            Log.i(TAG, "Stopping player");
        } else {
            Log.i(TAG, "Stopping player not allowed");
        }
    }

    /**
     * Destroys media-player if it isn't playing
     */
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

    /**
     * Media-player starts to play sound
     * @param fromBeginning defines if player should start sound from begin or from the current position
     * @throws IllegalStateException
     */
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
