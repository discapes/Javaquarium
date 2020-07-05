package com.discape.javaquarium.display.toolbar;

import com.discape.javaquarium.display.Alerts;
import com.discape.javaquarium.display.StageUtilities;
import com.discape.javaquarium.display._pages.LoginRegisterPage;
import com.discape.javaquarium.display._pages.MainPage;
import com.discape.javaquarium.display._pages.SettingsPage;
import com.discape.javaquarium.logic.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

public class ToolbarPresenter {

    @Inject private StageUtilities stageUtilities;

/******************************************************************************/

    @Inject private ChartDataUpdater chartDataUpdater;
    @FXML private void resetChart() {
        chartDataUpdater.reload();
    }
/******************************************************************************/

    @Inject private Aquarium aquarium;

    @FXML private void nuke() {
        if (alerts.confirm("Delete all fish?"))
            aquarium.getFishList().clear();
    }
    @FXML private void addFish() {
        aquarium.getFishList().add(new Fish("New fish"));
    }
/******************************************************************************/

    @Inject private SettingsPage settingsPage;
    @FXML private void openSettings() {
        stageUtilities.newTemporaryStage(settingsPage).showAndWait();
    }
/******************************************************************************/

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
/******************************************************************************/

    @Inject private AccountManager accountManager;
    @Inject private LoginRegisterPage loginRegisterPage;
    @Inject private Session session;
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
                session.stop();
                stageUtilities.setPage(loginRegisterPage);
            }
        }
    }
/******************************************************************************/

    @Inject private AquariumFile aquariumFile;
    @Inject private MainPage mainPage;

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
        Stage stage = stageUtilities.quickStage(vBox, "Key?");

        button.setOnAction(evt -> {
                    aquariumFile.save(keyField.getText(), file);
                    stage.close();
                }
        );
        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                aquariumFile.save(keyField.getText(), file);
                stage.close();
            }
        });
        stage.showAndWait();
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
        Stage stage = stageUtilities.quickStage(vBox, "Key?");

        keyField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                aquariumFile.setKey(keyField.getText());
                aquariumFile.setFile(file);
                session.stop();
                stageUtilities.setPage(mainPage);
                stage.close();
            }
        });
        button.setOnAction(evt -> {
            aquariumFile.setKey(keyField.getText());
            aquariumFile.setFile(file);
            session.stop();
            stageUtilities.setPage(mainPage);
            stage.close();
        });
        stage.showAndWait();
    }
}