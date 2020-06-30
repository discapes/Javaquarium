package com.discape.javaquarium.gui.chart;

import com.discape.javaquarium.business.model.Aquarium;
import com.discape.javaquarium.gui.ChartDataUpdater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartPresenter implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis categoryAxis;

    @Inject
    private ChartDataUpdater IChartDataUpdater;

    @Inject
    private Aquarium aquarium;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryAxis.setCategories(IChartDataUpdater.getCategories());

        // for some reason if I set the series straight it produces a visual glitch. mb the lineChart modifies the series somehow?
        XYChart.Series<String, Number> oxygen = new XYChart.Series<>(IChartDataUpdater.getOxygenSeries().getData());
        XYChart.Series<String, Number> food = new XYChart.Series<>(IChartDataUpdater.getFoodSeries().getData());
        oxygen.setName("Oxygen");
        food.setName("Food");
        lineChart.getData().add(oxygen);
        lineChart.getData().add(food);
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
