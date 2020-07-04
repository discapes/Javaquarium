package com.discape.javaquarium.display._pages;

import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class Page {
    public abstract Parent getView();

    public abstract void modifyStage(Stage stage);
}
