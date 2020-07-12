package com.javaquarium.backend.services;

import com.firework.Dependency;
import com.firework.Service;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.net.URL;

/** Applies themes to the application.
 * Note: Metro themes cannot be used without rebuilding all scenes.
 */
@Service
public class ThemeService {

    @Dependency private SettingService settingService;

    private final JMetro jMetro;
    private final String[] themes = {
            "Light",
            "Dark",
            "Metro Dark",
            "Metro Light",
            "Old"
    };

    public ThemeService() {
        jMetro = new JMetro();
        jMetro.automaticallyColorPanesProperty().set(true);
    }

    /**
     * Applies the current theme to a scene.
     * @param scene scene the current theme will be applied to
     */
    public void applyTheme(Scene scene) {
        StyleManager.getInstance().platformUserAgentStylesheetContainers.clear();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        jMetro.sceneProperty().set(null);

        switch (settingService.theme) {
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
                URL styleSheet = getClass().getResource("/" + settingService.theme + ".css");
                StyleManager.getInstance().addUserAgentStylesheet(styleSheet.toString());
        }
    }

    /**
     * Returns a list of all themes.
     * @return a list of all the themes
     */
    public String[] getThemes() {
        return themes;
    }
}
