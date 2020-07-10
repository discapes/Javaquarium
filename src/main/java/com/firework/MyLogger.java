package com.firework;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MyLogger implements Logger {

    private static boolean doLog = true;
    private static Consumer<String> consumer = System.out::println;
    private final boolean doThisLog;

    public MyLogger() { this(true); }

    public MyLogger(boolean doThisLog) {this.doThisLog = doThisLog;}

    public void log(String msg) {
        if (doLog && doThisLog) consumer.accept(msg);
    }

    public static void setConsumer(Consumer<String> consumer) { MyLogger.consumer = consumer; }
    public static void setDoLog(boolean doLog) { MyLogger.doLog = doLog; }
    public static boolean getDoLog() { return doLog; }
}
