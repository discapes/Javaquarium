package com.discape.javaquarium.gui.settings;

import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.Stages;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class SettingsPresenter implements Initializable {

    @Inject private Stages stages;

    @Inject private IThemes themes;

    @FXML
    private ChoiceBox<String> themePicker;

    @FXML
    private void apply() {
        themes.setCurrentTheme(themePicker.getValue());
        stages.reload();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themes.getThemes()));
        themePicker.setValue(themes.getCurrentTheme());
    }
}
