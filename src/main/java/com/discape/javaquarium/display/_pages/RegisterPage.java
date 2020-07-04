package com.discape.javaquarium.display._pages;

import com.discape.javaquarium.Utils;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage extends Page {

    @Override
    public Parent getView() {
        VBox vBox = new VBox(new Label("Register"));
        Utils.setAnchors(vBox, 50);
        return new AnchorPane(vBox);
    }

    @Override
    public void modifyStage(Stage stage) {
        stage.setTitle("Register");
    }
}
