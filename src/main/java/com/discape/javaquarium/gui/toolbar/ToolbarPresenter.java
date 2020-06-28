package com.discape.javaquarium.gui.toolbar;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Javaquarium;
import com.discape.javaquarium.business.Aquarium;
import com.discape.javaquarium.business.AquariumFile;
import com.discape.javaquarium.gui.IThemes;
import com.discape.javaquarium.gui.JavaquariumApplication;
import com.discape.javaquarium.gui.Stages;
import com.discape.javaquarium.gui.chart.ChartDataUpdater;
import com.discape.javaquarium.gui.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class ToolbarPresenter {

    @Inject
    private Aquarium aquarium;

    @Inject private IThemes themes;

    @Inject private Stages stages;

    @Inject private ChartDataUpdater chartDataUpdater;
    
    @FXML private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        try {
            Injector.setModelOrService(Aquarium.class, AquariumFile.getAquarium(selectedFile));
        } catch (IOException e) { e.printStackTrace(); }
        stages.reload();
    }

    @FXML private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        try {
            AquariumFile.setAquarium(aquarium, selectedFile);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void openSettings() {
        Stage settingsStage = new Stage();
        stages.setSettingsStage(settingsStage);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(new SettingsView().getView());
        themes.setTheme(scene);
        settingsStage.setScene(scene);
        settingsStage.showAndWait();
    }

    @FXML private void nukeFish() {
        aquarium.getFishList().clear();
    }

    @FXML private void resetChart() {
        chartDataUpdater.reload();
    }

}
