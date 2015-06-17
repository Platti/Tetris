package at.fhooe.mc.android.tetris;

/**
 * Class to make a new Color Theme with a certain name and a certain amount of colors
 */
public class ColorTheme {

    private String name;
    private int[] colors;

    /**
     * Constructor to initialize a new instance
     * @param name of the Color Theme
     * @param colors array of different colors
     */
    public ColorTheme(String name, int[] colors){
        this.name = name;
        this.colors = colors;
    }

    /**
     * @return returns the Name of the current object
     */
    public String getName(){
        return name;
    }

    /**
     * @return returns the color-array of the current object
     */
    public int[] getColors(){
        return colors;
    }

}
