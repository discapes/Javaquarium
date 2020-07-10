package com.firework;

import java.util.function.Consumer;

public abstract class Logger {

    private static Consumer<String> Logger = s -> {};

    public static void log(String message) {
        Logger.accept(message);
    }

    public static void setLogger(Consumer<String> logger) {
        Logger = logger;
    }
}
