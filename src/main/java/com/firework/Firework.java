package com.firework;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Set;

public abstract class Firework {

    public static void startServices() {
        Logger.log("Starting services...");
        Reflections reflections = new Reflections("", new SubTypesScanner(false), new TypeAnnotationsScanner());

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> clazz : serviceClasses) {
            Logger.log("Found @Service class          " + clazz.getSimpleName());
            Services.buildServiceIfAbsent(clazz);
        }
    }

}
