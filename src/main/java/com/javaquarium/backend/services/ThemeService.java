package com.javaquarium.backend.services;

import com.firework.Service;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.net.URL;

@Service
public class ThemeService {

    private final JMetro jMetro;
    private final String[] themes = {
            "Light",
            "Dark",
            "Metro Dark",
            "Metro Light",
            "Old"
    };
    private String currentTheme = SettingsService.defaultTheme;

    public ThemeService() {
        jMetro = new JMetro();
        jMetro.automaticallyColorPanesProperty().set(true);
    }

    /**
     * Applies the current theme to a scene.
     */
    public void applyTheme(Scene scene) {
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
                URL styleSheet = getClass().getResource("/" + currentTheme + ".css");
                StyleManager.getInstance().addUserAgentStylesheet(styleSheet.toString());
        }
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    /**
     * Returns a list of all themes.
     */
    public String[] getThemes() {
        return themes;
    }
}
