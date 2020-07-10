package com.firework;

import javafx.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static com.firework.Services.getString;

public abstract class EventSystem {

    private final static HashMap<Enum<?>, Stack<Method>> listeners = new HashMap<>();
    private final static ArrayList<Pair<Enum<?>, Object[]>> eventBus = new ArrayList<>();

    static <T> void registerService(Class<T> clazz) {
        //     Logger.log("Checking for listeners in     " + clazz.getSimpleName());
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                for (OnEvent annotation : method.getAnnotationsByType(OnEvent.class)) {
                    Enum<?> event = annotation.value();
                    Stack<Method> methods = listeners.computeIfAbsent(event, k -> new Stack<>());
                    methods.add(method);
                    Logger.log("Found listener                " + method.getName() + "   for   " + event);
                }
            }
        }
    }

    public static void queueAutomaticEvent(Enum<?> event, Object... params) {
        Logger.log("Queuing event                 " + event + " (" + params.length + ")");
        eventBus.add(new Pair<>(event, params));
        if (eventBus.size() == 1) {
            runEventExecutor();
        }
    }

    private static void runEventExecutor() {
        Logger.log("Running event executor...");
        while (eventBus.size() > 0) {
            Pair<Enum<?>, Object[]> latestEvent = eventBus.get(0);
            Enum<?> event = latestEvent.getKey();
            Object[] params = latestEvent.getValue();
            Logger.log("Firing event                  " + event + " (" + params.length + ")");
            callMethods(event, params);
            Logger.log("Done firing event             " + event + " (" + params.length + ")");
            eventBus.remove(0);
        }
    }

    private static void callMethods(Enum<?> event, Object... params) {
        Stack<Method> methods = listeners.get(event);
        if (methods == null) return;
        for (Method method : methods) {
            Class<?> clazz = method.getDeclaringClass();
            Object service = Services.getService(clazz);
            method.setAccessible(true);
            Logger.log("Calling method                " + method.getName() + " @ " + getString(service));
            try {
                method.invoke(service, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Error calling method" + method.getName()
                        + " @ " + getString(service), e);
            }
        }
    }

}
