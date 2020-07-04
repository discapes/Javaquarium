package com.discape.javaquarium.logic;

import java.util.ArrayList;
import java.util.TimerTask;

public class Session {

    private final ArrayList<TimerTask> tasks = new ArrayList<>();

    public void stop() {
        for (TimerTask task : tasks)
            task.cancel();
    }

    public void addTask(TimerTask task) {
        tasks.add(task);
    }
}
