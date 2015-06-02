package at.fhooe.mc.android.tetris;

import java.io.Serializable;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisProtocol implements Serializable {

    public int tetromino;
    public boolean tetrominoRequest;
    public boolean gameOver;
    public String toast;

    public TetrisProtocol(String toast){
        this.toast = toast;
        this.tetromino = -1;
    }

    public TetrisProtocol(int tetromino){
        this.tetromino = tetromino;
    }
}
