package com.javaquarium.views.app.fishtable;

import com.javaquarium.Event;
import com.javaquarium.backend.CustomIntegerStringConverter;
import com.javaquarium.backend.Utils;
import com.javaquarium.backend.aquarium.Fish;
import com.javaquarium.backend.aquarium.FishSpecies;
import com.javaquarium.backend.services.AlertService;
import com.javaquarium.backend.services.AquariumService;
import com.javaquarium.backend.services.StageService;
import com.management.Dependency;
import com.management.OnEvent;
import com.management.Presenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));
        speciesCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(FishSpecies.values()));

        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

        healthCol.setCellValueFactory(new PropertyValueFactory<>("health"));
        healthCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorCol.setCellFactory(param -> new TableCell<>() {
            @Override protected void updateItem(Color color, boolean empty) {
                updateColor(color);
            }

            private void updateColor(Color color) {
                if (color != null) {
                    this.setText(Utils.colorToString(color));
                    setStyle("-fx-text-fill: " + Utils.colorToString(Color.color(
                            1-color.getRed(), 1-color.getGreen(), 1-color.getBlue())) + ";" + "-fx-background-color: " + Utils.colorToString(color));
                } else  {
                    setStyle("-fx-background-color: transparent");
                    setText(null);
                }
            }
        });
        colorCol.setOnEditStart(e -> {
           /* Fish fish = e.getRowValue();
            if (fish != null) {
                ColorPicker colorPicker = new ColorPicker();
                colorPicker.valueProperty().bindBidirectional(fish.colorProperty());
                Button closeBtn = new Button("Close");
                HBox hBox = new HBox(colorPicker, closeBtn);

                Stage stage = viewSetter.quickStage(hBox, "Change color");
                stage.initModality(Modality.APPLICATION_MODAL);
                closeBtn.setOnAction(saveEvt -> stage.close());
                stage.showAndWait();
            }*/
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
}
