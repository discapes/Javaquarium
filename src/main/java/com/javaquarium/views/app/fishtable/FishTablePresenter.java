package com.javaquarium.views.app.fishtable;

import com.javaquarium.backend.*;
import com.javaquarium.backend.services.AlertService;
import com.javaquarium.backend.services.AquariumService;
import com.javaquarium.backend.services.StageService;
import com.management.Dependency;
import com.management.Presenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Presenter
public class FishTablePresenter implements Initializable {

    @FXML private TableView<Fish> tableView;
    @FXML private TableColumn<Fish, String> nameCol;
    @FXML private TableColumn<Fish, FishSpecies> speciesCol;
    @FXML private TableColumn<Fish, Integer> speedCol;
    @FXML private TableColumn<Fish, Color> colorCol;
    @FXML private TableColumn<Fish, Integer> healthCol;

    @Dependency private AquariumService aquariumService;
    @Dependency private AlertService alertService;
    @Dependency private StageService stageService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomStringConverter()));

        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));
        speciesCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(FishSpecies.values()));

        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

        healthCol.setCellValueFactory(new PropertyValueFactory<>("health"));
        healthCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Color color, boolean empty) {
                updateColor(color);
            }

            private void updateColor(Color color) {
                if (color != null) {
                    this.setText(Utils.colorToString(color));
                    setStyle("-fx-text-fill: " + Utils.colorToString(Color.color(
                            1 - color.getRed(), 1 - color.getGreen(), 1 - color.getBlue())) + ";" + "-fx-background-color: " + Utils.colorToString(color));
                } else {
                    setStyle("-fx-background-color: transparent");
                    setText(null);
                }
            }
        });
        colorCol.setOnEditStart(e -> {
            Fish fish = e.getRowValue();
            if (fish != null) {
                ColorPicker colorPicker = new ColorPicker();
                colorPicker.valueProperty().bindBidirectional(fish.colorProperty());
                Button closeBtn = new Button("Close");
                HBox hBox = new HBox(colorPicker, closeBtn);

                Stage stage = stageService.quickStage(hBox, "Change color");
                stage.initModality(Modality.APPLICATION_MODAL);
                closeBtn.setOnAction(saveEvt -> stage.close());
                stage.showAndWait();
            }
        });

        tableView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Fish fish = tableView.getSelectionModel().getSelectedItem();
                if (fish == null) return;
                if (alertService.confirm("Are you sure you want to delete " + fish.getName() + "?"))
                    aquariumService.getFish().remove(fish);
            }
        });
        tableView.setItems(aquariumService.getFish());
    }

    private static class CustomStringConverter extends StringConverter<String> {
        @Override
        public String toString(String o) {
            return o;

        }

        @Override
        public String fromString(String s) {
            return s.replace('%', ' ');
        }
    }

    private static class CustomIntegerStringConverter extends IntegerStringConverter {
        private final IntegerStringConverter converter = new IntegerStringConverter();

        /**
         * Converts a string to an integer, and handles an invalid string as follows:
         * - If the string contains letters, return 0.
         * - Else if the string starts with a -, return Integer.MIN_VALUE.
         * - Else return Integer.MAX_VALUE.
         *
         * @param string to be converted.
         * @return An integer.
         */
        @Override public Integer fromString(String string) {
            try {
                return converter.fromString(string);
            } catch (NumberFormatException ignored) {
            }

            /* if letters */
            String rest = string.substring(1);
            if (!Pattern.compile("-?\\d+(\\.\\d+)?").matcher(rest).matches())
                return 0;

            if (string.charAt(0) == '-')
                return Integer.MIN_VALUE;

            return Integer.MAX_VALUE;

        }
    }

}
