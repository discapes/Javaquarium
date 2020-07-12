package com.firework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a method that will get executed when the specified event is fired.
 * It is up to the user to make sure that the method will take the same parameters
 * as when the event is fired.
 * \@OnEvent annotations will be read only from classes marked with @Service. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnEvent {
    String value();
}
