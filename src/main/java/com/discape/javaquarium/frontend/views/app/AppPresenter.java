package com.discape.javaquarium.frontend.views.app;

import com.discape.javaquarium.frontend.views.app.chart.ChartView;
import com.discape.javaquarium.frontend.views.app.fishtable.FishTableView;
import com.discape.javaquarium.frontend.views.app.search.SearchView;
import com.discape.javaquarium.frontend.views.app.toolbar.ToolbarView;
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
        chartPane.getChildren().add(new ChartView().getView());
        searchPane.getChildren().add(new SearchView().getView());
        toolbarPane.getChildren().add(new ToolbarView().getView());
        tablePane.getChildren().add(new FishTableView().getView());

        // supa fast
        /*
        new ChartView().getViewAsync(chartPane.getChildren()::add);
        new SearchView().getViewAsync(searchPane.getChildren()::add);
        new ToolbarView().getViewAsync(toolbarPane.getChildren()::add);
        new FishTableView().getViewAsync(tablePane.getChildren()::add);*/
    }

}
