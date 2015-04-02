package at.fhooe.mc.android.tetris;

/**
 * Created by Platti on 02.04.2015.
 */
public class ColorTheme {

    private String name;
    private int[] colors;

    public ColorTheme(String name, int[] colors){
        this.name = name;
        this.colors = colors;
    }

    public String getName(){
        return name;
    }

    public int[] getColors(){
        return colors;
    }

}
