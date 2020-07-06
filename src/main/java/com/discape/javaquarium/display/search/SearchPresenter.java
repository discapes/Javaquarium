package com.discape.javaquarium.display.search;

import com.discape.javaquarium.logic.Fish;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SearchPresenter implements Initializable {

    private ArrayList<Predicate> predicates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        predicates.add(o -> false);

    }
}
