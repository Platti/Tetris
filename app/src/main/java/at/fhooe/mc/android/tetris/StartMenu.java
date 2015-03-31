package at.fhooe.mc.android.tetris;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class StartMenu extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set Fullscreen
        Window win = getWindow();
        win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getActionBar();
        bar.hide();

        setContentView(R.layout.start_menu);

        Button b = null;
        b = (Button) findViewById(R.id.button_start);
        b.setBackgroundColor(MainActivity.COLOR_O);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_highscores);
        b.setBackgroundColor(MainActivity.COLOR_I);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button_options);
        b.setBackgroundColor(MainActivity.COLOR_L);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:{
                Intent i = new Intent(StartMenu.this, MainActivity.class);
                startActivity(i);
            }
            break;
            case R.id.button_highscores:{
                Intent i = new Intent(StartMenu.this, HighscoreTable.class);
                startActivity(i);
            }
            break;
            case R.id.button_options:{

            }
            break;
        }
    }
}
