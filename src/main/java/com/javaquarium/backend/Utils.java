package com.javaquarium.backend;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

@SuppressWarnings("unused")

/** Some utility functions, strictly static. */
public abstract class Utils {

    public static String colorToString(Color color) {
        return "#" + color.toString().substring(2, 8);
    }

    public static void tightenAnchors(Node node) {
        setAnchors(node, 0);
    }

    public static void setAnchors(Node node, double length) {
        AnchorPane.setTopAnchor(node, length);
        AnchorPane.setBottomAnchor(node, length);
        AnchorPane.setRightAnchor(node, length);
        AnchorPane.setLeftAnchor(node, length);
    }
}
