package com.javaquarium.backend;

import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

/** Converts strings into integers using custom rules. */
public class CustomIntegerStringConverter extends IntegerStringConverter {
    private final IntegerStringConverter converter = new IntegerStringConverter();

    /**
     * Converts a string to an integer, and handles an invalid string as follows:
     * - If the string contains letters, return 0.
     * - Else if the string starts with a -, return Integer.MIN_VALUE.
     * - Else return Integer.MAX_VALUE.
     * @param string to be converted.
     * @return An integer.
     */
    @Override
    public Integer fromString(String string) {
        try {
            return converter.fromString(string);
        } catch (NumberFormatException ignored) {
        }

        /* if letters */
        String rest = string.substring(1);
        if (!Pattern.compile("-?\\d+(\\.\\d+)?").matcher(rest).matches())
            return 0;

        if (string.charAt(0) == '-')
            return Integer.MIN_VALUE;

        return Integer.MAX_VALUE;

    }
}