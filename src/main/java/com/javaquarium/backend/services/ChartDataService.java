package com.javaquarium.backend.services;

import com.javaquarium.Event;
import com.javaquarium.backend.Settings;
import com.management.AfterInjection;
import com.management.Dependency;
import com.management.OnEvent;
import com.management.Service;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * Responsible for creating and updating the data displayed in the chart.
 */
@Service
public class ChartDataService {


    private final Timer timer = new Timer(true);
    private TimerTask timerTask = null;

    private int points;
    private double pointsPerSecond;

    private ReadOnlyDoubleProperty oxygen;
    private ReadOnlyDoubleProperty food;
    @Dependency private AquariumService aquariumService;

    private final ObservableList<XYChart.Data<String, Number>> paddingData1 = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> paddingData2 = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> oxygenData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> foodData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> topData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> bottomData = observableArrayList();
    private final ObservableList<String> categories = observableArrayList();

    @OnEvent(Event.LOGOUT)
    private void close() {
        timerTask.cancel();
        categories.clear();
        getData().forEach(data -> data.clear());
    }

    @OnEvent(Event.LOGIN)
    private void fillChartAndStartUpdater() {
        points = Settings.prettyChartPoints + 1;
        pointsPerSecond = (points - 1) / (double)Settings.chartHistory;

        for (int i = 0; i < points; i++) {
            int indexFromRight = points - i - 1;
            String indexInSeconds = Double.toString((double) indexFromRight / pointsPerSecond);
            categories.add(indexInSeconds);
            oxygenData.add(new XYChart.Data<>(indexInSeconds, aquariumService.getOxygen().get()));
            foodData.add(new XYChart.Data<>(indexInSeconds, aquariumService.getFood().get()));
            topData.add(new XYChart.Data<>(indexInSeconds, 150));
            bottomData.add(new XYChart.Data<>(indexInSeconds, 50));
        }
        paddingData1.add(new XYChart.Data<>("range", 200));

        assert timerTask == null;
        timerTask = newTimerTask();
        int updateRateMs = (int)(1000 / pointsPerSecond);
        if (Settings.prettyChartPoints > 0)
            timer.scheduleAtFixedRate(timerTask, updateRateMs, updateRateMs);
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            @Override public void run() {
                Platform.runLater(() -> {
                    for (int i = 0; i < points - 1; i++) {
                        int reverseIndex = points - i - 1;
                        float indexInSeconds = (float) reverseIndex / (float) pointsPerSecond;
                        String toString = Float.toString(indexInSeconds);
                        for (ObservableList<XYChart.Data<String, Number>> data : List.of(foodData, oxygenData)) {
                            XYChart.Data<String, Number> pointToBeMoved;
                            try {
                                pointToBeMoved = data.get(i + 1);
                            } catch (IndexOutOfBoundsException e) { return; }
                            XYChart.Data<String, Number> newPoint = new XYChart.Data<>(toString, pointToBeMoved.getYValue());
                            data.set(i, newPoint);
                        }
                    }
                    foodData.set(points - 1, new XYChart.Data<>("0.0", food.get()));
                    oxygenData.set(points - 1, new XYChart.Data<>("0.0", oxygen.get()));
                });
            }
        };
    }

    public List<ObservableList<XYChart.Data<String, Number>>> getData() {
        return List.of(paddingData1, paddingData2, oxygenData, foodData, topData, bottomData);
    }

    public ObservableList<String> getCategories() {
        return categories;
    }

    @AfterInjection
    private void afterInjection() {
        oxygen = aquariumService.getOxygen();
        food = aquariumService.getFood();
    }
}
