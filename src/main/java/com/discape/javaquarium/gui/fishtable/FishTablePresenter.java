package com.discape.javaquarium.gui.fishtable;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.Fish;
import com.discape.javaquarium.business.FishSpecies;
import com.discape.javaquarium.gui.CustomIntegerStringConverter;
import com.discape.javaquarium.gui.Stages;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class FishTablePresenter implements Initializable {

    @Inject private Aquarium aquarium;

    @FXML private TableView<Fish> tableView;
    @FXML private TableColumn<Fish, String> nameCol;
    @FXML private TableColumn<Fish, FishSpecies> speciesCol;
    @FXML private TableColumn<Fish, Integer> speedCol;
    @FXML private TableColumn<Fish, Void> colorCol;
    @FXML private TableColumn<Fish, Integer> saturationCol;

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

        AtomicBoolean pickerOpen = new AtomicBoolean(false);
        colorCol.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    if (empty) {
                        setBackground(Background.EMPTY);
                        return;
                    }

                    Fish fish = param.getTableView().getItems().get(indexProperty().getValue());

                    ObjectProperty<Color> color = fish.colorProperty();
                    color.addListener((observable, oldVal, newVal) -> {
                        setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
                    });
                    setBackground(new Background(new BackgroundFill(color.get(), CornerRadii.EMPTY, Insets.EMPTY)));
                }
        });
        colorCol.setOnEditStart(e -> {
            Fish fish = e.getRowValue();
            if (!pickerOpen.get() && fish != null) {
                ColorPicker colorPicker = new ColorPicker();
                colorPicker.valueProperty().bindBidirectional(fish.colorProperty());

                Button closeBtn = new Button("Close");
                HBox hBox = new HBox(colorPicker, closeBtn);

                pickerOpen.set(true);
                Stage stage = Utils.makeWindow(hBox, "Change color");
                closeBtn.setOnAction(saveEvt -> {
                    stage.close();
                });
                stage.showAndWait();
                pickerOpen.set(false);
            }
        });

        tableView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Fish fish = tableView.getSelectionModel().getSelectedItem();
                if (fish == null) return;
                if (Utils.confirm("Are you sure you want to delete " + fish.getName() + "?"))
                    aquarium.getFishList().remove(fish);
            }
        });
        tableView.setItems(aquarium.getFishList());
    }
}
