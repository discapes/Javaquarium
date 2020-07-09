package com.javaquarium.views.app.toolbar;

import com.javaquarium.Event;
import com.javaquarium.backend.aquarium.Fish;
import com.javaquarium.backend.services.*;
import com.management.Dependency;
import com.management.OnEvent;
import com.management.Presenter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

@Presenter
public class ToolbarPresenter {

    @Dependency private AquariumLoaderService aquariumLoaderService;
    @Dependency private AccountService accountService;
    @Dependency private StageService stageService;
    @Dependency private AlertService alertService;
    @Dependency private AquariumService aquariumService;

    @FXML private void nuke() {
        if (alertService.confirm("Delete all fish?"))
            aquariumService.getFish().clear();
    }

    @FXML private void addFish() {
        aquariumService.getFish().add(new Fish("New fish"));
    }

    @FXML private void openSettings() {
    //    stageService.getStage().setScene(LawnMower.getScene(SettingsView.class));
    }

    @FXML private void about() {
        alertService.inform("Javaquarium\n" +
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
        Optional<ButtonType> buttonPressed = alertService.inform(
                "Username: " + accountService.getUsername() + "\n" +
                        "Salt: " + accountService.getSalt() + "\n" +
                        "Hash: " + accountService.getHash(),
                "Account", logoutBtn, ButtonType.OK);
        if (buttonPressed.isPresent()) {
            if (buttonPressed.get() == logoutBtn) {
                accountService.logout();
              //  viewSetter.applyView(StartView.class);
            }
        }
    }



    @FXML private void saveToFile() {/*
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
        stage.showAndWait();*/
    }

   /* private void trySave(File file, String key, Stage stage) {
        try {
            aquariumLoaderService.save(file, key);
            alertService.inform("Saved aquarium to " + file.getPath() + (key.length()>0 ? " encrypted with key " + key : ""));
        } catch (IOException e) {
            alertService.errorAlert("Could not write to file: " + e);
            e.printStackTrace();
        }
        stage.close();
    }*/

    @FXML private void loadFromFile() {/*
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
        stage.showAndWait();*/
    }

    private void tryLoad(File file, String key, Stage stage) {/*
        try {
            aquariumLoaderService.load(file, key);
            alertService.inform("Loaded aquarium from " + file.getPath() + (key.length()>0 ? " encrypted with key " + key : ""));
        } catch (IOException e) {
            alertService.errorAlert("Could not read from file: " + e);
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            alertService.errorAlert("Wrong key or invalid data!");
            e.printStackTrace();
        }
        stage.close();*/
    }
}