package com.javaquarium.backend.services;

import com.management.Service;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

@Service
public class AlertService {

  //  @Dependency private ThemeManager themeManager;

    /** Shows an error alert. */
    public void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
   //     themeManager.applyTheme(alert.getDialogPane().getScene());
        alert.show();
    }

    /** Shows information. */
    public void inform(String message) {
        inform(message, null);
    }

    /** Shows information with a custom header. */
    public void inform(String message, String headerText) {
        inform(message, headerText, ButtonType.OK);
    }

    /** Shows information with custom buttons and header. */
    public Optional<ButtonType> inform(String message, String headerText, ButtonType... buttonTypes) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, buttonTypes);
        alert.setHeaderText(headerText);
   //     themeManager.applyTheme(alert.getDialogPane().getScene());
        return alert.showAndWait();
    }

    /** Shows and returns the result of a confirmation alert. */
    public boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
   //     themeManager.applyTheme(alert.getDialogPane().getScene());
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

}
