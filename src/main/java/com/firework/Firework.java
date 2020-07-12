package com.firework;

import javafx.util.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import static com.firework.MyLogger.getString;

/** Firework is a framework that allows for event driven architecture and has it's own dependency injection. */
public abstract class Firework {

    private final static HashMap<String, Stack<Method>> listeners = new HashMap<>();
    private final static ArrayList<Pair<String, Object[]>> eventBus = new ArrayList<>();
    private static final Logger logger = new MyLogger();

    /** Starts all of the @Service classes, and adds their @OnEvent functions to the event system
     * @param pkg package where all of the @Service classes are
     * */
    public static void startServices(String pkg) {
        logger.log("Starting services...");
        Reflections reflections = new Reflections(pkg, new SubTypesScanner(false), new TypeAnnotationsScanner());

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> clazz : serviceClasses) {
            logger.log("Found @Service class          " + clazz.getSimpleName());
            Services.buildServiceIfAbsent(clazz);
        }
    }

    static <T> void registerService(Class<T> clazz) {
        //     logger.log("Checking for listeners in     " + clazz.getSimpleName());
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                for (OnEvent annotation : method.getAnnotationsByType(OnEvent.class)) {
                    String event = annotation.value();
                    Stack<Method> methods = listeners.computeIfAbsent(event, k -> new Stack<>());
                    methods.add(method);
                    logger.log("Found listener                " + method.getName() + "   for   " + event);
                }
            }
        }
    }

    /** Adds an event to the event queue, with optional parameters to be passed to the @OnEvent listener methods.
     * @param event name of the event to be fired
     * @param params parameters to call the @OnEvent methods with
     * */
    public static void queueAutomaticEvent(String event, Object... params) {
        logger.log("Queuing event                 " + event + " (" + params.length + ")");
        eventBus.add(new Pair<>(event, params));
        if (eventBus.size() == 1) {
            runEventExecutor();
        }
    }

    private static void runEventExecutor() {
        logger.log("Running event executor...");
        while (eventBus.size() > 0) {
            Pair<String, Object[]> latestEvent = eventBus.get(0);
            String event = latestEvent.getKey();
            Object[] params = latestEvent.getValue();
            logger.log("Firing event                  " + event + " (" + params.length + ")");
            callMethods(event, params);
            logger.log("Done firing event             " + event + " (" + params.length + ")");
            eventBus.remove(0);
        }
    }

    private static void callMethods(String event, Object... params) {
        Stack<Method> methods = listeners.get(event);
        if (methods == null) return;
        for (Method method : methods) {
            Class<?> clazz = method.getDeclaringClass();
            Object service = Services.getService(clazz);
            method.setAccessible(true);
            logger.log("Calling method                " + method.getName() + " @ " + getString(service));
            try {
                method.invoke(service, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Error calling method" + method.getName()
                        + " @ " + getString(service), e);
            }
        }
    }

}
