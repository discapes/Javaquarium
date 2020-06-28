package com.discape.javaquarium.gui.settings;

import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.Stages;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class SettingsPresenter implements Initializable {

    @Inject private Stages stages;

    @Inject private IThemes themes;

    @FXML private ChoiceBox<String> themePicker;

    @FXML private Slider chartHistorySSlider;
    @FXML private Slider chartUpdateRateMsSlider;
    @FXML private Slider tickRateMsSlider;

    @Inject private IntegerProperty chartHistoryS;
    @Inject private IntegerProperty chartUpdateRateMs;
    @Inject private IntegerProperty tickRateMs;

    private void apply() {
        //TODO bind slider values to the labels
        themes.setCurrentTheme(themePicker.getValue());
        chartHistoryS.setValue(chartHistorySSlider.getValue());
        chartUpdateRateMs.setValue(chartUpdateRateMsSlider.getValue());
        tickRateMs.setValue(tickRateMsSlider.getValue());
        stages.reload();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themes.getThemes()));
        themePicker.setValue(themes.getCurrentTheme());
        chartHistorySSlider.setValue(chartHistoryS.getValue());
        chartUpdateRateMsSlider.setValue(chartUpdateRateMs.getValue());
        tickRateMsSlider.setValue(tickRateMs.getValue());
    }
}
