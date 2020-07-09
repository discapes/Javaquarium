package com.javaquarium.backend;

public abstract class Settings {
    public static int tickRate = 20;
    public static int chartHistory = 10;
    public static int chartPoints = 200;
    public static String defaultAquarium = System.getProperty("user.home") + "/.javaquariumdefault.txt";
}
