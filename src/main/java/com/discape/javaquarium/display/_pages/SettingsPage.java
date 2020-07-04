package com.discape.javaquarium.display._pages;

import com.discape.javaquarium.display.settings.SettingsView;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SettingsPage extends Page {
    @Override
    public Parent getView() {
        SettingsView settingsView = new SettingsView();
        return settingsView.getView();
    }

    @Override
    public void modifyStage(Stage stage) {
        stage.setTitle("Settings");
    }
}
