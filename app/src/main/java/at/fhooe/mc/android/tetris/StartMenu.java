package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;

/**
 * Activity to show the Start-Menu of the Tetris-App
 * Choice how to go on in the app
 * Possible actions are
 * 1. start the game in single mode
 * 2. start the game in multiplayer mode
 * 3. switch to the highscore table
 * 4. switch to the options menu to choose an other color theme
 */
public class StartMenu extends Activity implements View.OnClickListener {

    TetrisColor color;
    TetrisMediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        color = new TetrisColor(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "1PaxayessSb1r1M2IFSmrWNveeM39vcKi8drsrKg", "1t0O9bGHymohSRpJGffsbLVNNeS7EFMTGo195W6c");

        final SharedPreferences sp = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        String name = sp.getString("name", "unknown");
        if (name.equals("unknown")) {
            DialogFragment nameDialog = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Choose a nickname.");

                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!input.getText().toString().equals("unknown") && !input.getText().toString().equals("")) {
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("name", input.getText().toString());
                                edit.commit();
                            }
                        }
                    });

                    return builder.create();
                }
            };
            nameDialog.show(getFragmentManager(), "name_dialog");
        } else {
            Toast.makeText(this, "Your Nickname: " + name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mediaPlayer.stop();
        mediaPlayer.destroy(R.raw.menu_theme);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GradientDrawable gd1 = new GradientDrawable();
        GradientDrawable gd2 = new GradientDrawable();
        GradientDrawable gd3 = new GradientDrawable();
        GradientDrawable gd4 = new GradientDrawable();

        gd1.setCornerRadius(10);
        gd2.setCornerRadius(10);
        gd3.setCornerRadius(10);
        gd4.setCornerRadius(10);

        Button b = null;
        b = (Button) findViewById(R.id.button_start);
        gd1.setColors(new int[]{color.o | Color.LTGRAY, color.o});
        b.setBackground(gd1);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_multiplayer);
        gd2.setColors(new int[]{color.i | Color.LTGRAY, color.i});
        b.setBackground(gd2);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_highscores);
        gd3.setColors(new int[]{color.l | Color.LTGRAY, color.l});
        b.setBackground(gd3);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button_options);
        gd4.setColors(new int[]{color.j | Color.LTGRAY, color.j});
        b.setBackground(gd4);
        b.setOnClickListener(this);


        mediaPlayer = TetrisMediaPlayer.getInstance(StartMenu.this, R.raw.menu_theme);
        mediaPlayer.start(false);
    }

    @Override
    public void onClick(View v) {
        GradientDrawable gdPressed = new GradientDrawable();
        gdPressed.setCornerRadius(10);
        gdPressed.setColor(Color.WHITE);

        switch (v.getId()) {
            case R.id.button_start: {
                v.setBackground(gdPressed);
                mediaPlayer.stop();
                mediaPlayer.destroy(R.raw.menu_theme);
                Intent i = new Intent(StartMenu.this, MainActivity.class);
                startActivity(i);
            }
            break;
            case R.id.button_multiplayer: {
                v.setBackground(gdPressed);
                mediaPlayer.setStop(false);
                Intent i = new Intent(StartMenu.this, BluetoothMenu.class);
                startActivity(i);
            }
            break;
            case R.id.button_highscores: {
                v.setBackground(gdPressed);
                mediaPlayer.setStop(false);
//                Intent i = new Intent(StartMenu.this, HighscoreTable.class);
                Intent i = new Intent(StartMenu.this, HighscoreActivity.class);
                startActivity(i);
            }
            break;
            case R.id.button_options: {
                v.setBackground(gdPressed);
                mediaPlayer.setStop(false);
                Intent i = new Intent(StartMenu.this, OptionsActivity.class);
                startActivity(i);
            }
            break;
        }
    }
}