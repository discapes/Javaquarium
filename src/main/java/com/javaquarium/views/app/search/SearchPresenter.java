package com.javaquarium.views.app.search;

import com.firework.Dependency;
import com.javaquarium.backend.Fish;
import com.javaquarium.backend.services.AquariumDataService;
import com.sun.javafx.property.PropertyReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SearchPresenter implements Initializable {

    private final BooleanProperty isDefaultPredicate = new SimpleBooleanProperty();
    @FXML private TextField criteriaField;
    @FXML private ChoiceBox<String> columnChooser;
    @FXML private Button andBtn;
    @FXML private Button orBtn;
    @FXML private Button resetBtn;
    @Dependency private AquariumDataService aquariumDataService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isDefaultPredicate.set(true);
        columnChooser.setItems(FXCollections.observableArrayList("name", "species", "speed", "color", "health"));
        resetBtn.disableProperty().bind(isDefaultPredicate);
        orBtn.disableProperty().bind(
                isDefaultPredicate
                        .or(columnChooser.valueProperty().isNull())
                        .or(criteriaField.textProperty().isEmpty()));
        andBtn.disableProperty().bind(
                columnChooser.valueProperty().isNull()
                        .or(criteriaField.textProperty().isEmpty()));
    }

    @FXML
    private void and() {
        isDefaultPredicate.set(false);
        setPredicate(getPredicate().and(newPredicate()));
    }

    @FXML
    private void or() {
        isDefaultPredicate.set(false);
        setPredicate(getPredicate().or(newPredicate()));
    }

    @FXML
    private void reset() {
        isDefaultPredicate.set(true);
        setPredicate((f) -> true);
    }

    private Predicate<Fish> newPredicate() {
        return new SearchPredicate<>(criteriaField.getText(), new PropertyReference<>(Fish.class, columnChooser.getValue()));
    }

    @SuppressWarnings("unchecked")
    private Predicate<Fish> getPredicate() {
        return (Predicate<Fish>) aquariumDataService.getVisibleFish().getPredicate();
    }

    private void setPredicate(Predicate<Fish> predicate) {
        aquariumDataService.getVisibleFish().setPredicate(predicate);
    }

    private static class SearchPredicate<Fish> implements Predicate<Fish> {
        private final String regex;
        private final PropertyReference<Fish> propertyReference;

        public SearchPredicate(String regex, PropertyReference<Fish> propertyReference) {
            this.regex = regex;
            this.propertyReference = propertyReference;
        }

        @Override
        public boolean test(Fish fish) {
            String value = propertyReference.get(fish).toString();
            return value.matches(regex);
        }
    }
}
