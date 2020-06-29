package com.discape.javaquarium.gui;

import com.discape.javaquarium.business.Aquarium;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;

public class ChartDataUpdater implements IChartDataUpdater {

    private TimerTask currentTask = null;

    private final Timer timer = new Timer(true);

    @Inject
    private IntegerProperty chartUpdateRateMs;
    @Inject
    private IntegerProperty chartHistoryS;
    @Inject
    private Aquarium aquarium;

    private XYChart.Series<String, Number> foodSeries;
    private XYChart.Series<String, Number> oxygenSeries;
    private ObservableList<String> categories;
    private int numCategories;
    private float updatesPerSecond;

    // so we can haz lambdas
    private static TimerTask wrap(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(r);
            }
        };
    }

    @Override
    public XYChart.Series<String, Number> getFoodSeries() {
        return foodSeries;
    }

    @Override
    public XYChart.Series<String, Number> getOxygenSeries() {
        return oxygenSeries;
    }

    @Override
    public ObservableList<String> getCategories() {
        return categories;
    }

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
                    XYChart.Data<String, Number> pointToBeMoved;
                    try {
                        pointToBeMoved = series.getData().get(i + 1);
                    } catch (IndexOutOfBoundsException e) {
                        // current task is still running while series are cleared and made shorter
                        // I COULD check if numCategories > series.getData().size().
                        // checking for an exception is a hack, but it is probably faster so I'll use it.
                        return;
                    }
                    XYChart.Data<String, Number> newPoint = new XYChart.Data<>(Float.toString((float) (numCategories - i - 1) / updatesPerSecond), pointToBeMoved.getYValue());
                    series.getData().set(i, newPoint);
                }
            }
            foodSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountFood.get()));
            oxygenSeries.getData().set(numCategories - 1, new XYChart.Data<>("0.0", amountOxygen.get()));
        });
        if (chartUpdateRateMs.get() > 0)
            timer.scheduleAtFixedRate(currentTask, chartUpdateRateMs.get(), chartUpdateRateMs.get());

    }

    @Override
    public void reload() {
        updatesPerSecond = (float) 1000 / (float) chartUpdateRateMs.get();
        numCategories = (int) (chartHistoryS.get() * updatesPerSecond) + 1;
        initialize(numCategories, updatesPerSecond);
    }

    @Override
    public void init() {
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
        reload();
    }
}
