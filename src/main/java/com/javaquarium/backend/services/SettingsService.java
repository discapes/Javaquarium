package com.javaquarium.backend.services;

import com.firework.OnEvent;
import com.firework.Service;
import com.javaquarium.Events;

@Service
public class SettingsService {
    public static final int defaultTickRate = 20;
    public static final int defaultChartHistory = 10;
    public static final int defaultPrettyChartPoints = 200;
    public static final String defaultAquarium = System.getProperty("user.home") + "/.javaquariumdefault.txt";
    public static final String defaultTheme = "Dark";
    public static int tickRate = 20;
    public static int chartHistory = 10;
    public static int prettyChartPoints = 200;

    @OnEvent(Events.LOGOUT)
    private void resetSettings() {
        tickRate = defaultTickRate;
        chartHistory = defaultChartHistory;
        prettyChartPoints = defaultPrettyChartPoints;
    }

}
