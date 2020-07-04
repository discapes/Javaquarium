package com.discape.javaquarium.display;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.inject.Inject;
import java.util.Optional;

public class Alerts {

    @Inject private ThemeManager themeManager;

    public void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        themeManager.applyTheme(alert.getDialogPane().getScene());
        alert.show();
    }

    public void inform(String message) {
        inform(message, null);
    }

    public void inform(String message, String headerText) {
        inform(message, headerText, ButtonType.OK);
    }

    public Optional<ButtonType> inform(String message, String headerText, ButtonType... buttonTypes) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, buttonTypes);
        alert.setHeaderText(headerText);
        themeManager.applyTheme(alert.getDialogPane().getScene());
        return alert.showAndWait();
    }


    public boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        themeManager.applyTheme(alert.getDialogPane().getScene());
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

}
