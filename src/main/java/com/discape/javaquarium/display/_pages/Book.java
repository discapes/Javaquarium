package com.discape.javaquarium.display._pages;

import java.util.WeakHashMap;

public class Book {
    private final WeakHashMap<Class<? extends Page>, Page> pages = new WeakHashMap<>();

    public void addPage(Page page) {
        pages.put(page.getClass(), page);
    }

    public <T> T getPage(Class<T> clazz) {
        //noinspection unchecked
        return (T) pages.get(clazz);
    }
}
