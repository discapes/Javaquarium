package com.discape.javaquarium.gui;

import com.discape.javaquarium.gui.app.AppView;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;

public class Stages {

    @Inject private IThemes themes;

    private Stage appStage;
    private Stage settingsStage;

    public void reload() {
        appStage.setScene(themes.setTheme(new Scene(new AppView().getView())));
        if (settingsStage != null) settingsStage.setScene(themes.setTheme(new Scene(new SettingsView().getView())));
    }

    public void setAppStage(Stage appStage) {
        this.appStage = appStage;
    }

    public void setSettingsStage(Stage settingsStage) {
        this.settingsStage = settingsStage;
    }
}
