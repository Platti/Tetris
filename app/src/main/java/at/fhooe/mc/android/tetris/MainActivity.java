package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;
import com.parse.ParseObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main class for the game in single player mode.
 */
public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, View.OnTouchListener {

    protected static final String TAG = "Tetris";
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
    int pixelCornerRadius;
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
    Button bLeft;
    Button bRight;
    Button bSpin;
    ImageButton ib;
    boolean gameOver = false;
    boolean pause = false;
    TetrisMediaPlayer mediaPlayer;

    private static final int TETROMINO_O = 0;
    private static final int TETROMINO_I = 1;
    private static final int TETROMINO_L = 2;
    private static final int TETROMINO_J = 3;
    private static final int TETROMINO_S = 4;
    private static final int TETROMINO_Z = 5;
    private static final int TETROMINO_T = 6;

    TetrisColor color;

    public static final String PREF_NAME = "my_highscores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

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

        ViewGroup.LayoutParams params;
        bLeft = (Button) findViewById(R.id.button_left);
        bLeft.setOnClickListener(this);
        bRight = (Button) findViewById(R.id.button_right);
        bRight.setOnClickListener(this);
        bSpin = (Button) findViewById(R.id.button_spin);
        bSpin.setOnClickListener(this);

        params = bLeft.getLayoutParams();
        params.width = getWindowManager().getDefaultDisplay().getWidth() / 4;
        params.height = getWindowManager().getDefaultDisplay().getHeight() / 3 * 2;
        bLeft.setLayoutParams(params);

        params = bRight.getLayoutParams();
        params.width = getWindowManager().getDefaultDisplay().getWidth() / 4;
        params.height = getWindowManager().getDefaultDisplay().getHeight() / 3 * 2;
        bRight.setLayoutParams(params);

        params = bSpin.getLayoutParams();
        params.width = getWindowManager().getDefaultDisplay().getWidth() / 3;
        params.height = getWindowManager().getDefaultDisplay().getHeight() / 3;
        bSpin.setLayoutParams(params);


        ib = (ImageButton) findViewById(R.id.button_pause);
        ib.setOnClickListener(this);

        textLevel = (TextView) findViewById(R.id.textView_level);
        textLines = (TextView) findViewById(R.id.textView_lines);
        textScore = (TextView) findViewById(R.id.textView_score);

        textLevel.setText(getString(R.string.level) + "\n" + 0);
        textScore.setText(getString(R.string.score) + "\n" + 0);
        textLines.setText(getString(R.string.lines) + "\n" + 0);

        color = new TetrisColor(this);

        pixelCornerRadius = getWindowManager().getDefaultDisplay().getHeight() / 240;

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
                if (!pause) {
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
//        if (timerRunning != 0) {
//            timer.cancel();
//            timer.purge();
//            timerRunning = 0;
//        }
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        if (timerRunning != 0 && !pause) {
            pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDisplay();
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
            case R.id.button_pause: {
                if (timerRunning != 0) {
                    pause();
                } else {
                    startGame();
                }
            }
            break;
        }
    }

    /**
     * Tasks to start a game.
     */
    public void startGame() {
        if (!gameOver) {
            initDisplay();
            score = 0;
            level = 0;
            numberOfLinesCleared = 0;
            initTimerTask();
            timer = new Timer();
            timer.schedule(timerTask, 1000, 300);
            Log.i(TAG, "new Timer 1 (startGame)");
            timerRunning = 1;

            if (mediaPlayer != null) {
                mediaPlayer.destroy();
            }
            mediaPlayer = TetrisMediaPlayer.getInstance(MainActivity.this, R.raw.game_theme);
            mediaPlayer.start(true);

            nextTetrominoID = (int) (Math.random() * 7);
            newTetromino();
        }
    }

    /**
     * Initialize Timer to move down the new tetromino periodically.
     */
    public void initTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                refreshDisplay();
                moveDown();
            }
        };
    }

    /**
     * Initialize game board (pixels-array) and preview window (preview-array).
     */
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

    /**
     * if game is running: stop timer, disable buttons
     * if game is paused: reinitialize timer, enable buttons
     */
    public void pause() {
        if (!pause) {
            Log.i(TAG, "Pause pressed...");
            timer.cancel();
            timer.purge();
            ib.setImageResource(R.drawable.ic_media_play);
            bLeft.setEnabled(false);
            bRight.setEnabled(false);
            bSpin.setEnabled(false);
            pause = true;
            mediaPlayer.stop();
        } else {
            if (timerRunning == 1) {
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
                initTimerTask();
                timer = new Timer();

                if (level == 10) {
                    timer.schedule(timerTask, 0, 100);
                } else {
                    timer.schedule(timerTask, 0, (1000 - (level * 100)));
                }

                timerRunning = 1;
            }
            ib.setImageResource(R.drawable.ic_media_pause);
            bLeft.setEnabled(true);
            bRight.setEnabled(true);
            bSpin.setEnabled(true);
            pause = false;
            mediaPlayer.start(false);
        }
    }

    /**
     * check if move down of the new tetromino is possible
     *
     * @return is possible
     */
    private boolean moveDownPossible() {
        for (int row = pixels.length - 1; row >= 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
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

    /**
     * move down all pixels that are not fixed and check if game is over or line is full
     */
    public void moveDown() {
        if (moveDownPossible()) {
            for (int row = pixels.length - 2; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
                        pixels[row + 1][col].color = pixels[row][col].color;
                        pixels[row][col].color = color.clear;
                    }
                }
            }
        } else {
            for (int row = pixels.length - 1; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    if (pixels[row][col].color != color.clear) {
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

    /**
     * check if move left of the new tetromino is possible
     *
     * @return is possible
     */
    private boolean moveLeftPossible() {
        for (int col = 0; col < pixels[0].length; col++) {
            for (int row = pixels.length - 1; row >= 0; row--) {
                if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
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

    /**
     * move left all pixels that are not fixed
     */
    public void moveLeft() {
        if (moveLeftPossible()) {
            for (int col = 1; col < pixels[0].length; col++) {
                for (int row = pixels.length - 1; row >= 0; row--) {
                    if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
                        pixels[row][col - 1].color = pixels[row][col].color;
                        pixels[row][col].color = color.clear;
                    }
                }
            }
        }
    }

    /**
     * check if move right of the new tetromino is possible
     *
     * @return is possible
     */
    private boolean moveRightPossible() {
        for (int col = pixels[0].length - 1; col >= 0; col--) {
            for (int row = pixels.length - 1; row >= 0; row--) {
                if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
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

    /**
     * move right all pixels that are not fixed
     */
    public void moveRight() {
        if (moveRightPossible()) {
            for (int col = pixels[0].length - 2; col >= 0; col--) {
                for (int row = pixels.length - 1; row >= 0; row--) {
                    if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
                        pixels[row][col + 1].color = pixels[row][col].color;
                        pixels[row][col].color = color.clear;
                    }
                }
            }
        }
    }

    /**
     * find the new tetromino on the game board
     *
     * @return position of the first pixel found of new tetromino (low / left)
     */
    private int[] getTetromino() {
        for (int row = pixels.length - 1; row >= 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                if (pixels[row][col].color != color.clear && !pixels[row][col].fixed) {
                    int[] i = {row, col};
                    return i;
                }
            }
        }
        return new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    }

    /**
     * Set color of a tetromino
     *
     * @param t     positions of the 4 pixels
     * @param color new color
     */
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

    /**
     * find the new tetromino, calculate position after spin, remove at old position and set at new position
     */
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.i);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.i);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.l);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.l);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.l);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.l);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.j);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.j);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.j);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.j);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.s);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.s);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.z);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.z);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.t);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.t);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.t);
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
                        setTetromino(oldT, color.clear);
                        setTetromino(newT, color.t);
                        spinned = 0;
                    }
                }
            }
            break;
        }
    }

    /**
     * Draw the game board, using the pixels array.
     */
    public void refreshDisplay() {
        if (mHolder != null) {
            Canvas c = mHolder.lockCanvas();
            c.drawColor(color.background);
            Paint p = new Paint();

            for (int row = pixels.length - 1; row >= 0; row--) {
                for (int col = 0; col < pixels[row].length; col++) {
                    p.setColor(pixels[row][col].color);
                    c.drawRoundRect(pixels[row][col].rect, pixelCornerRadius, pixelCornerRadius, p);
                }
            }
            mHolder.unlockCanvasAndPost(c);
        }
    }

    /**
     * Find full lines that have to be cleared and set the level.
     */
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

    /**
     * Move down every pixel above the cleared Line and make a sound.
     *
     * @param row number of the line to clear
     */
    private void clearLineNumber(int row) {

        MediaPlayer fullLineSound = MediaPlayer.create(this, R.raw.full_line);
        fullLineSound.start();

        // move down lines above
        for (; row > 0; row--) {
            for (int col = 0; col < pixels[row].length; col++) {
                pixels[row][col].color = pixels[row - 1][col].color;
                pixels[row][col].fixed = pixels[row - 1][col].fixed;
            }
        }
        // clear line number 0
        for (int col = 0; col < pixels[0].length; col++) {
            pixels[0][col].color = color.clear;
        }
    }

    /**
     * Generate random tetromino.
     */
    public void nextTetromino() {
        Log.i(TAG, "surfacePreview nextTetromino....");
        nextTetromino((int) (Math.random() * 7));
    }

    /**
     * Set next tetromino and refresh the preview.
     *
     * @param id id of the next tetromino
     */
    protected void nextTetromino(int id) {
        nextTetrominoID = id;

        for (int row = 0; row < preview.length; row++) {
            for (int col = 0; col < preview[row].length; col++) {
                preview[row][col].color = color.background;
            }
        }

        switch (nextTetrominoID) {
            case TETROMINO_O: {
                preview[1][1].color = color.o;
                preview[1][2].color = color.o;
                preview[2][1].color = color.o;
                preview[2][2].color = color.o;
            }
            break;
            case TETROMINO_I: {
                preview[0][1].color = color.i;
                preview[1][1].color = color.i;
                preview[2][1].color = color.i;
                preview[3][1].color = color.i;
            }
            break;
            case TETROMINO_L: {
                preview[0][1].color = color.l;
                preview[1][1].color = color.l;
                preview[2][1].color = color.l;
                preview[2][2].color = color.l;
            }
            break;
            case TETROMINO_J: {
                preview[0][2].color = color.j;
                preview[1][2].color = color.j;
                preview[2][2].color = color.j;
                preview[2][1].color = color.j;
            }
            break;
            case TETROMINO_S: {
                preview[2][0].color = color.s;
                preview[2][1].color = color.s;
                preview[1][1].color = color.s;
                preview[1][2].color = color.s;
            }
            break;
            case TETROMINO_Z: {
                preview[1][0].color = color.z;
                preview[1][1].color = color.z;
                preview[2][1].color = color.z;
                preview[2][2].color = color.z;
            }
            break;
            case TETROMINO_T: {
                preview[2][0].color = color.t;
                preview[2][1].color = color.t;
                preview[2][2].color = color.t;
                preview[1][1].color = color.t;
            }
            break;
        }

        drawPreview();

    }

    /**
     * Draw the preview using the preview array.
     */
    public void drawPreview() {
        Log.i(TAG, "surfacePreview draw Preview....");
        if (mHolder2 != null) {
            canvasPre = mHolder2.lockCanvas();
            canvasPre.drawColor(color.background);
            Paint p = new Paint();

            for (int row = preview.length - 1; row >= 0; row--) {
                for (int col = 0; col < preview[row].length; col++) {
                    p.setColor(preview[row][col].color);
                    canvasPre.drawRoundRect(preview[row][col].rect, (pixelCornerRadius + 1) / 2, (pixelCornerRadius + 1) / 2, p);
                }
            }
//            previewSurface.draw(canvasPre);
//            previewSurface.setVisibility(View.VISIBLE);
            mHolder2.unlockCanvasAndPost(canvasPre);
        }
    }

    /**
     * Sets the new tetromino to the id of the next tetromino, update score, update score board, please new tetromino on the game board (if possible)
     *
     * @return is game over
     */
    public boolean newTetromino() {
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
                textLevel.setText(getString(R.string.level) + "\n" + String.valueOf(level));
                textScore.setText(getString(R.string.score) + "\n" + String.valueOf(score));
                textLines.setText(getString(R.string.lines) + "\n" + String.valueOf(numberOfLinesCleared));
            }
        });


        switch (tetrominoID) {
            case TETROMINO_O: {
                newT[0] = new int[]{0, 4};
                newT[1] = new int[]{0, 5};
                newT[2] = new int[]{1, 4};
                newT[3] = new int[]{1, 5};

                if (checkIfPositionAvailable(newT) == 0) {
                    setTetromino(newT, color.o);
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
                    setTetromino(newT, color.i);
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
                    setTetromino(newT, color.l);
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
                    setTetromino(newT, color.j);
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
                    setTetromino(newT, color.s);
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
                    setTetromino(newT, color.z);
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
                    setTetromino(newT, color.t);
                    gameOver = false;
                }
            }
            break;
        }

        nextTetromino();
//        textLevel.setText("Level:\n" + level + "\n");
        return gameOver;
    }


    /**
     * store the new score in shared preferences
     */
    public void storeLocalHighscore() {
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
        edit.putInt("latest score", score);
        edit.commit();
    }

    /**
     * Sort array (Bubble-Sort)
     *
     * @param array Array to sort
     */
    private void sortIntArray(int[] array) {
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

    public void storeGlobalHighscore() {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String name = sp.getString("name", "unknown");
        String country = sp.getString("country", "");
        if (name.equals("unknown") || country.length() != 2) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "You need a nickname and a country to store your score in the global highscore table.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (score >= 1000) { // only scores above 1000 get stored
                ParseObject scoreObject = new ParseObject("TetrisHighscore");
                scoreObject.put("name", name);
                scoreObject.put("score", score);
                scoreObject.put("country", country);
                scoreObject.saveInBackground();
            }
        }
    }

    /**
     * Stop timer, game over sound, store highscore, show restart dialog.
     */
    public void gameOverTasks() {
        gameOver = true;

        timer.cancel();
        timer.purge();
        timerRunning = 0;

        mediaPlayer.stop();
        MediaPlayer gameOverSound = MediaPlayer.create(this, R.raw.game_over);
        gameOverSound.start();

        Log.i(TAG, "save score in highscore table...");
        storeLocalHighscore();
        storeGlobalHighscore();

        DialogFragment dialog = new RestartDialog();
        Bundle args = new Bundle();
        args.putInt("score", score);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "restart_dialog");


    }
}