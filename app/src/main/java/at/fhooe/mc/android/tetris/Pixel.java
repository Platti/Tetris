package at.fhooe.mc.android.tetris;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Class to define one block of the Tetris-Display
 */
public class Pixel {
    RectF rect;
    int color;
    boolean fixed;

    /**
     * Constructor to initialize a new instance
     * @param rect describes the size of a Pixel
     * @param color describes the color of a Pixel
     * @param fixed describes if Pixel is able to move (false) or not (true)
     */
    public Pixel(RectF rect, int color, boolean fixed) {
        this.rect = rect;
        this.color = color;
        this.fixed = fixed;
    }

    /**
     * Constructor to initialize a new instance with default color and fixed
     * @param rect describes the size of a Pixel
     */
    public Pixel(RectF rect) {
        this(rect, Color.TRANSPARENT, false);
    }
}
