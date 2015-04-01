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

public class HighscoreTable extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_table);

        SharedPreferences sp = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.highscore_layout);

        int score;
        for (int i = 0; i < 10; i++) {
            score = sp.getInt("score" + i, -1);
            if (score >= -1) {
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
                tv.setText(String.valueOf(score));
                layout.addView(tv);
            }
        }
    }
}
