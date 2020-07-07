package com.discape.javaquarium.frontend.views.app.chart;

import com.discape.javaquarium.backend.Aquarium;
import com.discape.javaquarium.backend.ChartDataUpdater;
import com.discape.javaquarium.frontend.persistent.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
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

    @Inject private ThemeManager themeManager;
    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private Aquarium aquarium;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryAxis.setCategories(chartDataUpdater.getCategories());

        for (XYChart.Series<String, Number> series : chartDataUpdater.getSeries()) {
            lineChart.getData().add(new XYChart.Series<>(series.getData()));
        }

        lineChart.getData().get(2).setName("Oxygen");
        lineChart.getData().get(3).setName("Food");

        aquarium.getFood().addListener((observableValue, number, t1) ->
            valueChange((float)t1, plusFoodBtn, minusFoodBtn));
        aquarium.getOxygen().addListener((observableValue, number, t1) ->
                valueChange((float)t1, plusOxygenBtn, minusOxygenBtn));
    }

    private void valueChange(float val, Button plusBtn, Button minusBtn) {
        if (val > 150)
            minusBtn.setStyle("-fx-background-color: " + (themeManager.getCurrentTheme().contains("Dark") ? "darkred" : "palevioletred"));
        else if (val < 50)
            plusBtn.setStyle("-fx-background-color: " + (themeManager.getCurrentTheme().contains("Dark") ? "darkred" : "palevioletred"));
        else {
            plusBtn.setStyle("");
            minusBtn.setStyle("");
        }
    }

    @FXML
    private void increaseOxygen() {
        aquarium.plusOxygen();
    }

    @FXML
    private void decreaseOxygen() {
        aquarium.minusOxygen();
    }

    @FXML
    private void increaseFood() {
        aquarium.plusFood();
    }

    @FXML
    private void decreaseFood() {
        aquarium.minusFood();
    }
}