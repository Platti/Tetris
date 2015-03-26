package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "Tetris";
    private SurfaceHolder mHolder;
    SurfaceView background;
    FrameLayout mFrame;
    Timer timer = new Timer();
    Pixel[][] pixels = new Pixel[20][10];
    boolean timerRunning = false;
    int tetrominoID;
    int spinned = 0;

    private static final int TETROMINO_O = 0;
    private static final int TETROMINO_I = 1;
    private static final int TETROMINO_L = 2;
    private static final int TETROMINO_J = 3;
    private static final int TETROMINO_S = 4;
    private static final int TETROMINO_Z = 5;
    private static final int TETROMINO_T = 6;

    private static final int COLOR_O = Color.RED;
    private static final int COLOR_I = Color.GREEN;
    private static final int COLOR_L = Color.YELLOW;
    private static final int COLOR_J = Color.BLUE;
    private static final int COLOR_S = Color.MAGENTA;
    private static final int COLOR_Z = Color.CYAN;
    private static final int COLOR_T = Color.GRAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set Fullscreen
        Window win = getWindow();
        win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getActionBar();
        bar.hide();

        //some Changes!!!
        //hahahaha Sven Sven Sven was here
        //blablablab passwort?

        setContentView(R.layout.activity_main);

        mFrame = (FrameLayout) findViewById(R.id.frame);
        background = (SurfaceView) findViewById(R.id.background);

        SurfaceHolder sh = background.getHolder();
        sh.addCallback(this);

        background.setOnClickListener(this);

        Button b = (Button) findViewById(R.id.button_left);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_right);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_spin);
        b.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunning) {
            timer.cancel();
            timerRunning = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(height / 2, height);
        params.gravity = Gravity.CENTER;
        mFrame.setLayoutParams(params);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.background: {
                if (!timerRunning) {
                    startGame();
                }
            }
            break;
            case R.id.button_left: {
                if (timerRunning) {
                    Log.i(TAG, "Move left...");
                    moveLeft();
                    refreshDisplay();
                } else {
                    startGame();
                }
            }
            break;
            case R.id.button_right: {
                if (timerRunning) {
                    Log.i(TAG, "Move right...");
                    moveRight();
                    refreshDisplay();
                } else {
                    startGame();
                }
            }
            break;
            case R.id.button_spin: {
                if (timerRunning) {
                    Log.i(TAG, "Spin Tetromino...");
                    spin();
                    refreshDisplay();
                } else {
                    startGame();
                }
            }
            break;
        }

    }

    public void startGame() {
        initDisplay();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "TimerTask started...");
                refreshDisplay();
                moveDown();
            }
        }, 1000, 500);

        newTetromino();
        timerRunning = true;
    }

    public void initDisplay() {
        float pixelWidth = background.getWidth() / 10;
        float pixelHeight = background.getHeight() / 20;

        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[row].length; col++) {
                pixels[row][col] = new Pixel(new RectF(col * pixelWidth + 1, row * pixelHeight + 1, (col + 1) * pixelWidth - 1, (row + 1) * pixelHeight - 1));
            }
        }

        refreshDisplay();
    }

    private boolean moveDownPossible() {
        for (int row = pixels.length - 1; row >= 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                    if (row < pixels.length - 1 && pixels[row + 1][col].fixed) {
                        return false;
                    }
                    if (row == pixels.length - 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void moveDown() {
        if (moveDownPossible()) {
            for (int row = pixels.length - 2; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                        pixels[row + 1][col].color = pixels[row][col].color;
                        pixels[row][col].color = Pixel.COLOR_CLEAR;
                    }
                }
            }
        } else {
            for (int row = pixels.length - 1; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    if (pixels[row][col].color != Pixel.COLOR_CLEAR) {
                        pixels[row][col].fixed = true;
                    }
                }
            }
            newTetromino();
        }
    }

    private boolean moveLeftPossible() {
        for (int col = 0; col < pixels[0].length; col++) {
            for (int row = pixels.length - 1; row >= 0; row--) {
                if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                    if (col > 0 && pixels[row][col - 1].fixed) {
                        return false;
                    }
                    if (col <= 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void moveLeft() {
        if (moveLeftPossible()) {
            for (int col = 1; col < pixels[0].length; col++) {
                for (int row = pixels.length - 1; row >= 0; row--) {
                    if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                        pixels[row][col - 1].color = pixels[row][col].color;
                        pixels[row][col].color = Pixel.COLOR_CLEAR;
                    }
                }
            }
        }
    }

    private boolean moveRightPossible() {
        for (int col = pixels[0].length - 1; col >= 0; col--) {
            for (int row = pixels.length - 1; row >= 0; row--) {
                if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                    if (col < pixels[0].length - 1 && pixels[row][col + 1].fixed) {
                        return false;
                    }
                    if (col >= pixels[0].length - 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void moveRight() {
        if (moveRightPossible()) {
            for (int col = pixels[0].length - 2; col >= 0; col--) {
                for (int row = pixels.length - 1; row >= 0; row--) {
                    if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                        pixels[row][col + 1].color = pixels[row][col].color;
                        pixels[row][col].color = Pixel.COLOR_CLEAR;
                    }
                }
            }
        }
    }

    private int[] getTetromino() {
        for (int row = pixels.length - 1; row >= 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                if (pixels[row][col].color != Pixel.COLOR_CLEAR && !pixels[row][col].fixed) {
                    int[] i = {row, col};
                    return i;
                }
            }
        }
        return null;
    }

    private void setTetromino(int[][] t, int color) {
        for (int i = 0; i < 4; i++) {
            pixels[t[i][0]][t[i][1]].color = color;
        }
    }

    /**
     * @param t pixels of new tetromino
     * @return 0...spin posible, 1...impossible (left), 2...impossible (right), 3...impossible
     */
    private int spinPossible(int[][] t) {
        int code = 0;
        for (int i = 0; i < 4; i++) {
            if (code < 1 && t[i][1] < 0) code = 1;
            if (code < 2 && t[i][1] >= pixels[0].length) code = 2;
            if (t[i][0] >= pixels.length || t[i][0] < 0) return 3;

            if (t[i][1] >= 0 && t[i][1] < pixels[0].length && pixels[t[i][0]][t[i][1]].fixed) {
                return 3;
            }
        }
        return code;
    }

    public void spin() {
        switch (tetrominoID) {
            case TETROMINO_I: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0];
                    oldT[2][1] = pos[1] + 2;
                    oldT[3][0] = pos[0];
                    oldT[3][1] = pos[1] + 3;

                    newT[0][0] = pos[0] - 1;
                    newT[0][1] = pos[1] + 2;
                    newT[1] = oldT[2];
                    newT[2][0] = pos[0] + 1;
                    newT[2][1] = pos[1] + 2;
                    newT[3][0] = pos[0] + 2;
                    newT[3][1] = pos[1] + 2;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_I);
                        spinned = 1;
                    }


                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 2;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 3;
                    oldT[3][1] = pos[1];

                    newT[0][0] = pos[0] - 2;
                    newT[0][1] = pos[1] - 2;
                    newT[1][0] = pos[0] - 2;
                    newT[1][1] = pos[1] - 1;
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_I);
                        spinned = 0;
                    }

                }
            }
            break;
            case TETROMINO_L: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1];

                    newT[0][0] = pos[0];
                    newT[0][1] = pos[1] - 1;
                    newT[1][0] = pos[0] - 1;
                    newT[1][1] = pos[1] - 1;
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_L);
                        spinned = 1;
                    }
                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] + 1;
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 2;

                    newT[0][0] = pos[0];
                    newT[0][1] = pos[1] + 1;
                    newT[1] = oldT[2];
                    newT[2][0] = pos[0] - 2;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1];

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_L);
                        spinned = 2;
                    }
                } else if (spinned == 2) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 2;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1] - 1;

                    newT[0][0] = pos[0] - 1;
                    newT[0][1] = pos[1] - 1;
                    newT[1] = oldT[1];
                    newT[2][0] = pos[0] - 1;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_L);
                        spinned = 3;
                    }
                } else if (spinned == 3) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0];
                    oldT[2][1] = pos[1] + 2;
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 2;

                    newT[0][0] = pos[0] - 1;
                    newT[0][1] = pos[1] + 1;
                    newT[1] = oldT[1];
                    newT[2][0] = pos[0] + 1;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0] + 1;
                    newT[3][1] = pos[1] + 2;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_L);
                        spinned = 0;
                    }
                }

            }
            break;
            case TETROMINO_J: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] + 1;
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1] + 1;

                    newT[0][0] = pos[0] - 2;
                    newT[0][1] = pos[1];
                    newT[1][0] = pos[0] - 1;
                    newT[1][1] = pos[1];
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] + 2;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_J);
                        spinned = 1;
                    }
                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0];
                    oldT[2][1] = pos[1] + 1;
                    oldT[3][0] = pos[0];
                    oldT[3][1] = pos[1] + 2;

                    newT[0][0] = pos[0] + 1;
                    newT[0][1] = pos[1] + 1;
                    newT[1] = oldT[2];
                    newT[2][0] = pos[0] - 1;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] + 2;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_J);
                        spinned = 2;
                    }
                } else if (spinned == 2) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 2;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1] + 1;

                    newT[0][0] = pos[0] - 1;
                    newT[0][1] = pos[1] - 1;
                    newT[1] = oldT[1];
                    newT[2][0] = pos[0] - 1;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0];
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_J);
                        spinned = 3;
                    }
                } else if (spinned == 3) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] - 1;
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] - 2;

                    newT[0][0] = pos[0];
                    newT[0][1] = pos[1] - 2;
                    newT[1][0] = pos[0];
                    newT[1][1] = pos[1] - 1;
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1] - 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_J);
                        spinned = 0;
                    }
                }
            }
            break;
            case TETROMINO_S: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] + 1;
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 2;

                    newT[0][0] = pos[0] - 2;
                    newT[0][1] = pos[1];
                    newT[1][0] = pos[0] - 1;
                    newT[1][1] = pos[1];
                    newT[2] = oldT[2];
                    newT[3] = oldT[1];

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_S);
                        spinned = 1;
                    }
                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] - 1;
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1] - 1;

                    newT[0][0] = pos[0];
                    newT[0][1] = pos[1] - 1;
                    newT[1] = pos;
                    newT[2] = oldT[1];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_S);
                        spinned = 0;
                    }

                }
            }
            break;
            case TETROMINO_Z: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] - 1;

                    newT[0] = pos;
                    newT[1] = oldT[2];
                    newT[2][0] = pos[0] - 1;
                    newT[2][1] = pos[1] + 1;
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_Z);
                        spinned = 1;
                    }
                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1] + 1;
                    oldT[3][0] = pos[0] - 2;
                    oldT[3][1] = pos[1] + 1;

                    newT[0][0] = pos[0];
                    newT[0][1] = pos[1] + 1;
                    newT[1] = pos;
                    newT[2] = oldT[1];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] - 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_Z);
                        spinned = 0;
                    }

                }
            }
            break;
            case TETROMINO_T: {
                if (spinned == 0) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0];
                    oldT[1][1] = pos[1] + 1;
                    oldT[2][0] = pos[0];
                    oldT[2][1] = pos[1] + 2;
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 1;

                    newT[0] = oldT[3];
                    newT[1] = oldT[1];
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] + 1;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_T);
                        spinned = 1;
                    }
                } else if (spinned == 1) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 2;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 1;

                    newT[0] = oldT[0];
                    newT[1] = oldT[1];
                    newT[2] = oldT[3];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] - 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_T);
                        spinned = 2;
                    }
                } else if (spinned == 2) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1] - 1;
                    oldT[2][0] = pos[0] - 1;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] + 1;

                    newT[0] = oldT[0];
                    newT[1] = oldT[1];
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 2;
                    newT[3][1] = pos[1];

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_T);
                        spinned = 3;
                    }
                } else if (spinned == 3) {
                    int[] pos = getTetromino();
                    int[][] oldT = new int[4][2];
                    int[][] newT = new int[4][2];

                    oldT[0] = pos;
                    oldT[1][0] = pos[0] - 1;
                    oldT[1][1] = pos[1];
                    oldT[2][0] = pos[0] - 2;
                    oldT[2][1] = pos[1];
                    oldT[3][0] = pos[0] - 1;
                    oldT[3][1] = pos[1] - 1;

                    newT[0] = oldT[3];
                    newT[1] = oldT[1];
                    newT[2] = oldT[2];
                    newT[3][0] = pos[0] - 1;
                    newT[3][1] = pos[1] + 1;

                    if (spinPossible(newT) == 0) {
                        setTetromino(oldT, Pixel.COLOR_CLEAR);
                        setTetromino(newT, COLOR_T);
                        spinned = 0;
                    }
                }
            }
            break;
        }
    }

    public void refreshDisplay() {
        if (mHolder != null) {
            Canvas c = mHolder.lockCanvas();
            c.drawColor(Color.LTGRAY);
            Paint p = new Paint();

            for (int row = pixels.length - 1; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    p.setColor(pixels[row][col].color);
                    c.drawRoundRect(pixels[row][col].rect, 8, 8, p);
                }
            }
            mHolder.unlockCanvasAndPost(c);
        }
    }

    public void newTetromino() {
        tetrominoID = (int) (Math.random() * 7);
        spinned = 0;

        // TODO schaun obs no passt

        switch (tetrominoID) {
            case TETROMINO_O: {
                pixels[0][4].color = COLOR_O;
                pixels[0][5].color = COLOR_O;
                pixels[1][4].color = COLOR_O;
                pixels[1][5].color = COLOR_O;
            }
            break;
            case TETROMINO_I: {
                pixels[0][3].color = COLOR_I;
                pixels[0][4].color = COLOR_I;
                pixels[0][5].color = COLOR_I;
                pixels[0][6].color = COLOR_I;
            }
            break;
            case TETROMINO_L: {
                pixels[0][4].color = COLOR_L;
                pixels[1][4].color = COLOR_L;
                pixels[2][4].color = COLOR_L;
                pixels[2][5].color = COLOR_L;
            }
            break;
            case TETROMINO_J: {
                pixels[0][5].color = COLOR_J;
                pixels[1][5].color = COLOR_J;
                pixels[2][5].color = COLOR_J;
                pixels[2][4].color = COLOR_J;
            }
            break;
            case TETROMINO_S: {
                pixels[1][4].color = COLOR_S;
                pixels[1][5].color = COLOR_S;
                pixels[0][5].color = COLOR_S;
                pixels[0][6].color = COLOR_S;
            }
            break;
            case TETROMINO_Z: {
                pixels[0][4].color = COLOR_Z;
                pixels[0][5].color = COLOR_Z;
                pixels[1][5].color = COLOR_Z;
                pixels[1][6].color = COLOR_Z;
            }
            break;
            case TETROMINO_T: {
                pixels[1][4].color = COLOR_T;
                pixels[1][5].color = COLOR_T;
                pixels[1][6].color = COLOR_T;
                pixels[0][5].color = COLOR_T;
            }
            break;
        }

    }
}