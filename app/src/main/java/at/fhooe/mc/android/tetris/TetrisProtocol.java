package at.fhooe.mc.android.tetris;

import java.io.Serializable;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisProtocol implements Serializable {

    public int tetromino = -1;
    public boolean tetrominoRequest;
    public boolean gameOver;
    public int ready = 0;
    public int acknowledgement = -1;
    public String toast;
    public int score = 0;
    public int revenge = 0;

    public static final int REQUEST = 0;
    public static final int ACK = 1;
    public static final int GAME_OVER = 2;
    public static final int REVENGE = 3;
    public static final int READY = 4;


    public TetrisProtocol(String toast) {
        this.toast = toast;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }

    public TetrisProtocol(int tetromino) {
        this.tetromino = tetromino;
        this.acknowledgement = -1;
    }

    public TetrisProtocol(boolean value, int id) {
        if (id == REQUEST) {
            this.tetrominoRequest = value;
        }
        this.acknowledgement = -1;
        this.tetromino = -1;
    }

    public TetrisProtocol(int value, int id) {
        if (id == ACK) {
            this.acknowledgement = value;
        } else if (id == READY) {
            this.ready = value;
            this.acknowledgement = -1;
        }

        this.tetromino = -1;
    }

    public TetrisProtocol(String toast, int score, boolean gameOver) {
        this.toast = toast;
        this.score = score;
        this.gameOver = gameOver;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }

    public TetrisProtocol(String toast, int revenge){
        this.toast = toast;
        this.revenge = revenge;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }
}
