package com.discape.javaquarium.logic;

import javafx.beans.property.BooleanProperty;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.TimerTask;

public class Session {

    @Inject private AquariumFile aquariumFile;

    private final ArrayList<TimerTask> tasks = new ArrayList<>();
    private final ArrayList<BooleanProperty> resets = new ArrayList<>();

    public void stop() {
        for (TimerTask task : tasks)
            task.cancel();
        for (BooleanProperty property : resets)
            property.set(true);
        aquariumFile.nullAquarium();
    }

    public void addTask(TimerTask task) {
        tasks.add(task);
    }

    public void addResetProperty(BooleanProperty property) {
        resets.add(property);
    }
}
