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
        tablePane.getChildren().add(new FishTableView().getRoot());
        searchPane.getChildren().add(new SearchView().getRoot());
        chartPane.getChildren().add(new ChartView().getRoot());
        toolbarPane.getChildren().add(new ToolbarView().getRoot());


        // supa fast
        /*
        new ChartView().getViewAsync(chartPane.getChildren()::add);*/
        //    new SearchView().getViewAsync(searchPane.getChildren()::add);
        //    new ToolbarView().getViewAsync(toolbarPane.getChildren()::add);
        //     new FishTableView().getViewAsync(tablePane.getChildren()::add);
    }

}
