package com.javaquarium.views.app.search;

import com.javaquarium.backend.aquarium.Fish;
import com.management.Presenter;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

@Presenter
public class SearchPresenter implements Initializable {

    private ArrayList<Predicate<Fish>> predicates = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
