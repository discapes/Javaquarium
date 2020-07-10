package com.javaquarium.backend.services;

import com.firework.OnEvent;
import com.firework.Service;
import com.javaquarium.Events;

@Service
public class SettingService {

    public static final int defaultTickRate = 20;
    public static final int defaultChartHistory = 10;
    public static final int defaultPrettyChartPoints = 200;
    public static final String defaultAquarium = System.getProperty("user.home") + "/.javaquariumdefault.txt";
    public static final String defaultTheme = "Dark";
    public static int tickRate = defaultTickRate;
    public static int chartHistory = defaultChartHistory;
    public static int prettyChartPoints = defaultPrettyChartPoints;
    public static String theme = defaultTheme;

    @OnEvent(Events.LOGOUT)
    public void resetSettings() {
        tickRate = defaultTickRate;
        chartHistory = defaultChartHistory;
        prettyChartPoints = defaultPrettyChartPoints;
        theme = defaultTheme;
    }

}
