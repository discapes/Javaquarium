package com.javaquarium.views.settings;

import com.firework.Dependency;
import com.firework.EventSystem;
import com.javaquarium.Events;
import com.javaquarium.backend.services.AlertService;
import com.javaquarium.backend.services.SettingService;
import com.javaquarium.backend.services.ThemeService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class SettingsPresenter implements Initializable {

    @FXML private ChoiceBox<String> themePicker;

    @FXML private Label historyLabel;
    @FXML private Slider chartHistoryS;
    @FXML private Label dataLabel;
    @FXML private Slider chartNumDataS;
    @FXML private Label tickLabel;
    @FXML private Slider tickRateS;

    @Dependency private SettingService settingService;
    @Dependency private ThemeService themeService;
    @Dependency private AlertService alertService;

    private static IntegerProperty getDisplayValue(Slider slider) {
        IntegerProperty integerProperty = new SimpleIntegerProperty((int) slider.getValue());
        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                integerProperty.set((int) slider.getBlockIncrement() * Math.round(newValue.floatValue() / (float) slider.getBlockIncrement())));
        return integerProperty;
    }

    @FXML
    private void question() {
        alertService.inform("The chart has an adjustable number of data points, and\n" +
                "the update rate of the chart is automatically calculated\n" +
                "so that each data point will be one update.\n" +
                "For example, if the seconds history is 10,\n" +
                "and number of data points is 200,\n" +
                "the update function will run 20 times a second.\n" +
                "Therefore having a high data/history ratio could cause lag.\n", "How does the chart work?");
    }

    @FXML
    private void apply() {
        int nextChartHistory = (int) chartHistoryS.getValue();
        int nextPrettyChartPoints = (int) chartNumDataS.getValue();
        if (nextChartHistory != SettingService.chartHistory || nextPrettyChartPoints != SettingService.prettyChartPoints) {
            SettingService.chartHistory = nextChartHistory;
            SettingService.prettyChartPoints = nextPrettyChartPoints;
            EventSystem.queueAutomaticEvent(Events.CHARTSETTINGCHANGE);
        }

        int nextTickRate = (int) tickRateS.getValue();
        if (SettingService.tickRate != nextTickRate) {
            SettingService.tickRate = nextTickRate;
            EventSystem.queueAutomaticEvent(Events.TICKRATECHANGE);
        }
    }

    @FXML private void reset() {
        chartHistoryS.setValue(SettingService.defaultChartHistory);
        chartNumDataS.setValue(SettingService.defaultPrettyChartPoints);
        tickRateS.setValue(SettingService.defaultTickRate);
    }

    @FXML private void applyTheme() {
        SettingService.theme = themePicker.getValue();
        EventSystem.queueAutomaticEvent(Events.NEWTHEME);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themeService.getThemes()));
        themePicker.setValue(SettingService.theme);

        /* SPENT WAYYY TOO LONG ON ALL THESE SLIDERS */
        chartHistoryS.setValue(SettingService.chartHistory);
        chartNumDataS.setValue(SettingService.prettyChartPoints);
        tickRateS.setValue(SettingService.tickRate);

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
