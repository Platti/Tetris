package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Platti on 20.05.2015.
 */

public class TetrisMediaPlayer extends MediaPlayer {

    private static TetrisMediaPlayer player;
    private boolean stop;

    public static TetrisMediaPlayer getInstance(Context context, int resid) {
        if (player == null) {
            player = (TetrisMediaPlayer) TetrisMediaPlayer.create(context, resid);
            player.setLooping(true);
        }
        return player;
    }

    public void changeMenu() {
        stop = false;
    }

    @Override
    public void stop() throws IllegalStateException {
        if (stop) {
            player = null;
            super.stop();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (!isPlaying()) {
            super.start();
        }
    }
}
