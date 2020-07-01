package com.discape.javaquarium.gui.toolbar;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Cryptographer;
import com.discape.javaquarium.business.model.Aquarium;
import com.discape.javaquarium.business.model.Fish;
import com.discape.javaquarium.gui.ChartDataUpdater;
import com.discape.javaquarium.gui.Stages;
import com.discape.javaquarium.gui.Themes;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;

public class ToolbarPresenter {

    @FXML private AnchorPane toolbarRoot;

    @Inject private Aquarium aquarium;

    @Inject private Themes themes;

    @Inject private Stages stages;

    @Inject private ChartDataUpdater chartDataUpdater;

    @Inject private Cryptographer cryptographer;

    @FXML
    private void resetChart() {
        chartDataUpdater.reload();
    }


    @FXML
    private void nuke() {
        if (Utils.confirm("Delete all fish?")) aquarium.getFishList().clear();
    }


    @FXML
    private void addFish() {
        aquarium.getFishList().add(new Fish("New fish"));
    }


    @FXML
    private void openSettings() {
        Stage stage = Utils.makeWindow(new SettingsView().getView(), "Settings");
        stages.setSettingsStage(stage);
        stage.showAndWait();
    }


    @FXML
    private void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Javaquarium\n" +
                "Written by Discape\n" +
                "Manage a virtual aquarium,\n" +
                "and make sure the fish have appropriate amounts\n" +
                "of food and oxygen.\n" +
                "In settings you can change the theme,\n" +
                "How fast the oxygen and food levels change,\n" +
                "How fast the chart updates and how much history it shows.\n" +
                "You can delete fish by pressing DELETE", ButtonType.OK);
        themes.setTheme(alert.getDialogPane().getScene());
        alert.setHeaderText("About");
        alert.show();
    }


    @FXML
    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());

        TextField keyField = new TextField();
        keyField.setPromptText("Key");
        Button button = new Button("Save");
        keyField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 0) button.setText("Encrypt");
            else button.setText("Save");
        });
        VBox vBox = new VBox(new Label("Key: "), keyField, button);
        Stage stage = Utils.makeWindow(vBox, "Save");
        Utils.setAnchors(vBox, 50);

        button.setOnAction(evt ->
                save(stage, keyField.getText(), file)
        );
        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                load(stage, keyField.getText(), file);
        });
        stage.showAndWait();
    }


    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        TextField keyField = new TextField();
        keyField.setPromptText("Key");
        Button button = new Button("Load");
        keyField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 0) button.setText("Decrypt");
            else button.setText("Load");
        });
        VBox vBox = new VBox(new Label("Key: "), keyField, button);
        Utils.setAnchors(vBox, 50);
        Stage stage = Utils.makeWindow(vBox, "Load");
        Utils.setAnchors(vBox, 50);

        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                load(stage, keyField.getText(), file);
            }
        });
        button.setOnAction(evt -> load(stage, keyField.getText(), file));
        stage.showAndWait();
    }

    private void save(Stage stage, String key, File file) {
        String str;
        if (key.length() > 0) {
            try {
                str = cryptographer.base64Encrypt(aquarium.toString(), key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                Utils.errorAlert("Wrong key or invalid data!");
                stage.close();
                return;
            } catch (InvalidKeyException e) {
                Utils.errorAlert("Invalid key!");
                stage.close();
                return;
            }
        } else {
            str = aquarium.toString();
        }

        try {
            Files.writeString(file.toPath(), str);
        } catch (IOException e) {
            Utils.errorAlert("Could not write to file: " + e.getMessage());
            stage.close();
            return;
        }
        Utils.inform("Saved aquarium to " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
        stage.close();
    }

    private void load(Stage stage, String key, File file) {
        String rawStr;
        try {
            rawStr = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            Utils.errorAlert("Could not read from file: " + e.getMessage());
            stage.close();
            return;
        }
        String aquariumStr = rawStr;
        if (key.length() > 0) {
            try {
                aquariumStr = cryptographer.base64Decrypt(rawStr, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                Utils.errorAlert("Wrong key or bad encryption!");
                stage.close();
                return;
            } catch (InvalidKeyException e) {
                Utils.errorAlert("Invalid key!");
                stage.close();
                return;
            } catch (IllegalArgumentException e) {
                Utils.errorAlert(e.getMessage());
                stage.close();
                return;
            }
        }
        try {
            Aquarium aquarium = Aquarium.fromString(aquariumStr);
            Injector.setModelOrService(Aquarium.class, aquarium);
            stages.reload();
        } catch (Exception e) {
            Utils.errorAlert("Invalid data: " + e.getMessage());
            stage.close();
            return;
        }
        Utils.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
        stage.close();
    }
}
