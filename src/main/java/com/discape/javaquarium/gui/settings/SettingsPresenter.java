package com.discape.javaquarium.gui.settings;

import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.JavaquariumApplication;
import com.discape.javaquarium.gui.Stages;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class SettingsPresenter implements Initializable {

    @Inject private Stages stages;

    @Inject private IThemes themes;

    @FXML private ChoiceBox<String> themePicker;

    @FXML private Label historyLabel;
    @FXML private Slider chartHistorySSlider;
    @FXML private Label dataLabel;
    @FXML private Slider chartDataPointsSlider;
    @FXML private Label tickLabel;
    @FXML private Slider tickRateMsSlider;

    @Inject private IntegerProperty chartHistoryS;
    @Inject private IntegerProperty chartDataPoints;
    @Inject private IntegerProperty tickRateMs;

    private static IntegerProperty getDisplayValue(Slider slider) {
        IntegerProperty integerProperty = new SimpleIntegerProperty((int) slider.getValue());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            integerProperty.set((int) slider.getBlockIncrement() * Math.round(newValue.floatValue() / (float) slider.getBlockIncrement()));
        });
        return integerProperty;
    }

    @FXML
    private void question() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The chart has an adjustable number of data points, and\n" +
                "the update rate of the chart is automatically calculated\n" +
                "so that each data point will be one update.\n" +
                "For example, if the seconds history is 10,\n" +
                "and number of data points is 200,\n" +
                "the update function will run 20 times a second.\n" +
                "Therefore having a high data/history ratio could cause lag.\n", ButtonType.OK);
        themes.setTheme(alert.getDialogPane().getScene());
        alert.setHeaderText("How does the chart work?");
        alert.show();
    }

    @FXML
    private void apply() {
        themes.setCurrentTheme(themePicker.getValue());

        chartHistoryS.setValue(chartHistorySSlider.getValue());
        chartDataPoints.setValue(chartDataPointsSlider.getValue());
        tickRateMs.setValue(tickRateMsSlider.getValue());

        stages.reload();
    }

    @FXML
    private void reset() {
        Map<String, Object> defaults = JavaquariumApplication.getDefaults();
        chartHistorySSlider.setValue(((SimpleIntegerProperty) defaults.get("chartHistoryS")).getValue());
        chartDataPointsSlider.setValue(((SimpleIntegerProperty) defaults.get("chartDataPoints")).getValue());
        tickRateMsSlider.setValue(((SimpleIntegerProperty) defaults.get("tickRateMs")).getValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themes.getThemes()));
        themePicker.setValue(themes.getCurrentTheme());

        /* SPENT WAYYY TOO LONG ON ALL THESE SLIDERS */
        chartHistorySSlider.setValue(chartHistoryS.getValue());
        chartDataPointsSlider.setValue(chartDataPoints.getValue());
        tickRateMsSlider.setValue(tickRateMs.getValue());

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
