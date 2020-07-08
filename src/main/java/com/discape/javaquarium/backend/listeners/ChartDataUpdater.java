package com.discape.javaquarium.backend.listeners;

import com.discape.javaquarium.backend.aquarium.Aquarium;
import com.discape.javaquarium.backend.events.Events;
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

    public ChartDataUpdater() {
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");
        Events.CHARTSETTINGCHANGE.e().addListener(v -> {
            stopAndClear();
            initializeAndStartUpdater(chartNumData.get() + 1, chartHistory.get());
        });
        Events.NEWAQUARIUM.e().addListener(a -> {
            getSeries().forEach(s -> s.getData().clear());
            this.aquarium = (Aquarium) a;
            this.oxygen = aquarium.getOxygen();
            this.food = aquarium.getFood();
        });
    }


    /** Stops the updater and clears the data and categories. */
    private void stopAndClear() {
        if (currentTask != null) currentTask.cancel();
        categories.clear();
        getSeries().forEach(s -> s.getData().clear());
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
