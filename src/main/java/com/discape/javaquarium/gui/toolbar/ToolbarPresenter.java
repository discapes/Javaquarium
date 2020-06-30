package com.discape.javaquarium.gui.toolbar;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.AquariumFile;
import com.discape.javaquarium.business.Fish;
import com.discape.javaquarium.gui.ChartDataUpdater;
import com.discape.javaquarium.gui.Stages;
import com.discape.javaquarium.gui.Themes;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class ToolbarPresenter {

    @FXML private AnchorPane toolbarRoot;

    @Inject private Aquarium aquarium;

    @Inject private Themes themes;

    @Inject private Stages stages;

    @Inject private ChartDataUpdater IChartDataUpdater;

    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        //noinspection CatchMayIgnoreException
        try {
            Injector.setModelOrService(Aquarium.class, AquariumFile.getAquarium(selectedFile));
        } catch (Exception e) { if (!(e instanceof NullPointerException)) Utils.errorAlert("Invalid file"); }
        stages.reload();
    }

    @FXML
    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        AquariumFile.setAquarium(aquarium, selectedFile);
    }

    @FXML
    private void openSettings() {
        Stage stage = Utils.makeWindow(new SettingsView().getView(), "Settings");
        stages.setSettingsStage(stage);
        stage.showAndWait();
    }

    @FXML
    private void resetChart() {
        IChartDataUpdater.reload();
    }


    public void about() {
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
    private void nuke() {
        if (Utils.confirm("Delete all fish?")) aquarium.getFishList().clear();
    }

    @FXML
    private void addFish() { aquarium.getFishList().add(new Fish("New fish")); }
}
