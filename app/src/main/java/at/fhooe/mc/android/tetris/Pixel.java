package at.fhooe.mc.android.tetris;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by Platti on 25.03.2015.
 */
public class Pixel {
    RectF rect;
    int color;
    boolean fixed;

    public Pixel(RectF rect, int color, boolean fixed) {
        this.rect = rect;
        this.color = color;
        this.fixed = fixed;
    }

    public Pixel(RectF rect) {
        this(rect, Color.TRANSPARENT, false);
    }
}
