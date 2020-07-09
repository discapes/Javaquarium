package com.javaquarium.views.app.chart;

import com.javaquarium.backend.services.AquariumService;
import com.javaquarium.backend.services.ChartDataService;
import com.management.Dependency;
import com.management.Presenter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

@Presenter
public class ChartPresenter implements Initializable {

    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis categoryAxis;

    @FXML private Button plusOxygenBtn;
    @FXML private Button minusOxygenBtn;
    @FXML private Button plusFoodBtn;
    @FXML private Button minusFoodBtn;

    @Dependency private ChartDataService chartDataService;
    @Dependency private AquariumService aquariumService;

    @Override public void initialize(URL url, ResourceBundle rb) {
        categoryAxis.setCategories(chartDataService.getCategories());

        for (ObservableList<XYChart.Data<String, Number>> data : chartDataService.getData()) {
            lineChart.getData().add(new XYChart.Series<>(data));
        }

        lineChart.getData().get(2).setName("Oxygen");
        lineChart.getData().get(3).setName("Food");

        /*aquarium.getFood().addListener((observableValue, number, t1) ->
            valueChange((float)t1, plusFoodBtn, minusFoodBtn));
        aquarium.getOxygen().addListener((observableValue, number, t1) ->
                valueChange((float)t1, plusOxygenBtn, minusOxygenBtn));*/
    }

    private void valueChange(float val, Button plusBtn, Button minusBtn) {
        /*if (val > 150)
            minusBtn.setStyle("-fx-background-color: " + (themeManager.getCurrentTheme().contains("Dark") ? "darkred" : "palevioletred"));
        else if (val < 50)
            plusBtn.setStyle("-fx-background-color: " + (themeManager.getCurrentTheme().contains("Dark") ? "darkred" : "palevioletred"));
        else {
            plusBtn.setStyle("");
            minusBtn.setStyle("");
        }*/
    }

    @FXML
    private void increaseOxygen() {
        aquariumService.addOxygen(10);
    }

    @FXML
    private void decreaseOxygen() {
        aquariumService.addOxygen(-10);
    }

    @FXML
    private void increaseFood() {
        aquariumService.addFood(10);
    }

    @FXML
    private void decreaseFood() {
        aquariumService.addFood(-10);
    }
}
