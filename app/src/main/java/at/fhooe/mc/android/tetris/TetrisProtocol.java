package at.fhooe.mc.android.tetris;

import java.io.Serializable;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisProtocol implements Serializable {

    public int tetromino;
    public boolean tetrominoRequest;
    public boolean gameOver;
    public boolean startGame;
    public String toast;

    public static final int REQUEST = 0;
    public static final int START_GAME = 1;


    public TetrisProtocol(String toast){
        this.toast = toast;
        this.tetromino = -1;
    }

    public TetrisProtocol(int tetromino){
        this.tetromino = tetromino;
    }

    public TetrisProtocol(boolean value, int id){
        if(id==REQUEST){
            this.tetrominoRequest = value;
        } else if (id == START_GAME){
            this.startGame = value;
        }

        this.tetromino = -1;
    }
}
