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
import javafx.beans.property.StringProperty;
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
import java.util.Optional;

public class ToolbarPresenter {

    @FXML private AnchorPane toolbarRoot;

    @Inject private Aquarium aquarium;

    @Inject private Themes themes;

    @Inject private Stages stages;

    @Inject private ChartDataUpdater chartDataUpdater;

    @Inject private Cryptographer cryptographer;

    @Inject private StringProperty accountLine;

    @FXML
    private void resetChart() {
        //chartDataUpdater.reload();
        String out = cryptographer.saltHashPassword("MYPASSWD");
        System.out.println(out);
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
    private void account() {
        ButtonType logoutBtn = new ButtonType("Logout", ButtonBar.ButtonData.CANCEL_CLOSE);
        String acc = accountLine.get();
        String username = acc.substring(0, acc.indexOf(" "));
        String salt = acc.substring(acc.indexOf(' ') + 1, acc.lastIndexOf(' '));
        String hash = accountLine.get().substring(acc.lastIndexOf(' ') + 1, acc.length());
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Username: " + username + "\n" +
                        "Salt: " + salt + "\n" +
                        "Hash: " + hash, logoutBtn, ButtonType.OK);
        themes.setTheme(alert.getDialogPane().getScene());
        alert.setHeaderText("Account");
        Optional<ButtonType> buttonPressed = alert.showAndWait();
        if(buttonPressed.isPresent())
            if (buttonPressed.get() == logoutBtn) {
                System.out.println("LOGOUT");
                // TODO
            }
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

        button.setOnAction(evt -> {
                    save(keyField.getText(), file);
                    stage.close();
                }
        );
        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                save(keyField.getText(), file);
                stage.close();
            }
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
        Stage stage = Utils.makeWindow(vBox, "Load");

        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                load(keyField.getText(), file);
                stage.close();
            }
        });
        button.setOnAction(evt -> {
            load(keyField.getText(), file);
            stage.close();
        });
        stage.showAndWait();
    }

    private void save(String key, File file) {
        String str;
        if (key.length() > 0) {
            try {
                str = cryptographer.encrypt(aquarium.toString(), key);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                Utils.errorAlert("Error: " + e.getMessage());
                return;
            }
        } else {
            str = aquarium.toString();
        }

        try {
            Files.writeString(file.toPath(), str);
        } catch (IOException e) {
            Utils.errorAlert("Could not write to file: " + e.getMessage());
            return;
        }
        Utils.inform("Saved aquarium to " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
    }

    private void load(String key, File file) {
        String rawStr;
        try {
            rawStr = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            Utils.errorAlert("Could not read from file: " + e.getMessage());
            return;
        }
        String aquariumStr = rawStr;
        if (key.length() > 0) {
            try {
                aquariumStr = cryptographer.decrypt(rawStr, key);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                Utils.errorAlert("Wrong key or bad encryption!");
                return;
            } catch (InvalidKeyException e) {
                Utils.errorAlert("Invalid key!");
                return;
            } catch (IllegalArgumentException e) {
                Utils.errorAlert(e.getMessage());
                return;
            }
        }
        try {
            Aquarium aquarium = Aquarium.fromString(aquariumStr);
            Injector.setModelOrService(Aquarium.class, aquarium);
            stages.reload();
        } catch (Exception e) {
            Utils.errorAlert("Invalid data: " + e.getMessage());
            return;
        }
        Utils.inform("Loaded aquarium from " + file.getPath() + (key.length() > 0 ? " encrypted with " + key : ""));
    }
}
