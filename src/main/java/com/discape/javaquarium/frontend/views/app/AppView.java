package com.discape.javaquarium.frontend.views.app;

import com.airhacks.afterburner.views.FXMLView;
import com.discape.javaquarium.backend.Alerts;
import com.discape.javaquarium.backend.SessionManager;
import com.discape.javaquarium.frontend.persistent.IMainView;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.awt.*;

public class AppView extends FXMLView implements IMainView {

    @Inject private Alerts alerts;
    @Inject private SessionManager sessionManager;

    private final Parent root;

    public AppView() {
        root = super.getView();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double scrWidth = screenSize.getWidth();
        double scrHeight = screenSize.getHeight();
        int height = 1000;
        int width = 1000;
        if (1000 > scrHeight) {
            height = (int)scrHeight - 100;
        }
        if (1000 > scrWidth) {
            width = (int)scrWidth - 100;
        }
        ((AnchorPane) root).setPrefHeight(height);
        ((AnchorPane) root).setPrefWidth(width);
    }

    @Override public Parent getRoot() {
        return root;
    }

    @Override public void modifyStage(Stage stage) {
        stage.setTitle("Javaquarium");
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            if (!alerts.confirm("Close?")) e.consume();
            sessionManager.quit();
        });
    }

}
