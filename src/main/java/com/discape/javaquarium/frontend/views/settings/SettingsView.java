package com.discape.javaquarium.frontend.views.settings;

import com.airhacks.afterburner.views.FXMLView;
import com.discape.javaquarium.frontend.persistent.IMainView;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SettingsView extends FXMLView implements IMainView {

    private final Parent root;

    public SettingsView() {
        root = super.getView();
    }

    @Override public void modifyStage(Stage stage) {
        stage.setTitle("Settings");
    }

    @Override public Parent getRoot() {
        return root;
    }
}
