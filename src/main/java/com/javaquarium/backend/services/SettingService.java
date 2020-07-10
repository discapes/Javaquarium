package com.javaquarium.backend.services;

import com.firework.OnEvent;
import com.firework.Service;
import com.javaquarium.Events;

@Service
public class SettingService {

    public final int defaultTickRate = 20;
    public final int defaultChartHistory = 10;
    public final int defaultPrettyChartPoints = 200;
    public final String defaultAquarium = System.getProperty("user.home") + "/.javaquariumdefault.txt";
    public final String defaultTheme = "Dark";
    public int tickRate = defaultTickRate;
    public int chartHistory = defaultChartHistory;
    public int prettyChartPoints = defaultPrettyChartPoints;
    public String theme = defaultTheme;

    @OnEvent(Events.LOGOUT)
    public void resetSettings() {
        tickRate = defaultTickRate;
        chartHistory = defaultChartHistory;
        prettyChartPoints = defaultPrettyChartPoints;
        theme = defaultTheme;
    }

}
