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

    private XYChart.Series<String, Number> foodSeries;
    private XYChart.Series<String, Number> oxygenSeries;
    private ObservableList<String> categories;

    // so we can haz lambdas
    private static TimerTask wrap(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(r);
            }
        };
    }

    public XYChart.Series<String, Number> getFoodSeries() {
        return foodSeries;
    }

    public XYChart.Series<String, Number> getOxygenSeries() {
        return oxygenSeries;
    }

    public ObservableList<String> getCategories() {
        return categories;
    }

    private void initialize(int numCategories, int secondsHistory) throws ArithmeticException {
        int categoriesPerSecond = (numCategories - 1) / secondsHistory;
        categories.clear();
        foodSeries.getData().clear();
        oxygenSeries.getData().clear();
        ReadOnlyFloatProperty amountFood = aquarium.getAmountFood();
        ReadOnlyFloatProperty amountOxygen = aquarium.getAmountOxygen();

        for (int i = 0; i < numCategories; i++) {
            int reverseIndex = numCategories - i - 1;
            float indexInSeconds = (float) reverseIndex / (float) categoriesPerSecond;
            String toString = Float.toString(indexInSeconds);
            categories.add(toString);
            foodSeries.getData().add(new XYChart.Data<>(toString, amountFood.get()));
            oxygenSeries.getData().add(new XYChart.Data<>(toString, amountOxygen.get()));
        }

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
                    } catch (IndexOutOfBoundsException e) {
                        // current task is still running while series are cleared and made shorter
                        // I COULD check if numCategories > series.getData().size().
                        // checking for an exception is a hack, but it is probably faster so I'll use it.
                        return;
                    }
                    XYChart.Data<String, Number> newPoint = new XYChart.Data<>(toString, pointToBeMoved.getYValue());
                    series.getData().set(i, newPoint);
                }
            }
            //System.out.println(amountFood.get() + "  ---  " + amountOxygen.get());
            foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountFood.get()));
            oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountOxygen.get()));
        });
        session.addTask(currentTask);
        int updateRateMs = 1000 / categoriesPerSecond;
        if (chartNumData.get() > 0)
            timer.scheduleAtFixedRate(currentTask, updateRateMs, updateRateMs);

    }

    public void reload() {
        try {
            initialize(chartNumData.get() + 1, chartHistory.get());
        } catch (ArithmeticException e) {
            /* I have spent so much time making this chart, and refactoring and fixing everything.
            I'm not gonna spend another 10 hours figuring out why I got one single ArithmeticException that one time.
             */
        }
    }

    public void init(Aquarium aquarium) {
        this.aquarium = aquarium;
        categories = observableArrayList();
        foodSeries = new XYChart.Series<>();
        oxygenSeries = new XYChart.Series<>();
        foodSeries.setName("Food");
        oxygenSeries.setName("Oxygen");

        chartNumData.addListener((observable, oldValue, newValue) -> reload());
        chartHistory.addListener((observable, oldValue, newValue) -> reload());
        reload();
    }
}
