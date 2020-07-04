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
    @FXML private Slider chartHistorySSlider;
    @FXML private Label dataLabel;
    @FXML private Slider chartDataPointsSlider;
    @FXML private Label tickLabel;
    @FXML private Slider tickRateMsSlider;

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

        chartHistory.setValue(chartHistorySSlider.getValue());
        chartNumData.setValue(chartDataPointsSlider.getValue());
        tickRate.setValue(tickRateMsSlider.getValue());

        stageUtilities.setPage(mainPage);
        stageUtilities.setTemporaryPage(settingsPage);
    }

    @FXML
    private void reset() {
        Map<String, Object> defaults = JavaquariumApplication.getDefaults();
        chartHistorySSlider.setValue(((SimpleIntegerProperty) defaults.get("chartHistoryS")).getValue());
        chartDataPointsSlider.setValue(((SimpleIntegerProperty) defaults.get("chartDataPoints")).getValue());
        tickRateMsSlider.setValue(((SimpleIntegerProperty) defaults.get("tickRateMs")).getValue());
        themeManager.resetTheme();

        stageUtilities.setPage(mainPage);
        stageUtilities.setTemporaryPage(settingsPage);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themeManager.getThemes()));
        themePicker.setValue(themeManager.getCurrentTheme());

        /* SPENT WAYYY TOO LONG ON ALL THESE SLIDERS */
        chartHistorySSlider.setValue(chartHistory.getValue());
        chartDataPointsSlider.setValue(chartNumData.getValue());
        tickRateMsSlider.setValue(tickRate.getValue());

        chartDataPointsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((double) newValue < chartHistorySSlider.getValue())
                chartDataPointsSlider.setValue(chartHistorySSlider.getValue());
        });
        chartHistorySSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((double) newValue < 1) chartHistorySSlider.setValue(1);
            if ((double) newValue > chartDataPointsSlider.getValue())
                chartDataPointsSlider.setValue(chartHistorySSlider.getValue());
        });

        historyLabel.textProperty().bind(Bindings.concat(historyLabel.getText(), getDisplayValue(chartHistorySSlider)));
        dataLabel.textProperty().bind(Bindings.concat(dataLabel.getText(), getDisplayValue(chartDataPointsSlider)));
        tickLabel.textProperty().bind(Bindings.concat(tickLabel.getText(), getDisplayValue(tickRateMsSlider)));
    }
}
