package com.javaquarium;

/** Main program class. The maven shade plugin requires that the class of the main method doesn't inherit
 * from Application, which is required for JavaFX.
 */
public class Javaquarium {
    /** Calls the actual main method. */
    public static void main(String[] args) {
        JavaquariumApplication.actualMain(args);
    }
}
