package com.discape.javaquarium.gui;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Themes implements IThemes {

    private final JMetro jMetro;
    private final String[] themes = {
            "Light",
            "Dark",
            "Metro Dark",
            "Metro Light",
            "Old"
    };
    private String currentTheme = "Modena";

    public Themes() {
        jMetro = new JMetro();
        jMetro.automaticallyColorPanesProperty().set(true);
    }

    @Override
    public String getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    @Override
    public String[] getThemes() {
        return themes;
    }

    @Override
    public Scene setTheme(Scene scene) {
        StyleManager.getInstance().platformUserAgentStylesheetContainers.clear();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        jMetro.sceneProperty().set(null);

        switch (currentTheme) {
            case "Light":
                break;
            case "Old":
                Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
                break;
            case "Metro Dark":
                jMetro.setStyle(Style.DARK);
                jMetro.setScene(scene);
                jMetro.getOverridingStylesheets().add(getClass().getResource("/MetroDark.css").toString());
                break;
            case "Metro Light":
                jMetro.setStyle(Style.LIGHT);
                jMetro.setScene(scene);
                break;
            default:
                StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource("/" + currentTheme + ".css").toString());
        }
        currentTheme = currentTheme;
        return scene;
    }
}
