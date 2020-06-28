package com.discape.javaquarium.gui;

import javafx.scene.Scene;

public interface IThemes {
    String getCurrentTheme();

    void setCurrentTheme(String currentTheme);

    String[] getThemes();

    Scene setTheme(Scene scene);
}
