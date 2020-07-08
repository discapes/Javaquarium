package com.discape.javaquarium.backend.events;

import com.discape.javaquarium.backend.aquarium.Aquarium;

public enum Events {
    NEWAQUARIUM(new Event<Aquarium>()),
    NEWTHEME(new Event<String>()),
    CHARTSETTINGCHANGE(new Event<>()),
    TICKRATECHANGE(new Event<>()),
    LOGIN(new Event<String>()),
    LOGOUT(new Event<String>());

    private final Event<?> event;
    Events(Event<?> event) {
        this.event = event;
    }
    public <T> Event<T> e() {
        return (Event<T>) event;
    }
}
