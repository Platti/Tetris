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

    TetrisColor color;

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
        color = new TetrisColor(this);
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
        gd1.setColors(new int[]{color.o, color.o | Color.LTGRAY, color.o});
        b.setBackground(gd1);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_highscores);
        gd2.setColors(new int[]{color.i, color.i | Color.LTGRAY, color.i});
        b.setBackground(gd2);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_options);
        gd3.setColors(new int[]{color.l, color.l | Color.LTGRAY, color.l});
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
