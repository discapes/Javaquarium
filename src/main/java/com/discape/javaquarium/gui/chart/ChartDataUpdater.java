package com.discape.javaquarium.gui.chart;

import com.discape.javaquarium.business.Aquarium;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;

public class ChartDataUpdater {

    private TimerTask currentTask = null;

    private Timer timer = null;

    @Inject
    private IntegerProperty chartUpdateRateMs;
    @Inject
    private IntegerProperty chartHistoryS;
    @Inject
    private Aquarium aquarium;

    private XYChart.Series<String, Number> foodSeries;
    private XYChart.Series<String, Number> oxygenSeries;
    private ObservableList<String> categories;

    public XYChart.Series<String, Number> getFoodSeries() {
        return foodSeries;
    }

    public XYChart.Series<String, Number> getOxygenSeries() {
        return oxygenSeries;
    }

    public ObservableList<String> getCategories() {
        return categories;
    }

    private int numCategories;
    private float updatesPerSecond;

    private void initialize(int numCategories, float updatesPerSecond) {
        categories.clear();
        foodSeries.getData().clear();
        oxygenSeries.getData().clear();
        ReadOnlyFloatProperty amountFood = aquarium.getAmountFood();
        ReadOnlyFloatProperty amountOxygen = aquarium.getAmountOxygen();
        for (int i = 0; i < numCategories; i++) {
            categories.add(Float.toString((float) (numCategories - i - 1) / updatesPerSecond));
            foodSeries.getData().add(new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), amountFood.get()));
            oxygenSeries.getData().add(new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), amountOxygen.get()));
        }

        if (currentTask != null) currentTask.cancel();

        currentTask = wrap(() -> {
            for (int i = 0; i < numCategories - 1; i++) {
                for (XYChart.Series<String, Number> series : new XYChart.Series[]{foodSeries, oxygenSeries}) {
                    XYChart.Data<String, Number> pointToBeMoved = series.getData().get(i + 1);
                    XYChart.Data<String, Number> newPoint = new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), pointToBeMoved.getYValue());
                    series.getData().set(i, newPoint);
                }
            }
            foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountFood.get()));
            oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountOxygen.get()));
        });
        timer.scheduleAtFixedRate(currentTask, chartUpdateRateMs.get(), chartUpdateRateMs.get());

    }

    public void reload() {
        updatesPerSecond = (float) 1000 / (float) chartUpdateRateMs.get();
        numCategories = (int) (chartHistoryS.get() * updatesPerSecond) + 1;
        initialize(numCategories, updatesPerSecond);
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

    @PostConstruct
    private void postConstruct() {
        categories = observableArrayList();
        foodSeries = new XYChart.Series<>();
        oxygenSeries = new XYChart.Series<>();
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");

        chartUpdateRateMs.addListener((observable, oldValue, newValue) -> {
             reload();
        });
         chartHistoryS.addListener((observable, oldValue, newValue) -> {
            reload();
        });

        timer = new Timer(true);
        reload();
    }
}
