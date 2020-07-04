package com.discape.javaquarium.display.settings;

import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.JavaquariumApplication;
import com.discape.javaquarium.display.StageUtilities;
import com.discape.javaquarium.display.ThemeManager;
import com.discape.javaquarium.display._pages.MainPage;
import com.discape.javaquarium.display._pages.SettingsPage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javax.inject.Inject;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class SettingsPresenter implements Initializable {

    @Inject private ThemeManager themeManager;

    @FXML private ChoiceBox<String> themePicker;

    @FXML private Label historyLabel;
    @FXML private Slider chartHistoryS;
    @FXML private Label dataLabel;
    @FXML private Slider chartNumDataS;
    @FXML private Label tickLabel;
    @FXML private Slider tickRateS;

    @Inject private MainPage mainPage;
    @Inject private SettingsPage settingsPage;
    @Inject private StageUtilities stageUtilities;
    @Inject private Alerts alerts;
    @Inject private IntegerProperty chartHistory;
    @Inject private IntegerProperty chartNumData;
    @Inject private IntegerProperty tickRate;

    private static IntegerProperty getDisplayValue(Slider slider) {
        IntegerProperty integerProperty = new SimpleIntegerProperty((int) slider.getValue());
        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                integerProperty.set((int) slider.getBlockIncrement() * Math.round(newValue.floatValue() / (float) slider.getBlockIncrement())));
        return integerProperty;
    }

    @FXML
    private void question() {
        alerts.inform("The chart has an adjustable number of data points, and\n" +
                "the update rate of the chart is automatically calculated\n" +
                "so that each data point will be one update.\n" +
                "For example, if the seconds history is 10,\n" +
                "and number of data points is 200,\n" +
                "the update function will run 20 times a second.\n" +
                "Therefore having a high data/history ratio could cause lag.\n", "How does the chart work?");
    }

    @FXML
    private void apply() {
        themeManager.setCurrentTheme(themePicker.getValue());

        chartHistory.setValue(chartHistoryS.getValue());
        chartNumData.setValue(chartNumDataS.getValue());
        tickRate.setValue(tickRateS.getValue());

        stageUtilities.setPage(mainPage);
        stageUtilities.setTemporaryPage(settingsPage);
    }

    @FXML
    private void reset() {
        Map<String, Object> defaults = JavaquariumApplication.getDefaults();
        chartHistoryS.setValue(((SimpleIntegerProperty) defaults.get("chartHistory")).getValue());
        chartNumDataS.setValue(((SimpleIntegerProperty) defaults.get("chartNumData")).getValue());
        tickRateS.setValue(((SimpleIntegerProperty) defaults.get("tickRate")).getValue());
        themePicker.setValue(themeManager.getDefaultTheme());
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themeManager.getThemes()));
        themePicker.setValue(themeManager.getCurrentTheme());

        /* SPENT WAYYY TOO LONG ON ALL THESE SLIDERS */
        chartHistoryS.setValue(chartHistory.getValue());
        chartNumDataS.setValue(chartNumData.getValue());
        tickRateS.setValue(tickRate.getValue());

        chartNumDataS.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((double) newValue < chartHistoryS.getValue())
                chartNumDataS.setValue(chartHistoryS.getValue());
        });
        chartHistoryS.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((double) newValue < 1) chartHistoryS.setValue(1);
            if ((double) newValue > chartNumDataS.getValue())
                chartNumDataS.setValue(chartHistoryS.getValue());
        });

        historyLabel.textProperty().bind(Bindings.concat(historyLabel.getText(), getDisplayValue(chartHistoryS)));
        dataLabel.textProperty().bind(Bindings.concat(dataLabel.getText(), getDisplayValue(chartNumDataS)));
        tickLabel.textProperty().bind(Bindings.concat(tickLabel.getText(), getDisplayValue(tickRateS)));
    }
}
