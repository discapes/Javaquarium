package com.javaquarium.views.app.chart;

import com.firework.AfterInjection;
import com.firework.Dependency;
import com.firework.OnEvent;
import com.firework.Service;
import com.javaquarium.Events;
import com.javaquarium.backend.services.AquariumDataService;
import com.javaquarium.backend.services.SettingService;
import com.javaquarium.backend.services.ThemeService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.collections.FXCollections.observableArrayList;

@Service
public class ChartPresenter implements Initializable {

    private final Timer timer = new Timer(true);
    private final ObservableList<XYChart.Data<String, Number>> paddingData1 = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> paddingData2 = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> oxygenData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> foodData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> topData = observableArrayList();
    private final ObservableList<XYChart.Data<String, Number>> bottomData = observableArrayList();
    private final ObservableList<String> categories = observableArrayList();
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis categoryAxis;
    @FXML private Button plusOxygenBtn;
    @FXML private Button minusOxygenBtn;
    @FXML private Button plusFoodBtn;
    @FXML private Button minusFoodBtn;
    private TimerTask timerTask = null;
    private int points;
    private double pointsPerSecond;
    private ReadOnlyDoubleProperty oxygen;
    private ReadOnlyDoubleProperty food;

    @Dependency private ThemeService themeService;
    @Dependency private AquariumDataService aquariumDataService;

    @Override public void initialize(URL url, ResourceBundle rb) {
        categoryAxis.setCategories(getCategories());

        for (ObservableList<XYChart.Data<String, Number>> data : getData()) {
            lineChart.getData().add(new XYChart.Series<>(data));
        }

        lineChart.getData().get(2).setName("Oxygen");
        lineChart.getData().get(3).setName("Food");

        aquariumDataService.getOxygen().addListener((observableValue, number, t1) ->
                valueChange((double) t1, plusOxygenBtn, minusOxygenBtn));
        aquariumDataService.getFood().addListener((observableValue, number, t1) ->
                valueChange((double) t1, plusFoodBtn, minusFoodBtn));

        //Thread.setDefaultUncaughtExceptionHandler((t, e) -> {});
    }

    private void valueChange(double val, Button plusBtn, Button minusBtn) {
        if (val > 150)
            minusBtn.setStyle("-fx-background-color: " + (SettingService.theme.contains("Dark") ? "darkred" : "palevioletred"));
        else if (val < 50)
            plusBtn.setStyle("-fx-background-color: " + (SettingService.theme.contains("Dark") ? "darkred" : "palevioletred"));
        else {
            plusBtn.setStyle("");
            minusBtn.setStyle("");
        }
    }

    @FXML
    private void increaseOxygen() {
        aquariumDataService.addOxygen(10);
    }

    @FXML
    private void decreaseOxygen() {
        aquariumDataService.addOxygen(-10);
    }

    @FXML
    private void increaseFood() {
        aquariumDataService.addFood(10);
    }

    @FXML
    private void decreaseFood() {
        aquariumDataService.addFood(-10);
    }

    @OnEvent(Events.LOGOUT)
    private void close() {
        timerTask.cancel();
        timerTask = null;
        categories.clear();
        getData().forEach(List::clear);
    }

    @OnEvent(Events.CHARTSETTINGCHANGE)
    private void reset() {
        close();
        fillChart();
        startUpdater();
    }

    @OnEvent(Events.PRELOAD)
    private void fillChart() {
        points = SettingService.prettyChartPoints + 1;
        pointsPerSecond = (points - 1) / (double) SettingService.chartHistory;

        for (int i = 0; i < points; i++) {
            int indexFromRight = points - i - 1;
            String indexInSeconds = Double.toString((double) indexFromRight / pointsPerSecond);
            categories.add(indexInSeconds);
            oxygenData.add(new XYChart.Data<>(indexInSeconds, aquariumDataService.getOxygen().get()));
            foodData.add(new XYChart.Data<>(indexInSeconds, aquariumDataService.getFood().get()));
            topData.add(new XYChart.Data<>(indexInSeconds, 150));
            bottomData.add(new XYChart.Data<>(indexInSeconds, 50));
        }
        paddingData1.add(new XYChart.Data<>("range", 200));
    }

    @OnEvent(Events.LOGIN)
    private void startUpdater() {
        assert timerTask == null;
        timerTask = newTimerTask();
        int updateRateMs = (int) (1000 / pointsPerSecond);
        if (SettingService.prettyChartPoints > 0)
            timer.scheduleAtFixedRate(timerTask, updateRateMs, updateRateMs);
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
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
        oxygen = aquariumDataService.getOxygen();
        food = aquariumDataService.getFood();
    }
}
