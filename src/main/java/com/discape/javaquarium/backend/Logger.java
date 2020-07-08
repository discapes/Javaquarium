package com.discape.javaquarium.backend;

public abstract class Logger {

    public static <T> void log(Object o, T msg) {
        System.out.println("[" + toString(o) + "] - " + msg);
    }

    public static <T> void log(String clazz, T msg) {
        System.out.println("new " + msg);
    }

    public static String toString(Object o) {
        return o.getClass().getSimpleName() + "@" +
                Integer.toHexString(System.identityHashCode(o));
    }
}
