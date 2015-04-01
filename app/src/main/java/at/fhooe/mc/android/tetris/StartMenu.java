package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class StartMenu extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set Fullscreen
//        Window win = getWindow();
//        win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ActionBar bar = getActionBar();
//        bar.hide();

        setContentView(R.layout.start_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GradientDrawable gd1 = new GradientDrawable();
        GradientDrawable gd2 = new GradientDrawable();
        GradientDrawable gd3 = new GradientDrawable();

        gd1.setCornerRadius(10);
        gd2.setCornerRadius(10);
        gd3.setCornerRadius(10);

        Button b = null;
        b = (Button) findViewById(R.id.button_start);
        gd1.setColors(new int[]{MainActivity.COLOR_O, MainActivity.COLOR_O | Color.LTGRAY, MainActivity.COLOR_O,});
        b.setBackground(gd1);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_highscores);
        gd2.setColors(new int[]{MainActivity.COLOR_I, MainActivity.COLOR_I | Color.LTGRAY, MainActivity.COLOR_I,});
        b.setBackground(gd2);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_options);
        gd3.setColors(new int[]{MainActivity.COLOR_L, MainActivity.COLOR_L | Color.LTGRAY, MainActivity.COLOR_L,});
        b.setBackground(gd3);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        GradientDrawable gdPressed = new GradientDrawable();
        gdPressed.setCornerRadius(10);
        gdPressed.setColor(Color.WHITE);

        switch (v.getId()) {
            case R.id.button_start: {
                v.setBackground(gdPressed);
                Intent i = new Intent(StartMenu.this, MainActivity.class);
                startActivity(i);
            }
            break;
            case R.id.button_highscores: {
                v.setBackground(gdPressed);
                Intent i = new Intent(StartMenu.this, HighscoreTable.class);
                startActivity(i);
            }
            break;
            case R.id.button_options: {
                v.setBackground(gdPressed);
            }
            break;
        }
    }
}
