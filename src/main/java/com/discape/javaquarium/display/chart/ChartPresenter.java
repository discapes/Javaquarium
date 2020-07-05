package com.discape.javaquarium.display.chart;

import com.discape.javaquarium.logic.Aquarium;
import com.discape.javaquarium.logic.ChartDataUpdater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartPresenter implements Initializable {

    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis categoryAxis;

    @FXML private Button plusOxygenBtn;
    @FXML private Button minusOxygenBtn;
    @FXML private Button plusFoodBtn;
    @FXML private Button minusFoodBtn;

    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Aquarium aquarium;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryAxis.setCategories(chartDataUpdater.getCategories());

        // for some reason if I set the series straight it produces a visual glitch. mb the lineChart modifies the series somehow?
        XYChart.Series<String, Number> oxygen = new XYChart.Series<>(chartDataUpdater.getOxygenSeries().getData());
        XYChart.Series<String, Number> food = new XYChart.Series<>(chartDataUpdater.getFoodSeries().getData());
        oxygen.setName("Oxygen");
        food.setName("Food");
        lineChart.getData().add(oxygen);
        lineChart.getData().add(food);

        aquarium.getAmountFood().addListener((observableValue, number, t1) ->
            valueChange((float)t1, plusFoodBtn, minusFoodBtn));
        aquarium.getAmountOxygen().addListener((observableValue, number, t1) ->
                valueChange((float)t1, plusOxygenBtn, minusOxygenBtn));
    }

    private void valueChange(float val, Button plusBtn, Button minusBtn) {
        if (val > 150)
            minusBtn.setStyle("-fx-background-color: -fx-accent");
        else if (val < 50)
            plusBtn.setStyle("-fx-background-color: -fx-accent");
        else {
            plusBtn.setStyle("");
            minusBtn.setStyle("");
        }
    }

    @FXML
    private void increaseOxygen() {
        aquarium.increaseOxygen();
    }

    @FXML
    private void decreaseOxygen() {
        aquarium.decreaseOxygen();
    }

    @FXML
    private void increaseFood() {
        aquarium.increaseFood();
    }

    @FXML
    private void decreaseFood() {
        aquarium.decreaseFood();
    }
}
