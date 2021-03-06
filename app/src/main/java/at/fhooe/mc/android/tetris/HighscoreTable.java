package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.fhooe.mc.android.tetris.R;

/**
 * Activity to list the 10 best highscores
 *
 * @deprecated
 */
public class HighscoreTable extends Activity {

    TetrisMediaPlayer mediaPlayer;

    /**
     * views the 10 best highscores getting with shared preferences
     * The first 3 and the last highscore are marked with different colors
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_table);

        SharedPreferences sp = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.highscore_layout);

        int score;
        boolean highlighted = false;
        for (int i = 0; i < 10; i++) {
            score = sp.getInt("score" + i, -1);
            if (score >= 0) {
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(30.0f);
                if (i == 0) {
                    tv.setTextColor(getResources().getColor(R.color.gold));
                } else if (i == 1) {
                    tv.setTextColor(getResources().getColor(R.color.silver));
                } else if (i == 2) {
                    tv.setTextColor(getResources().getColor(R.color.bronze));
                } else {
                    tv.setTextColor(getResources().getColor(R.color.white));
                }
                if(!highlighted && score == sp.getInt("latest score", -1)){
                    tv.setTextColor(getResources().getColor(R.color.cyan));
                    highlighted = true;
                }
                tv.setText(String.valueOf(score));
                layout.addView(tv);
            }
        }

        mediaPlayer = TetrisMediaPlayer.getInstance(HighscoreTable.this, R.raw.menu_theme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}
