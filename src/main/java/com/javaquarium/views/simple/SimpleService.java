package com.javaquarium.views.simple;

import com.javaquarium.backend.aquarium.Aquarium;
import com.management.Dependency;
import com.management.OnEvent;
import com.management.Service;
import com.javaquarium.Event;

@Service
public class SimpleService {

    public String getText() {
        return "Hello";
    }

    @OnEvent(Event.LOGOUT)
    private void logout() {
        System.err.println("LOGGING OUT - SimpleService@" + hashCode());
    }

    @OnEvent(Event.NEWAQUARIUM)
    private void onNewAquarium(Aquarium aquarium) {
        System.err.println(aquarium.toString());
    }
}
