package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, View.OnTouchListener {

    private static final String TAG = "Tetris";
    GestureDetector mDetector;
    private SurfaceHolder mHolder;
    SurfaceView background;
    FrameLayout mFrame;
    Timer timer;
    TimerTask timerTask;
    Pixel[][] pixels = new Pixel[20][10];
    int timerRunning = 0;
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

        setContentView(R.layout.activity_main);

        mFrame = (FrameLayout) findViewById(R.id.frame);
        background = (SurfaceView) findViewById(R.id.background);

        SurfaceHolder sh = background.getHolder();
        sh.addCallback(this);

        background.setOnClickListener(this);
        background.setOnTouchListener(this);

        Button b = (Button) findViewById(R.id.button_left);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_right);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_spin);
        b.setOnClickListener(this);

        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {return false;}

            @Override
            public void onShowPress(MotionEvent e) {}

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return false;}

            @Override
            public void onLongPress(MotionEvent e) {}

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, "FLIIIIIIIINGGGGGGG " );

                if (timer != null) {
                    timer.cancel();
                    timer.purge();

                    initTimerTask();

                    timer = new Timer();

                    timer.schedule(timerTask, 0, 50);
                    Log.i(TAG, "new Timer 2 (fling)");
                    timerRunning = 2;
                }

                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunning == 1 || timerRunning == 2) {
            timer.cancel();
            timerRunning = 0;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "on touch event!!");

        if (timerRunning == 0) {
            startGame();
        }

        mDetector.onTouchEvent(event);
        return true;
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
                if (timerRunning == 0) {
                    startGame();
                }
            }
            break;
            case R.id.button_left: {
                if (timerRunning != 0) {
                    Log.i(TAG, "Move left...");
                    moveLeft();
                    refreshDisplay();
                } else {
                    startGame();
                }
            }
            break;
            case R.id.button_right: {
                if (timerRunning != 0) {
                    Log.i(TAG, "Move right...");
                    moveRight();
                    refreshDisplay();
                } else {
                    startGame();
                }
            }
            break;
            case R.id.button_spin: {
                if (timerRunning != 0) {
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
        initTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 1000, 500);
        Log.i(TAG, "new Timer 1 (startGame)");
        timerRunning = 1;

//                new TimerTask() {
//            @Override
//            public void run() {
//                Log.i(TAG, "TimerTask started...");
//                refreshDisplay();
//                moveDown();
//            }
//        }, 1000, 500);

        newTetromino();

    }

    public void initTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "TimerTask started...");
                refreshDisplay();
                moveDown();
            }
        };
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
            clearFullLines();
            if (newTetromino()) {
                // game Over
                gameOverTasks();
            }
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
        return new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    }

    private void setTetromino(int[][] t, int color) {
        for (int i = 0; i < 4; i++) {
            pixels[t[i][0]][t[i][1]].color = color;
        }
    }

    /**
     * Check if the new tetromino can be placed successfully. Has to be inside the surface and
     * the pixels might not be used be other tetrominos.
     *
     * @param t pixels of new tetromino
     * @return 0...possible, 1...impossible (left), 2...impossible (right), 3...impossible
     */
    private int checkIfPositionAvailable(int[][] t) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

                    if (checkIfPositionAvailable(newT) == 0) {
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

    private void clearFullLines() {
        boolean fullLine;

        for (int row = 0; row < pixels.length; row++) {
            fullLine = true;
            for (int col = 0; col < pixels[row].length; col++) {
                if (!pixels[row][col].fixed) {
                    fullLine = false;
                }
            }

            if (fullLine) {
                clearLineNumber(row);
                // TODO count score
            }
        }

    }

    private void clearLineNumber(int row) {
        // move down lines above
        for (; row > 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                pixels[row][col].color = pixels[row - 1][col].color;
                pixels[row][col].fixed = pixels[row - 1][col].fixed;
            }
        }
        // clear line number 0
        for (int col = 0; col < pixels[0].length; col++) {
            pixels[0][col].color = Pixel.COLOR_CLEAR;
        }

    }

    public boolean newTetromino() {
        tetrominoID = (int) (Math.random() * 7);
        spinned = 0;
        int[][] newT = new int[4][];
        boolean gameOver = true;

        if (timerRunning == 2) {
            timer.cancel();
            timer.purge();

            initTimerTask();

            timer = new Timer();

            timer.schedule(timerTask, 1000, 500);
            Log.i(TAG, "new Timer 1 (newTetromino)");
            timerRunning = 1;

        }

        switch (tetrominoID) {
            case TETROMINO_O: {
                newT[0] = new int[]{0, 4};
                newT[1] = new int[]{0, 5};
                newT[2] = new int[]{1, 4};
                newT[3] = new int[]{1, 5};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_O);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_I: {
                newT[0] = new int[]{0, 3};
                newT[1] = new int[]{0, 4};
                newT[2] = new int[]{0, 5};
                newT[3] = new int[]{0, 6};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_I);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_L: {
                newT[0] = new int[]{0, 4};
                newT[1] = new int[]{1, 4};
                newT[2] = new int[]{2, 4};
                newT[3] = new int[]{2, 5};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_L);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_J: {
                newT[0] = new int[]{0, 5};
                newT[1] = new int[]{1, 5};
                newT[2] = new int[]{2, 5};
                newT[3] = new int[]{2, 4};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_J);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_S: {
                newT[0] = new int[]{1, 4};
                newT[1] = new int[]{1, 5};
                newT[2] = new int[]{0, 5};
                newT[3] = new int[]{0, 6};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_S);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_Z: {
                newT[0] = new int[]{0, 4};
                newT[1] = new int[]{0, 5};
                newT[2] = new int[]{1, 5};
                newT[3] = new int[]{1, 6};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_Z);
                    gameOver = false;
                }
            }
            break;
            case TETROMINO_T: {
                newT[0] = new int[]{1, 4};
                newT[1] = new int[]{1, 5};
                newT[2] = new int[]{1, 6};
                newT[3] = new int[]{0, 5};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, COLOR_T);
                    gameOver = false;
                }
            }
            break;
        }

        return gameOver;

    }

    public void gameOverTasks() {
        timer.cancel();
        timer.purge();
        timerRunning = 0;

        // TODO save score in highscore table

        DialogFragment dialog = new RestartDialog();
        dialog.show(getFragmentManager(), "restart_dialog");
    }
}