package com.discape.javaquarium.display.fishtable;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.StageUtilities;
import com.discape.javaquarium.display.ThemeManager;
import com.discape.javaquarium.logic.Aquarium;
import com.discape.javaquarium.logic.CustomIntegerStringConverter;
import com.discape.javaquarium.logic.Fish;
import com.discape.javaquarium.logic.FishSpecies;
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

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FishTablePresenter implements Initializable {

    @Inject private Aquarium aquarium;

    @FXML private TableView<Fish> tableView;
    @FXML private TableColumn<Fish, String> nameCol;
    @FXML private TableColumn<Fish, FishSpecies> speciesCol;
    @FXML private TableColumn<Fish, Integer> speedCol;
    @FXML private TableColumn<Fish, Color> colorCol;
    @FXML private TableColumn<Fish, Integer> saturationCol;

    @Inject private ThemeManager themeManager;
    @Inject private Alerts alerts;
    @Inject private StageUtilities stageUtilities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));
        speciesCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(FishSpecies.values()));

        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

        saturationCol.setCellValueFactory(new PropertyValueFactory<>("saturation"));
        saturationCol.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));

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
                            1-color.getRed(), 1-color.getGreen(), 1-color.getBlue())) + ";" + "-fx-background-color: " + Utils.colorToString(color));
                } else  {
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

                Stage stage = stageUtilities.quickStage(hBox, "Change color");
                stage.initModality(Modality.APPLICATION_MODAL);
                closeBtn.setOnAction(saveEvt -> stage.close());
                stage.showAndWait();
            }
        });

        tableView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Fish fish = tableView.getSelectionModel().getSelectedItem();
                if (fish == null) return;
                if (alerts.confirm("Are you sure you want to delete " + fish.getName() + "?"))
                    aquarium.getFishList().remove(fish);
            }
        });
        tableView.setItems(aquarium.getFishList());
    }
}
