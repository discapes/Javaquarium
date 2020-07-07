package com.discape.javaquarium.frontend.persistent;

import javafx.scene.Parent;
import javafx.stage.Stage;


public interface IMainView {
    void modifyStage(Stage stage);
    Parent getRoot();
}
