package com.discape.javaquarium.logic;

import java.util.TimerTask;

public final class TimerTaskCatcher extends TimerTask {
    private final TimerTask orig;
    private final Thread.UncaughtExceptionHandler handler;

    public TimerTaskCatcher(TimerTask orig, Thread.UncaughtExceptionHandler handler) {
        if (orig == null || handler == null) {
            throw new NullPointerException();
        }
        this.orig = orig;
        this.handler = handler;
    }

    @Override
    public boolean cancel() {
        return orig.cancel();
    }

    @Override
    public void run() {
        try {
            orig.run();
        } catch (Throwable exc) {
            handler.uncaughtException(Thread.currentThread(), exc);
        }
    }

    @Override
    public long scheduledExecutionTime() {
        return orig.scheduledExecutionTime();
    }
}
