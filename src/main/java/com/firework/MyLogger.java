package com.firework;

public class MyLogger implements Logger {

    public static boolean globalLog = true;
    private final boolean log;

    public MyLogger() { this(true); }

    public MyLogger(boolean log) {this.log = log;}

    public void log(String msg) {
        if (globalLog && log) System.out.println(msg);
    }
}
