package com.javaquarium.views.app;

import com.javaquarium.views.app.chart.ChartView;
import com.javaquarium.views.app.fishtable.FishTableView;
import com.javaquarium.views.app.search.SearchView;
import com.javaquarium.views.app.toolbar.ToolbarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppPresenter implements Initializable {

    @FXML private AnchorPane searchPane;
    @FXML private AnchorPane chartPane;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane toolbarPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
/*
        tablePane.getChildren().add(new FishTableView().getRoot());
        searchPane.getChildren().add(new SearchView().getRoot());
        chartPane.getChildren().add(new ChartView().getRoot());
        toolbarPane.getChildren().add(new ToolbarView().getRoot());
*/
        /* (OMG SO FAAAST) */
        new ChartView().getRootAsync(chartPane.getChildren()::add);
        new SearchView().getRootAsync(searchPane.getChildren()::add);
        new ToolbarView().getRootAsync(toolbarPane.getChildren()::add);
        new FishTableView().getRootAsync(tablePane.getChildren()::add);
    }

}
