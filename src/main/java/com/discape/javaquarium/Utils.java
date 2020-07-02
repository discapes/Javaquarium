package com.discape.javaquarium;

import com.discape.javaquarium.gui.Themes;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class Utils {

    private static Themes themes;

    public static void setThemes(Themes themes) {
        Utils.themes = themes;
    }

    public static String colorToString(Color color) {
        return "#" + color.toString().substring(2, 8);
    }

    public static void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        themes.setTheme(alert.getDialogPane().getScene());
        alert.show();
    }

    public static void inform(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        themes.setTheme(alert.getDialogPane().getScene());
        alert.show();
    }

    public static boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        themes.setTheme(alert.getDialogPane().getScene());
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

    public static void tightenAnchors(Node node) {
        AnchorPane.setTopAnchor(node, 0.);
        AnchorPane.setBottomAnchor(node, 0.);
        AnchorPane.setRightAnchor(node, 0.);
        AnchorPane.setLeftAnchor(node, 0.);
    }

    public static void setAnchors(Node node, double length) {
        AnchorPane.setTopAnchor(node, length);
        AnchorPane.setBottomAnchor(node, length);
        AnchorPane.setRightAnchor(node, length);
        AnchorPane.setLeftAnchor(node, length);
    }

    public static Stage prepareWindow(Node node, String title, double anchors, Stage stage) {
        AnchorPane root = new AnchorPane();
        Utils.setAnchors(node, anchors);
        root.getChildren().add(node);
        Scene scene = new Scene(root);
        themes.setTheme(scene);
        stage.setTitle(title);
        stage.setScene(scene);
        try {
            stage.initModality(Modality.APPLICATION_MODAL);
        } catch (IllegalStateException ignored) {}
        /* Cant set modality for primary stages, but because isPrimary() is private we do it this way */
        return stage;
    }

    public static Stage makeWindow(Node node, String title, double anchors) {
        return prepareWindow(node, title, anchors, new Stage());
    }

    public static Stage makeWindow(Node node, String title) {
        return makeWindow(node, title, 50);
    }

    public static Stage prepareWindow(Node node, String title, Stage stage) {
        return prepareWindow(node, title, 50, stage);
    }

    public static Stage prepareWindow(Node node, String title, Stage stage, double anchors) {
        return prepareWindow(node, title, anchors, stage);
    }
}
