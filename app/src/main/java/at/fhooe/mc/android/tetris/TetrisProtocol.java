package at.fhooe.mc.android.tetris;

import java.io.Serializable;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisProtocol implements Serializable {

    public int tetromino;
    public boolean tetrominoRequest;
    public boolean gameOver;
    public int acknowledgement;
    public String toast;

    public static final int REQUEST = 0;
    public static final int ACK = 1;


    public TetrisProtocol(String toast){
        this.toast = toast;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }

    public TetrisProtocol(int tetromino){
        this.tetromino = tetromino;
        this.acknowledgement = -1;
    }

    public TetrisProtocol(boolean value, int id){
        if(id==REQUEST){
            this.tetrominoRequest = value;
        }
        this.acknowledgement = -1;
        this.tetromino = -1;
    }

    public TetrisProtocol(int value, int id){
       if (id == ACK){
            this.acknowledgement = value;
        }

        this.tetromino = -1;
    }
}
