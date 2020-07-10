package com.firework;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public abstract class Services {

    private static final Logger logger = new MyLogger();

    private static final HashMap<Class<?>, Object> services = new HashMap<>();
    private static <T> void addService(T service) {
        services.put(service.getClass(), service);
    }
    public static <T> T getService(Class<T> clazz) {
        return (T) services.get(clazz);
    }

    static <T> T buildServiceIfAbsent(Class<T> clazz) {
        T service = getService(clazz);
        if (service == null) {
            logger.log("Building service              " + clazz.getSimpleName());
            EventSystem.registerService(clazz);

            service = instantiateClass(clazz);
            addService(service);
            Injector.injectDependencies(service);
        } else {
            logger.log("Using existing service        " + clazz.getSimpleName());
        }
        return service;
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            T o = clazz.getConstructor().newInstance();
            logger.log("Instantiated new object       " + getString(o));
            return o;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate a new " + clazz.getSimpleName(), e);
        }
    }


    static String getString(Object instance) {
        Class<?> clazz = instance.getClass();
        return clazz.getSimpleName() + "#" + System.identityHashCode(instance);
    }
}
