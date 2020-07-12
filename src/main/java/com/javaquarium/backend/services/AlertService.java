package com.javaquarium.backend.services;

import com.firework.Dependency;
import com.firework.Service;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/** Shows alerts on the screen. */
@Service
public class AlertService {

    @Dependency private ThemeService themeService;

    /**
     * Shows an error alert.
     * @param message error message to be shown in the alert
     */
    public void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        themeService.applyTheme(alert.getDialogPane().getScene());
        alert.show();
    }

    /**
     * Shows an informational alert.
     * @param message information to be shown in the alert
     */
    public void inform(String message) {
        inform(message, null);
    }

    /**
     * Shows an informational alert with a custom header.
     * @param message information to be shown in the alert
     * @param headerText header text for the alert
     */
    public void inform(String message, String headerText) {
        inform(message, headerText, ButtonType.OK);
    }

    /**
     * Shows an informational alert with custom buttons and header.
     * @param message information to be shown in the alert
     * @param headerText header text for the alert
     * @param buttonTypes custom button types to be used in the alert
     * @return the type of button that was pressed
     */
    public Optional<ButtonType> inform(String message, String headerText, ButtonType... buttonTypes) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, buttonTypes);
        alert.setHeaderText(headerText);
        themeService.applyTheme(alert.getDialogPane().getScene());
        return alert.showAndWait();
    }

    /**
     * Shows and returns the result of a confirmation alert.
     * @param msg question to ask the user
     * @return a boolean indicating if the user has clicked yes (true) or no (false)
     */
    public boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        themeService.applyTheme(alert.getDialogPane().getScene());
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

}
