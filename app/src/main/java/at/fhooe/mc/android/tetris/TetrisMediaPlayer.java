package at.fhooe.mc.android.tetris;

import android.media.MediaPlayer;

/**
 * Created by Platti on 20.05.2015.
 */

public class TetrisMediaPlayer extends MediaPlayer {

    private TetrisMediaPlayer player;
    private boolean stop;

    TetrisMediaPlayer(){
        stop = true;
    }

    TetrisMediaPlayer getInstance() {
        if (player == null) {
            player = new TetrisMediaPlayer();
        }
        return player;
    }

    public void changeMenu() {
        stop = false;
    }

    @Override
    public void stop() throws IllegalStateException {
        if (stop) {
            super.stop();
        }
    }
}
