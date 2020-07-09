package com.javaquarium.views.settings;

import com.javaquarium.Event;
import com.javaquarium.backend.Settings;
import com.javaquarium.backend.services.AlertService;
import com.javaquarium.backend.services.ThemeService;
import com.management.Dependency;
import com.management.LawnMower;
import com.management.Presenter;
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

@Presenter
public class SettingsPresenter implements Initializable {

    @FXML private ChoiceBox<String> themePicker;

    @FXML private Label historyLabel;
    @FXML private Slider chartHistoryS;
    @FXML private Label dataLabel;
    @FXML private Slider chartNumDataS;
    @FXML private Label tickLabel;
    @FXML private Slider tickRateS;

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
        themeService.setCurrentTheme(themePicker.getValue());
        int nextChartHistory = (int) chartHistoryS.getValue();
        int nextPrettyChartPoints = (int) chartNumDataS.getValue();
        if (nextChartHistory != Settings.chartHistory || nextPrettyChartPoints != Settings.prettyChartPoints) {
            Settings.chartHistory = nextChartHistory;
            Settings.prettyChartPoints = nextPrettyChartPoints;
            LawnMower.queueAutomaticEvent(Event.CHARTSETTINGCHANGE);
        }

        int nextTickRate = (int) tickRateS.getValue();
        if (Settings.tickRate != nextTickRate) {
            Settings.tickRate = nextTickRate;
            LawnMower.queueAutomaticEvent(Event.TICKRATECHANGE);
        }
    }

    @FXML
    private void reset() {
        chartHistoryS.setValue(Settings.defaultChartHistory);
        chartNumDataS.setValue(Settings.defaultPrettyChartPoints);
        tickRateS.setValue(Settings.defaultTickRate);
        themePicker.setValue(Settings.defaultTheme);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themeService.getThemes()));
        themePicker.setValue(themeService.getCurrentTheme());

        /* SPENT WAYYY TOO LONG ON ALL THESE SLIDERS */
        chartHistoryS.setValue(Settings.chartHistory);
        chartNumDataS.setValue(Settings.prettyChartPoints);
        tickRateS.setValue(Settings.tickRate);

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
