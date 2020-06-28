package com.discape.javaquarium.gui.toolbar;

import com.airhacks.afterburner.views.FXMLView;
import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.Stages;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarPresenter implements Initializable {

    @Inject private IThemes themes;

    @Inject private Stages stages;

    @FXML private void loadFromFile() {

    }

    @FXML private void saveToFile() {

    }

    @FXML private void openSettings() {
        Stage settingsStage = new Stage();
        stages.setSettingsStage(settingsStage);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(new SettingsView().getView());
        themes.setTheme(scene);
        settingsStage.setScene(scene);
        settingsStage.showAndWait();
    }

    @FXML private void reload() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
