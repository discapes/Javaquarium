package com.discape.javaquarium;

import com.discape.javaquarium.gui.IThemes;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;

import javax.inject.Inject;

public class Utils {

    private static IThemes themes;

    public static void setThemes(IThemes themes) {
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
}
