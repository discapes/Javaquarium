package com.discape.javaquarium.backend;

import com.airhacks.afterburner.injection.Injector;

import javax.inject.Inject;
import java.util.TimerTask;

/**
 * Is responsible for starting and stopping ChartDataUpdater and the aquarium clock,
 * and loading the aquarium on start into the Injector and all persistent classes that need it (ChartDataUpdater)
 */
public class SessionManager {

    @Inject private ChartDataUpdater chartDataUpdater;
    @Inject private AquariumFile aquariumFile;
    private TimerTask aquariumTask = null;

    /** Stops ChartDataUpdater and the aquarium clock, and sets aquarium to null. */
    public void quit() {
        aquariumTask.cancel();
        chartDataUpdater.stop();
    }

    /** Loads the aquarium and starts ChartDataUpdater. */
    public void start() {
        Aquarium aquarium = aquariumFile.loadDefault();
        Injector.injectMembers(aquarium.getClass(), aquarium);
        Injector.setModelOrService(Aquarium.class, aquarium);

        aquariumTask = aquarium.startClock();
        chartDataUpdater.start(aquarium);
    }

    private void start(Aquarium newAquarium) {
        Injector.injectMembers(newAquarium.getClass(), newAquarium);
        Injector.setModelOrService(Aquarium.class,  newAquarium);

        aquariumTask = newAquarium.startClock();
        chartDataUpdater.start(newAquarium);
    }

    /** Restarts the session with a new aquarium. */
    public void restart(Aquarium newAquarium) {
        quit();
        start(newAquarium);
    }
}
