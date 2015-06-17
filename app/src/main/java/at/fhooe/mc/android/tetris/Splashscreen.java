package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * First Activity of Tetris-App, which loads the Start-Menu
 */
public class Splashscreen extends Activity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set Fullscreen
        Window win = getWindow();
        win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getActionBar();
        bar.hide();

        setContentView(R.layout.splashscreen);

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                pb.setMax(100);
                pb.setProgress(0);
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(20);
                        pb.setProgress(i + 1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Splashscreen.this, StartMenu.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        };

        Thread t1 = new Thread(r1);
        t1.start();
    }
}
