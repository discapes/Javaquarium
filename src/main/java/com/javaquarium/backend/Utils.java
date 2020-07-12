package com.javaquarium.backend;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


/**
 * Some utility functions, strictly static.
 */
@SuppressWarnings("unused")
public abstract class Utils {

    /** Converts a color into a hex string that looks like #efefef.
     * @param color to be copied into a string
     * @return a string representation of the color
     * */
    public static String colorToString(Color color) {
        return "#" + color.toString().substring(2, 8);
    }

    /** Sets the AnchorPane anchors of a node to 0.
     * @param node node to set the anchors for '
     * */
    public static void tightenAnchors(Node node) {
        setAnchors(node, 0);
    }

    /** Sets the AnchorPane anchors of a node to a custom value.
     * @param node node to set the anchors for
     * @param length length the anchors should be set to
     * */
    public static void setAnchors(Node node, double length) {
        AnchorPane.setTopAnchor(node, length);
        AnchorPane.setBottomAnchor(node, length);
        AnchorPane.setRightAnchor(node, length);
        AnchorPane.setLeftAnchor(node, length);
    }
}
