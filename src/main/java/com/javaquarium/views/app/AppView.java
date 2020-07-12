package com.javaquarium.views.app;

import com.firework.SceneView;
import com.javaquarium.views.StandaloneView;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.awt.*;


/** Main view of the application. */
@SceneView
public class AppView extends StandaloneView {

    @Override
    public Parent getRoot() {
        AnchorPane root = (AnchorPane) super.getRoot();
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
        root.setPrefHeight(height);
        root.setPrefWidth(width);
        return root;
    }
}
