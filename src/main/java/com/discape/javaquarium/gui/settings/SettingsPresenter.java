package com.discape.javaquarium.gui.settings;

import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.Stages;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themePicker.setItems(observableArrayList(themes.getThemes()));
        themePicker.setValue(themes.getCurrentTheme());

        chartHistorySSlider.setValue(chartHistoryS.getValue());
        chartDataPointsSlider.setValue(chartDataPoints.getValue());
        tickRateMsSlider.setValue(tickRateMs.getValue());

        chartDataPointsSlider.minProperty().bind(chartHistorySSlider.valueProperty());

        historyLabel.textProperty().bind(Bindings.concat(historyLabel.getText(), chartHistorySSlider.valueProperty()));
        dataLabel.textProperty().bind(Bindings.concat(dataLabel.getText(), chartDataPointsSlider.valueProperty()));
        tickLabel.textProperty().bind(Bindings.concat(tickLabel.getText(), tickRateMsSlider.valueProperty()));
    }
}
