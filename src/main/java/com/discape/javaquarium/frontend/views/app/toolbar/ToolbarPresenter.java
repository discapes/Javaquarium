package com.discape.javaquarium.frontend.views.app.toolbar;

import com.discape.javaquarium.backend.AccountManager;
import com.discape.javaquarium.backend.Alerts;
import com.discape.javaquarium.backend.listeners.ChartDataUpdater;
import com.discape.javaquarium.backend.Fish;
import com.discape.javaquarium.backend.aquarium.Aquarium;
import com.discape.javaquarium.backend.listeners.AquariumLoader;
import com.discape.javaquarium.frontend.persistent.IViewSetter;
import com.discape.javaquarium.frontend.views.StartView;
import com.discape.javaquarium.frontend.views.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ToolbarPresenter {

    @Inject private AquariumLoader aquariumLoader;
    @Inject private AccountManager accountManager;
    @Inject private Aquarium aquarium;
    @Inject private IViewSetter viewSetter;
    @Inject private ChartDataUpdater chartDataUpdater;

    @FXML private void nuke() {
        if (alerts.confirm("Delete all fish?"))
            aquarium.getFish().clear();
    }

    @FXML private void addFish() {
        aquarium.getFish().add(new Fish("New fish"));
    }

    @FXML private void openSettings() {
        viewSetter.applySecondaryView(SettingsView.class);
    }

    @Inject private Alerts alerts;
    @FXML private void about() {
        alerts.inform("Javaquarium\n" +
                "\n" +
                "Manage a virtual aquarium,\n" +
                "and make sure the fish have appropriate amounts\n" +
                "of food and oxygen.\n" +
                "In settings you can change the theme,\n" +
                "How fast the oxygen and food levels change,\n" +
                "How fast the chart updates and how much history it shows.\n" +
                "You can delete fish by pressing DELETE", "About");
    }

    @FXML private void account() {
        ButtonType logoutBtn = new ButtonType("Logout", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> buttonPressed = alerts.inform(
                "Username: " + accountManager.getUsername() + "\n" +
                        "Salt: " + accountManager.getSalt() + "\n" +
                        "Hash: " + accountManager.getHash(),
                "Account", logoutBtn, ButtonType.OK);
        if (buttonPressed.isPresent()) {
            if (buttonPressed.get() == logoutBtn) {
                accountManager.logout();
                viewSetter.applyView(StartView.class);
            }
        }
    }



    @FXML private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file == null) return;

        TextField keyField = new TextField();
        keyField.setPromptText("Key");
        Button button = new Button("Save");
        keyField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 0) button.setText("Encrypt");
            else button.setText("Save");
        });

        VBox vBox = new VBox(new Label("Key: "), keyField, button);
        Stage stage = viewSetter.quickStage(vBox, "Key?");

        button.setOnAction(evt -> trySave(file, keyField.getText(), stage)
        );
        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) trySave(file, keyField.getText(), stage);
        });
        stage.showAndWait();
    }

    private void trySave(File file, String key, Stage stage) {
        try {
            aquariumLoader.save(file, key);
            alerts.inform("Saved aquarium to " + file.getPath() + (key.length()>0 ? " encrypted with key " + key : ""));
        } catch (IOException e) {
            alerts.errorAlert("Could not write to file: " + e);
            e.printStackTrace();
        }
        stage.close();
    }

    @FXML private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) return;

        TextField keyField = new TextField();
        keyField.setPromptText("Key");
        Button button = new Button("Load");
        keyField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 0) button.setText("Decrypt");
            else button.setText("Load");
        });

        VBox vBox = new VBox(new Label("Key: "), keyField, button);
        Stage stage = viewSetter.quickStage(vBox, "Key?");

        keyField.setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.ENTER) tryLoad(file, keyField.getText(), stage);
        });
        button.setOnAction(evt -> tryLoad(file, keyField.getText(), stage));
        stage.showAndWait();
    }

    private void tryLoad(File file, String key, Stage stage) {
        try {
            aquariumLoader.load(file, key);
            alerts.inform("Loaded aquarium from " + file.getPath() + (key.length()>0 ? " encrypted with key " + key : ""));
        } catch (IOException e) {
            alerts.errorAlert("Could not read from file: " + e);
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            alerts.errorAlert("Wrong key or invalid data!");
            e.printStackTrace();
        }
        stage.close();
    }
}