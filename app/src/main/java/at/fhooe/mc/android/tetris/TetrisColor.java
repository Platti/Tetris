package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * Class to assign the colors of one color theme to the individual Tetronimos
 */
public class TetrisColor {
    private Context context;
    public int o;
    public int i;
    public int l;
    public int j;
    public int s;
    public int z;
    public int t;
    public int clear;
    public int background;

    /**
     * Constructor to define the standard colors.
     * Or if already stored in shared preferences, take them.
     *
     * @param context The UI Activity Context
     */
    public TetrisColor(Context context) {
        this.context = context;
        SharedPreferences sp = context.getSharedPreferences(MainActivity.PREF_NAME, context.MODE_PRIVATE);
        o = sp.getInt("color_o", Color.RED);
        i = sp.getInt("color_i", Color.GREEN);
        l = sp.getInt("color_l", Color.YELLOW);
        j = sp.getInt("color_j", Color.BLUE);
        s = sp.getInt("color_s", Color.CYAN);
        z = sp.getInt("color_z", Color.MAGENTA);
        t = sp.getInt("color_t", Color.GRAY);
        clear = Color.TRANSPARENT;
        background = Color.LTGRAY;
    }

    /**
     * change color of the Tetrominos and store it in shared preferences
     * @param colors array with color value for the individual Tetrominos
     */
    public void chooseNewColors(int[] colors) {
        SharedPreferences sp = context.getSharedPreferences(MainActivity.PREF_NAME, context.MODE_PRIVATE);
        o = colors[0];
        i = colors[1];
        l = colors[2];
        j = colors[3];
        s = colors[4];
        z = colors[5];
        t = colors[6];

        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("color_o", o);
        edit.putInt("color_i", i);
        edit.putInt("color_l", l);
        edit.putInt("color_j", j);
        edit.putInt("color_s", s);
        edit.putInt("color_z", z);
        edit.putInt("color_t", t);
        edit.commit();
    }
}
