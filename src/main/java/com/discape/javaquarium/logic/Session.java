package com.discape.javaquarium.logic;

import com.discape.javaquarium.display._pages.MainPage;
import com.sun.tools.javac.Main;
import javafx.beans.property.BooleanProperty;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.TimerTask;

public class Session {

    private final ArrayList<TimerTask> tasks = new ArrayList<>();
    private final ArrayList<BooleanProperty> resets = new ArrayList<>();

    public void stop() {
        for (TimerTask task : tasks)
            task.cancel();
        for (BooleanProperty property : resets)
            property.set(true);
    }

    public void addTask(TimerTask task) {
        tasks.add(task);
    }

    public void addResetProperty(BooleanProperty property) {
        resets.add(property);
    }
}
