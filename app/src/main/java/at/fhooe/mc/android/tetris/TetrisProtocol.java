package at.fhooe.mc.android.tetris;

import java.io.Serializable;

/**
 * Class, which contains every information which is needed for bluetooth transmission
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

    // different Constructors for every type of message

    /**
     * Constructor for sending a toast to an other device
     * @param toast String for the toast-message
     */
    public TetrisProtocol(String toast) {
        this.toast = toast;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }

    /**
     * Constructor to define the next Tetromino
     * @param tetromino int-value that describes a certain Tetromino
     */
    public TetrisProtocol(int tetromino) {
        this.tetromino = tetromino;
        this.acknowledgement = -1;
    }

    /**
     * Constructor to send information
     * @param value to set boolean variable
     * @param id to define, which information
     */
    public TetrisProtocol(boolean value, int id) {
        if (id == REQUEST) {
            this.tetrominoRequest = value;
        }
        this.acknowledgement = -1;
        this.tetromino = -1;
    }

    /**
     * Constructor to send information
     * @param value to set int variable
     * @param id to define, which information
     */
    public TetrisProtocol(int value, int id) {
        if (id == ACK) {
            this.acknowledgement = value;
        } else if (id == READY) {
            this.ready = value;
            this.acknowledgement = -1;
        }

        this.tetromino = -1;
    }

    /**
     * Constructor to send a toast, if one player is game over
     * @param toast String for the toast-message
     * @param score final score
     * @param gameOver
     */
    public TetrisProtocol(String toast, int score, boolean gameOver) {
        this.toast = toast;
        this.score = score;
        this.gameOver = gameOver;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }

    /**
     * Constructor to send a toast, if player wants revenge or not
     * @param toast String for the toast-message
     * @param revenge int-value: 0-> default, if both players aren't game over yet, 1-> no revenge, 2-> opponent wants revenge
     */
    public TetrisProtocol(String toast, int revenge){
        this.toast = toast;
        this.revenge = revenge;
        this.tetromino = -1;
        this.acknowledgement = -1;
    }
}
