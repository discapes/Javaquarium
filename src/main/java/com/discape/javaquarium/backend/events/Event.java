package com.discape.javaquarium.backend.events;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Event<T> {

    Event(){};

    private ArrayList<Consumer<T>> consumers = new ArrayList<>();

    public void addListener(Consumer<T> consumer) {
        consumers.add(consumer);
    }

    public void fire(T param) {
        consumers.forEach(consumer -> consumer.accept(param));
    }
}
