package com.discape.javaquarium.gui.table;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.Fish;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class TablePresenter implements Initializable {

    @Inject private Aquarium aquarium;

    @FXML private TableView<Fish> tableView;
    @FXML private TableColumn<Fish, String> nameCol;
    @FXML private TableColumn<Fish, String> speciesCol;
    @FXML private TableColumn<Fish, Integer> speedCol;
    @FXML private TableColumn<Fish, String> colorCol;
    @FXML private TableColumn<Fish, Integer> saturationCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        speciesCol.setCellValueFactory(cell -> Bindings.createObjectBinding(() ->
                cell.getValue().speciesProperty().get().getName(), cell.getValue().speciesProperty()));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        saturationCol.setCellValueFactory(new PropertyValueFactory<>("saturation"));

        colorCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty) setStyle("-fx-background-color: transparent");
                else {
                    int currentIndex = indexProperty().getValue();
                    Color color = param.getTableView().getItems().get(currentIndex).getColor();
                    setStyle("-fx-background-color: " + Utils.colorToString(color));
                }
            }
        });

        tableView.setItems(aquarium.getFishList());
    }
}
