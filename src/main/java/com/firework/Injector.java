package com.firework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static com.firework.Services.getString;

public abstract class Injector {

    private static final Function<Class<?>, ?> dependencySupplier = Services::buildServiceIfAbsent;

    static <T> void injectDependencies(T instance) {
        Class<?> clazz = instance.getClass();
        //Logger.log("Injecting dependencies for    " + toString(instance));
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Dependency.class)) {
                Class<?> dependencyClazz = field.getType();
                Logger.log("Found dependency on service   " + dependencyClazz.getSimpleName() + " in " + getString(instance));
                if (dependencyClazz.getAnnotation(Service.class) == null) {
                    throw new IllegalStateException("Dependency must be a @Service");
                } else {
                    Object dependency = dependencySupplier.apply(dependencyClazz);
                    field.setAccessible(true);
                    try {
                        Logger.log("Injecting dependency          " + getString(dependency) + " to " + getString(instance));
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
                    Logger.log("Calling AfterInjection method " + method.getName() + "@" + instance.getClass().getSimpleName());
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Logger.log("Failed to call method:        " + e);
                }
            }
        }
    }

}
