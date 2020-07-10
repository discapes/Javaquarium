package com.firework;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Set;

public abstract class Firework {

    private static final Logger logger = new MyLogger();

    public static void startServices(String pkg) {
        logger.log("Starting services...");
        Reflections reflections = new Reflections(pkg, new SubTypesScanner(false), new TypeAnnotationsScanner());

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> clazz : serviceClasses) {
            logger.log("Found @Service class          " + clazz.getSimpleName());
            Services.buildServiceIfAbsent(clazz);
        }
    }

}
