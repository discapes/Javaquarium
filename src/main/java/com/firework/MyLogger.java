package com.firework;

import java.util.function.Consumer;

/** Logging system for Firework. */
@SuppressWarnings("unused")
public class MyLogger implements Logger {

    private static boolean doLog = true;
    private static Consumer<String> consumer = System.out::println;
    private final boolean doThisLog;

    public MyLogger() { this(true); }

    public MyLogger(boolean doThisLog) {this.doThisLog = doThisLog;}

    static String getString(Object instance) {
        Class<?> clazz = instance.getClass();
        return clazz.getSimpleName() + "#" + System.identityHashCode(instance);
    }

    public void log(String msg) {
        if (doLog && doThisLog) consumer.accept(msg);
    }

    public static void setConsumer(Consumer<String> consumer) { MyLogger.consumer = consumer; }
    public static void setDoLog(boolean doLog) { MyLogger.doLog = doLog; }
    public static boolean getDoLog() { return doLog; }
}
