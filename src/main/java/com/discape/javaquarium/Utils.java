package com.discape.javaquarium;

import javafx.scene.paint.Color;

public class Utils {
    public static String colorToString(Color color) {
        return "#" + color.toString().substring(2, 8);
    }
}
