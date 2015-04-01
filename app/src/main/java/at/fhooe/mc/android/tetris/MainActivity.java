package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, View.OnTouchListener {

    private static final String TAG = "Tetris";
    GestureDetector mDetector;
    private SurfaceHolder mHolder;
    private SurfaceHolder mHolder2;
    Handler mHandler = new Handler();
    SurfaceView background;
    SurfaceView previewSurface;
    FrameLayout mFrame;
    FrameLayout mFramePreview;
    Timer timer;
    TimerTask timerTask;
    Pixel[][] pixels = new Pixel[20][10];
    Pixel[][] preview = new Pixel[4][3];
    int timerRunning = 0;
    int tetrominoID;
    int nextTetrominoID;
    int spinned = 0;
    int score = 0;
    int level = 0;
    int interScore = 0;
    int numberOfLinesCleared = 0; //lines cleared overall
    int clearedLinesInARow = 0; //lines cleared at once
    Canvas canvasPre;
    TextView textLevel;
    TextView textLines;
    TextView textScore;

    private static final int TETROMINO_O = 0;
    private static final int TETROMINO_I = 1;
    private static final int TETROMINO_L = 2;
    private static final int TETROMINO_J = 3;
    private static final int TETROMINO_S = 4;
    private static final int TETROMINO_Z = 5;
    private static final int TETROMINO_T = 6;

    public static final int COLOR_O = Color.RED;
    public static final int COLOR_I = Color.GREEN;
    public static final int COLOR_L = Color.YELLOW;
    public static final int COLOR_J = Color.BLUE;
    public static final int COLOR_S = Color.MAGENTA;
    public static final int COLOR_Z = Color.CYAN;
    public static final int COLOR_T = Color.GRAY;

    public static final String PREF_NAME = "my_highscores";

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
        mFramePreview = (FrameLayout) findViewById(R.id.frame_preview);
        background = (SurfaceView) findViewById(R.id.background);
        previewSurface = (SurfaceView) findViewById(R.id.surface_next);

        SurfaceHolder sh = background.getHolder();
        sh.addCallback(this);

        background.setOnClickListener(this);
        background.setOnTouchListener(this);

        SurfaceHolder shPre = previewSurface.getHolder();
        shPre.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mHolder2 = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mFramePreview.getWidth(), (int) ((mFramePreview.getWidth() / 3.0) * 4));
                mFramePreview.setLayoutParams(params);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mHolder2 = null;
            }
        });

        Button b = (Button) findViewById(R.id.button_left);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_right);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_spin);
        b.setOnClickListener(this);

        textLevel = (TextView) findViewById(R.id.textView_level);
        textLines = (TextView) findViewById(R.id.textView_lines);
        textScore = (TextView) findViewById(R.id.textView_score);


        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, "FLIIIIIIIINGGGGGGG ");

                if (timerRunning == 1) {

                    if (timer != null) {
                        timer.cancel();
                        timer.purge();

                        initTimerTask();

                        timer = new Timer();

                        timer.schedule(timerTask, 0, 50);
                        Log.i(TAG, "new Timer 2 (fling)");
                        timerRunning = 2;

                        interScore++;

                        score = score + interScore;
                        Log.i(TAG, "score + interscore: " + score);
                    }
                }

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunning != 0) {
            timer.cancel();
            timer.purge();
            timerRunning = 0;
        }
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerRunning != 0) {
            timer.cancel();
            timer.purge();
            timerRunning = 0;
        }
        Log.i(TAG, "onStop");
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height / 2, height);
        params.gravity = Gravity.LEFT;
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
        timer.schedule(timerTask, 1000, 300);
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
//        nextTetromino();
        nextTetrominoID = (int) (Math.random() * 7);
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


        pixelWidth = previewSurface.getWidth() / 3;
        pixelHeight = previewSurface.getHeight() / 4;

        for (int row = 0; row < preview.length; row++) {
            for (int col = 0; col < preview[row].length; col++) {
                preview[row][col] = new Pixel(new RectF(col * pixelWidth + 1, row * pixelHeight + 1, (col + 1) * pixelWidth - 1, (row + 1) * pixelHeight - 1));
            }
        }
        drawPreview();

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

                clearedLinesInARow++;
                numberOfLinesCleared++;

                Log.i(TAG, "numberOfLinesCleared: " + numberOfLinesCleared);
                Log.i(TAG, "clearedLinesInARow: " + clearedLinesInARow);

                if (numberOfLinesCleared % 10 == 0) { //set level after 10 lines cleared
                    level = numberOfLinesCleared / 10;
                }
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

    public void nextTetromino() {
        Log.i(TAG, "surfacePreview nextTetromino....");
        nextTetrominoID = (int) (Math.random() * 7);

        for (int row = 0; row < preview.length; row++) {
            for (int col = 0; col < preview[row].length; col++) {
                preview[row][col].color = Color.LTGRAY;
            }
        }

        switch (nextTetrominoID) {
            case TETROMINO_O: {
                preview[1][1].color = COLOR_O;
                preview[1][2].color = COLOR_O;
                preview[2][1].color = COLOR_O;
                preview[2][2].color = COLOR_O;

                drawPreview();
            }
            break;
            case TETROMINO_I: {
                preview[0][1].color = COLOR_I;
                preview[1][1].color = COLOR_I;
                preview[2][1].color = COLOR_I;
                preview[3][1].color = COLOR_I;

                drawPreview();
            }
            break;
            case TETROMINO_L: {
                preview[0][1].color = COLOR_L;
                preview[1][1].color = COLOR_L;
                preview[2][1].color = COLOR_L;
                preview[2][2].color = COLOR_L;

                drawPreview();
            }
            break;
            case TETROMINO_J: {
                preview[0][2].color = COLOR_J;
                preview[1][2].color = COLOR_J;
                preview[2][2].color = COLOR_J;
                preview[2][1].color = COLOR_J;

                drawPreview();
            }
            break;
            case TETROMINO_S: {
                preview[2][0].color = COLOR_S;
                preview[2][1].color = COLOR_S;
                preview[1][1].color = COLOR_S;
                preview[1][2].color = COLOR_S;

                drawPreview();
            }
            break;
            case TETROMINO_Z: {
                preview[1][0].color = COLOR_Z;
                preview[1][1].color = COLOR_Z;
                preview[2][1].color = COLOR_Z;
                preview[2][2].color = COLOR_Z;

                drawPreview();
            }
            break;
            case TETROMINO_T: {
                preview[2][0].color = COLOR_T;
                preview[2][1].color = COLOR_T;
                preview[2][2].color = COLOR_T;
                preview[1][1].color = COLOR_T;

                drawPreview();
            }
            break;
        }

    }

    public void drawPreview() {
        Log.i(TAG, "surfacePreview draw Preview....");
        if (mHolder2 != null) {
            canvasPre = mHolder2.lockCanvas();
            canvasPre.drawColor(Color.LTGRAY);
            Paint p = new Paint();

            for (int row = preview.length - 1; row >= 0; row--) {
                for (int col = 0; col < preview[row].length; col++) {
                    p.setColor(preview[row][col].color);
                    canvasPre.drawRoundRect(preview[row][col].rect, 4, 4, p);
                }
            }
//            previewSurface.draw(canvasPre);
//            previewSurface.setVisibility(View.VISIBLE);
            mHolder2.unlockCanvasAndPost(canvasPre);
        }
    }

    public boolean newTetromino() {
        //tetrominoID = (int) (Math.random() * 7);
        tetrominoID = nextTetrominoID;
        spinned = 0;
        int[][] newT = new int[4][];
        boolean gameOver = true;

        if (timerRunning == 1) {
            timer.cancel();
            timer.purge();

            initTimerTask();

            timer = new Timer();

            if (level == 10) {
                timer.schedule(timerTask, 0, 100);
            } else {
                timer.schedule(timerTask, 0, (1000 - (level * 100)));
            }

            timerRunning = 1;
            interScore = 0;
        } else if (timerRunning == 2) {
            timer.cancel();
            timer.purge();

            initTimerTask();

            timer = new Timer();

            if (level == 10) {
                timer.schedule(timerTask, 0, 100);
            } else {
                timer.schedule(timerTask, 0, (1000 - (level * 100)));
            }

            timerRunning = 1;
        }

        switch (clearedLinesInARow) {
            case 1: {
                score = score + 40 * (level + 1);
            }
            break;

            case 2: {
                score = score + 100 * (level + 1);
            }
            break;

            case 3: {
                score = score + 300 * (level + 1);
            }
            break;

            case 4: {
                score = score + 1200 * (level + 1);
            }
            break;
        }

        clearedLinesInARow = 0;

        Log.i(TAG, "score newTetromino: " + score);
        Log.i(TAG, "level: " + level);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textLevel.setText(String.valueOf(level));
                textScore.setText(String.valueOf(score));
                textLines.setText(String.valueOf(numberOfLinesCleared));
            }
        });


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

        nextTetromino();
//        textLevel.setText("Level:\n" + level + "\n");
        return gameOver;
    }


    public void storeHighscore() {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int[] highscores = new int[11];
        // get old highscore table
        for (int i = 0; i < 10; i++) {
            highscores[i] = sp.getInt("score" + i, -1);
        }

        // insert latest score
        highscores[10] = score;
        sortIntArray(highscores);

        // save new highscore table
        SharedPreferences.Editor edit = sp.edit();
        for (int i = 0; i < 10; i++) {
            edit.putInt("score" + i, highscores[i]);
        }
        edit.commit();
    }

    private void sortIntArray(int[] array){
        int tempData;
        boolean swapped = true;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < array.length - 1; i++) {
                if (array[i] < array[i + 1]) {
                    tempData = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = tempData;
                    swapped = true;
                }
            }
        }
    }

    public void gameOverTasks() {
        timer.cancel();
        timer.purge();
        timerRunning = 0;

        // TODO save score in highscore table

        Log.i(TAG, "save score in highscore table...");
        storeHighscore();

        DialogFragment dialog = new RestartDialog();
        dialog.show(getFragmentManager(), "restart_dialog");
    }
}