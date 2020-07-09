package com.management;

import javafx.scene.Scene;
import javafx.util.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

public class LawnMower implements PresenterFactory {

    private static Consumer<String> LOG = s -> {};

    private final static HashMap<Enum<?>, Stack<Method>> serviceListeners = new HashMap<>();
    private final static HashMap<Enum<?>, Stack<Method>> presenterListeners = new HashMap<>();
    private final static ArrayList<Pair<Enum<?>,Object[]>> eventBus = new ArrayList<>();

    private static final HashMap<Class<?>, Object> services = new HashMap<>();
    private static final HashMap<Class<?>, Object> presenters = new HashMap<>();

    private static Set<Class<? extends FXMLView>> viewClasses = null;
    private static final HashMap<Class<? extends FXMLView>, Scene> scenes = new HashMap<>();

    public static void setLOG(Consumer<String> LOG) {
        LawnMower.LOG = LOG;
    }

    public static void startServicesAndPresenters() {
        LOG.accept("Starting services...");
        Reflections reflections = new Reflections("", new SubTypesScanner(false), new TypeAnnotationsScanner());

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> clazz : serviceClasses) {
            LOG.accept("Found @Service class          " + clazz.getSimpleName());
            buildService(clazz);
        }

        viewClasses = reflections.getSubTypesOf(FXMLView.class);
        for (Class<?> clazz : viewClasses)
            LOG.accept("Found FXMLView                " + clazz.getSimpleName());
    }

    public static Scene getScene(Class<? extends FXMLView> clazz) {
        LOG.accept("Reqested scene                " + clazz.getSimpleName());
        return scenes.computeIfAbsent(clazz, p -> new Scene(instantiateClass(clazz).getView()));
    }

    @Override public <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
        LOG.accept("Building presenter            " + clazz.getSimpleName());
        T presenter = (T) presenters.get(clazz);
        if (presenter == null) {
            addListeners(clazz, presenterListeners);
            presenter = instantiateClass(clazz);
            injectDependencies(presenter);
            presenters.put(clazz, presenter);
        } else {
            System.out.println("Presenter requested twice?    " + clazz.getSimpleName());
        }
        return presenter;
    }


    private static <T> T buildService(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            LOG.accept("Building service              " + serviceClass.getSimpleName());
            addListeners(serviceClass, serviceListeners);
            service = instantiateClass(serviceClass);
            services.put(serviceClass, service);
            injectDependencies(service);
            return service;
        } else {
            LOG.accept("Service already exists        " + serviceClass.getSimpleName());
            return service;
        }
    }

    static <T> void addListeners(Class<T> clazz, HashMap<Enum<?>, Stack<Method>> listenerMap) {
        LOG.accept("Checking for listeners in     " + clazz.getSimpleName());
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                for (OnEvent annotation : method.getAnnotationsByType(OnEvent.class)) {
                    Enum<?> event = annotation.value();
                    Stack<Method> methods = listenerMap.computeIfAbsent(event, k -> new Stack<>());
                    methods.add(method);
                    LOG.accept("Found listener                " + method.getName() + "   for   " + event);
                }
            }
        }
    }

    static <T> void injectDependencies(T instance) {
        Class<?> clazz = instance.getClass();
        LOG.accept("Injecting dependencies for    " + clazz.getSimpleName() + "@" + System.identityHashCode(instance));
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Dependency.class)) {
                LOG.accept("Found dependency              " + field.getName());
                Class<?> dependencyClass = field.getType();
                if (dependencyClass.getAnnotation(Service.class) == null) {
                    throw new IllegalStateException("Dependency must be a @Service");
                } else {
                    Object dependency = buildService(dependencyClass);
                    field.setAccessible(true);
                    try {
                        field.set(instance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Could not inject field        " + field.getName() + "   of   " + clazz);
                    }
                }
            }
        }
    }

    public static void queueAutomaticEvent(Enum<?> event, Object... params) {
        LOG.accept("Queuing event                 " + event + " ( " + params + " ) ");
        eventBus.add(new Pair<>(event, params));
        if (eventBus.size() == 1) {
            runEventExecutor();
        }
    }

    private static void runEventExecutor() {
        while (eventBus.size() > 0) {
            Pair<Enum<?>, Object[]> latestEvent = eventBus.get(0);
            Enum<?> event = latestEvent.getKey();
            Object[] params = latestEvent.getValue();
            LOG.accept("Firing event                  " + event + " ( " + params.length + " ) ");
            callMethods(event, serviceListeners, services, params);
            callMethods(event, presenterListeners, presenters, params);
            LOG.accept("Finished firing event         " + event + " ( " + params.length + " ) ");
            eventBus.remove(0);
        }
    }

    private static void callMethods(Enum<?> event, HashMap<Enum<?>, Stack<Method>> listeners, HashMap<Class<?>, Object> instances, Object... params) {
        Stack<Method> methods = listeners.get(event);
        if (methods == null) return;
        for (Method method : methods) {
            Class<?> clazz = method.getDeclaringClass();
            Object service = instances.get(clazz);
            method.setAccessible(true);
            LOG.accept("Calling method                " + method.getName() + "@" + clazz.getSimpleName() + "@" + System.identityHashCode(service));
            try {
                method.invoke(service, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Could not call method         " + method.getName() + " @" + clazz.getSimpleName(), e);
            }
        }
    }

    static <T> T instantiateClass(Class<T> clazz) {
        try {
            T o = clazz.getConstructor().newInstance();
            LOG.accept("Instantiated new object       " + clazz.getSimpleName() + "@" + System.identityHashCode(o));
            return o;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate a new   " + clazz.getSimpleName(), e);
        }
    }
}
