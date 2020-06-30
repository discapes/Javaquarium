package com.discape.javaquarium.gui;

import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

public class CustomIntegerStringConverter extends IntegerStringConverter {
    private final IntegerStringConverter converter = new IntegerStringConverter();

    @Override
    public Integer fromString(String string) {
        try {
            return converter.fromString(string);
        } catch (NumberFormatException e) {
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