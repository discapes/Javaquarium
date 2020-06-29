package com.discape.javaquarium.gui;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public interface IChartDataUpdater {
    XYChart.Series<String, Number> getFoodSeries();

    XYChart.Series<String, Number> getOxygenSeries();

    ObservableList<String> getCategories();

    void reload();

    void init();
}
