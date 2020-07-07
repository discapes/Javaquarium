package com.discape.javaquarium.backend;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * Responsible for creating and updating the data displayed in the chart.
 */
public class ChartDataUpdater {


    private final Timer timer = new Timer(true);
    private TimerTask currentTask = null;
    private Aquarium aquarium = null;
    private ReadOnlyFloatProperty oxygen;
    private ReadOnlyFloatProperty food;

    @Inject private IntegerProperty chartNumData;
    @Inject private IntegerProperty chartHistory;

    private final XYChart.Series<String, Number> paddingSeries1 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> paddingSeries2 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> foodSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> oxygenSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> bottomSeries = new XYChart.Series<>();
    private final ObservableList<String> categories = observableArrayList();

    // so we can haz lambdas
    private static TimerTask wrap(Runnable r) {
        return new TimerTask() {
                @Override public void run() { Platform.runLater(r); }
        };
    }

    /** Returns a list of all the series that should be copied *not added* to the chart.
     *  Not copying only the data to the chart creates visual glitches.
     */
    public List<XYChart.Series<String, Number>> getSeries() {
        return List.of(paddingSeries1, paddingSeries2, oxygenSeries, foodSeries, topSeries, bottomSeries);
    }

    /**
     * Returns a list of all the categories that should be added to the chart.
     */
    public ObservableList<String> getCategories() {
        return categories;
    }

    @PostConstruct
    private void init() {
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");
        chartNumData.addListener((observable, oldValue, newValue) -> tryReset());
        chartHistory.addListener((observable, oldValue, newValue) -> tryReset());
    }

    /** Stops and starts the chart if it already has a source of data. */
    public void tryReset() {
        if (aquarium != null) {
            stop();
            initializeAndStartUpdater(chartNumData.get() + 1, chartHistory.get());
        }
    }

    /**
     * Initializes the chart and starts the updater.
     * @param aquarium source of data.
     */
    public void start(Aquarium aquarium) {
        this.aquarium = aquarium;
        this.oxygen = aquarium.getOxygen();
        this.food = aquarium.getFood();
        initializeAndStartUpdater(chartNumData.get() + 1, chartHistory.get());
    }

    /** Stops the updater and clears the data and categories. */
    public void stop() {
        if (currentTask != null) currentTask.cancel();
        getSeries().forEach(s -> s.getData().clear());
        categories.clear();
    }

    private void initializeAndStartUpdater(int numCategories, int secondsHistory) {
        int categoriesPerSecond = (numCategories - 1) / secondsHistory;

        for (int i = 0; i < numCategories; i++) {
            int reverseIndex = numCategories - i - 1;
            float indexInSeconds = (float) reverseIndex / (float) categoriesPerSecond;
            String toString = Float.toString(indexInSeconds);
            categories.add(toString);
            foodSeries.getData().add(new XYChart.Data<>(toString, food.get()));
            oxygenSeries.getData().add(new XYChart.Data<>(toString, food.get()));
            topSeries.getData().add(new XYChart.Data<>(toString, 150));
            bottomSeries.getData().add(new XYChart.Data<>(toString, 50));
        }
        // to keep range at least 200
        paddingSeries1.getData().add(new XYChart.Data<>("nonExisting", 200));

        startUpdater(numCategories, categoriesPerSecond);
    }

    private void startUpdater(int numCategories, int categoriesPerSecond) {
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
                    series.getData().set(i, newPoint);
                }
            }

            foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", food.get()));
            oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", oxygen.get()));
        });
        int updateRateMs = 1000 / categoriesPerSecond;
        if (chartNumData.get() > 0)
            timer.scheduleAtFixedRate(currentTask, updateRateMs, updateRateMs);
    }
}
