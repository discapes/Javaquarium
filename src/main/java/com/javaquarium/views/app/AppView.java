package com.javaquarium.views.app;

import com.management.FXMLView;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.awt.*;

public class AppView extends FXMLView {

    @Override
    public Parent getView() {
        AnchorPane root = (AnchorPane) super.getView();
        /*
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
        ((AnchorPane) root).setPrefWidth(width);*/
        return root;
    }
}
