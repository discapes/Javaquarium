package com.discape.javaquarium.gui.chart;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.business.Aquarium;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;

public class ChartPresenter implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis categoryAxis;

    @Inject
    private Aquarium aquarium;
    @Inject
    private Integer chartUpdateRateMs;
    @Inject
    private Integer chartHistoryS;

    private static ObservableList<XYChart.Data<String, Number>> oxygenData = observableArrayList();
    private static ObservableList<XYChart.Data<String, Number>> foodData = observableArrayList();
    private static XYChart.Series<String, Number> oxygenSeries = new XYChart.Series<>();
    private static XYChart.Series<String, Number> foodSeries = new XYChart.Series<>();
    private static boolean repeat = false;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        float updatesPerSecond = (float) 1000 / (float) chartUpdateRateMs;
        int numCategories = (int) (chartHistoryS * updatesPerSecond) + 1;

        // create data and categories
        XYChart.Series<String, Number> foodSeries = new XYChart.Series<>(),
                oxygenSeries = new XYChart.Series<>();
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");
        ReadOnlyFloatProperty amountFood = aquarium.getAmountFood();
        ReadOnlyFloatProperty amountOxygen = aquarium.getAmountOxygen();
        lineChart.getData().addAll(oxygenSeries, foodSeries);

        ObservableList<String> categories = observableArrayList();
        // init chart
        for (int i = 0; i < numCategories; i++) {
            categories.add(Float.toString((float) (numCategories - i - 1) / updatesPerSecond));
            if (repeat) {
                foodSeries.setData(ChartPresenter.foodSeries.getData());
                oxygenSeries.setData(ChartPresenter.oxygenSeries.getData());
            } else {
                foodSeries.getData().add(new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), amountFood.get()));
                oxygenSeries.getData().add(new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), amountOxygen.get()));
                ChartPresenter.foodSeries = foodSeries;
                ChartPresenter.oxygenSeries = oxygenSeries;

            }

        }
        repeat = true;
        categoryAxis.setCategories(categories);
            TimerTask updateTask = wrap(() -> {
                for (int i = 0; i < numCategories - 1; i++) {
                    for (XYChart.Series<String, Number> series : observableArrayList(foodSeries, oxygenSeries)) {
                        XYChart.Data<String, Number> pointToBeMoved = series.getData().get(i + 1);
                        XYChart.Data<String, Number> newPoint = new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), pointToBeMoved.getYValue());
                        series.getData().set(i, newPoint);
                    }
                }
                foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountFood.get()));
                oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountOxygen.get()));
            });
            java.util.Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(updateTask, chartUpdateRateMs, chartUpdateRateMs);

    }

    // so we can haz lambdas
    private static TimerTask wrap(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(r);
            }
        };
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
