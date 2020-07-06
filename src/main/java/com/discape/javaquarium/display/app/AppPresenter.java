package com.discape.javaquarium.display.app;

import com.discape.javaquarium.display.chart.ChartView;
import com.discape.javaquarium.display.fishtable.FishTableView;
import com.discape.javaquarium.display.search.SearchView;
import com.discape.javaquarium.display.toolbar.ToolbarView;
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
        // supa fast
        new ChartView().getViewAsync(chartPane.getChildren()::add);
        new SearchView().getViewAsync(searchPane.getChildren()::add);
        new ToolbarView().getViewAsync(toolbarPane.getChildren()::add);
        new FishTableView().getViewAsync(tablePane.getChildren()::add);
    }

}
