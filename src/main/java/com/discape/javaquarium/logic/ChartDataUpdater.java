package com.discape.javaquarium.logic;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import javax.inject.Inject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;

public class ChartDataUpdater {

    private final Timer timer = new Timer(true);
    @Inject Session session;
    private TimerTask currentTask = null;
    @Inject
    private IntegerProperty chartNumData;
    @Inject
    private IntegerProperty chartHistory;
    private Aquarium aquarium;

    private XYChart.Series<String, Number> paddingSeries1 = new XYChart.Series<>();
    private XYChart.Series<String, Number> paddingSeries2 = new XYChart.Series<>();
    private XYChart.Series<String, Number> foodSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> oxygenSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> bottomSeries = new XYChart.Series<>();
    private ObservableList<String> categories = observableArrayList();

    // so we can haz lambdas
    private static TimerTask wrap(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(r);
            }
        };
    }

    public List<XYChart.Series<String, Number>> getSeries() {
        return List.of(paddingSeries1, paddingSeries2, oxygenSeries, foodSeries, topSeries, bottomSeries);
    }

    public ObservableList<String> getCategories() {
        return categories;
    }

    private void initialize(int numCategories, int secondsHistory) {
        int categoriesPerSecond = (numCategories - 1) / secondsHistory;
        categories.clear();
        foodSeries.getData().clear();
        oxygenSeries.getData().clear();
        topSeries.getData().clear();
        bottomSeries.getData().clear();
        ReadOnlyFloatProperty amountFood = aquarium.getAmountFood();
        ReadOnlyFloatProperty amountOxygen = aquarium.getAmountOxygen();

        for (int i = 0; i < numCategories; i++) {
            int reverseIndex = numCategories - i - 1;
            float indexInSeconds = (float) reverseIndex / (float) categoriesPerSecond;
            String toString = Float.toString(indexInSeconds);
            categories.add(toString);
            foodSeries.getData().add(new XYChart.Data<>(toString, amountFood.get()));
            oxygenSeries.getData().add(new XYChart.Data<>(toString, amountOxygen.get()));
            topSeries.getData().add(new XYChart.Data<>(toString, 150));
            bottomSeries.getData().add(new XYChart.Data<>(toString, 50));
        }
        // to keep range at least 200
        paddingSeries1.getData().add(new XYChart.Data<>("nonExisting", 200));

        if (currentTask != null) currentTask.cancel();

        currentTask = wrap(() -> {
            for (int i = 0; i < numCategories - 1; i++) {
                int reverseIndex = numCategories - i - 1;
                float indexInSeconds = (float) reverseIndex / (float) categoriesPerSecond;
                String toString = Float.toString(indexInSeconds);
                for (XYChart.Series<String, Number> series : List.of(foodSeries, oxygenSeries)) {
                    XYChart.Data<String, Number> pointToBeMoved;
                    try {
                        pointToBeMoved = series.getData().get(i + 1);
                    } catch (IndexOutOfBoundsException e) { return; }
                    XYChart.Data<String, Number> newPoint = new XYChart.Data<>(toString, pointToBeMoved.getYValue());
                    try {
                        series.getData().set(i, newPoint);
                    } catch (RuntimeException e) {
                        System.out.println("Caught " + e.getMessage());
                    }
                }
            }

            foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountFood.get()));
            oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountOxygen.get()));
        });
        session.addTask(currentTask);
        int updateRateMs = 1000 / categoriesPerSecond;
        if (chartNumData.get() > 0)
            timer.scheduleAtFixedRate(currentTask, updateRateMs, updateRateMs);

    }

    public void reload() {
        initialize(chartNumData.get() + 1, chartHistory.get());
    }

    public void init(Aquarium aquarium) {
        this.aquarium = aquarium;
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");

        chartNumData.addListener((observable, oldValue, newValue) -> reload());
        chartHistory.addListener((observable, oldValue, newValue) -> reload());
        reload();
    }
}
