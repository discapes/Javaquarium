package com.firework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static com.firework.Services.getString;

public abstract class Injector {

    private static final Logger logger = new MyLogger();
    private static final Function<Class<?>, ?> dependencySupplier = Services::buildServiceIfAbsent;

    public static <T> void injectDependencies(T instance) {
        Class<?> clazz = instance.getClass();
        //logger.log("Injecting dependencies for    " + toString(instance));
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Dependency.class)) {
                Class<?> dependencyClazz = field.getType();
                logger.log("Found dependency on service   " + dependencyClazz.getSimpleName() + " in " + getString(instance));
                if (dependencyClazz.getAnnotation(Service.class) == null) {
                    throw new IllegalStateException("Dependency must be a @Service");
                } else {
                    Object dependency = dependencySupplier.apply(dependencyClazz);
                    field.setAccessible(true);
                    try {
                        logger.log("Injecting dependency          " + getString(dependency) + " to " + getString(instance));
                        field.set(instance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Could not set field " + field.getName() + " of "
                                + getString(instance));
                    }
                }
            }
        }
        afterInjection(instance);
    }

    private static <T> void afterInjection(T instance) {
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(AfterInjection.class)) {
                try {
                    method.setAccessible(true);
                    logger.log("Calling AfterInjection method " + method.getName() + "@" + instance.getClass().getSimpleName());
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.log("Failed to call method:        " + e);
                }
            }
        }
    }

}
