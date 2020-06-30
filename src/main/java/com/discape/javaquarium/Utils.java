package com.discape.javaquarium;

import com.discape.javaquarium.gui.Themes;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    public static Stage makeWindow(Node node, String title) {
        Stage stage = new Stage();
        AnchorPane root = new AnchorPane();
        Utils.tightenAnchors(node);
        root.getChildren().add(node);
        Scene scene = new Scene(root);
        themes.setTheme(scene);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}
