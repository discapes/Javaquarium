package com.discape.javaquarium.display;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.display._pages.Page;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

public class StageUtilities {

    @Inject Alerts alerts;
    @Inject private ThemeManager themeManager;
    @Inject private Stage stage;
    private Stage temporaryStage;

    public Stage getStage() {
        return stage;
    }

    public void setPage(Page page, Stage stage) {
        Parent root = page.getView();
        Scene scene = new Scene(root);
        themeManager.applyTheme(scene);
        stage.setScene(scene);
        page.modifyStage(stage);
    }

    public void setPage(Page page) {
        setPage(page, stage);
    }

    public Stage newTemporaryStage(Page page) {
        temporaryStage = new Stage();
        setPage(page, temporaryStage);
        return temporaryStage;
    }

    public Stage newStage(Page page) {
        Stage stage = new Stage();
        setPage(page, stage);
        return stage;
    }

    public Stage quickStage(Parent parent, String title) {
        Stage stage = newStage(new Page() {
            @Override
            public Parent getView() {
                Utils.setAnchors(parent, 50);
                return new AnchorPane(parent);
            }

            @Override
            public void modifyStage(Stage stage) {
                stage.setTitle(title);
            }
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    public void setTemporaryPage(Page page) {
        setPage(page, temporaryStage);
    }
}
