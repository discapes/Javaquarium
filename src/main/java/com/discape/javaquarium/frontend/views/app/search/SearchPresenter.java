package com.discape.javaquarium.frontend.views.app.search;

import com.discape.javaquarium.backend.Fish;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SearchPresenter implements Initializable {

    private ArrayList<Predicate<Fish>> predicates = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
