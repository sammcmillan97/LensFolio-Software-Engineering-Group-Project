package nz.ac.canterbury.seng302.portfolio.util;

import java.util.Random;

public class ColourPicker {
    public static String[] colours = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };
    public static int index = 0;

    /**
     * Gets the next colour in the colour list relative to the methods last call
     * @return a colour from the colours list
     */
    public static String getNextColour() {
        String colour = colours[index];
        index++;
        if (index >= colours.length) {
            index = 0;
        }
        return colour;
    }

    public static void setColourZero() {
        index = 0;
    }

    public static String getColour() {
        Random randomGenerator = new Random(); // Construct a new Random number generator
        int randomNumber = randomGenerator.nextInt(colours.length);
        return colours[randomNumber];
    }
}
