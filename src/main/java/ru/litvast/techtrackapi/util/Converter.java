package ru.litvast.techtrackapi.util;

public class Converter {

    public static int convertIdStringToInt(String stringId) {
        int id;
        try {
            id = Integer.parseInt(stringId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID should be a number");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return id;
    }

    public static long convertIdStringToLong(String stringId) {
        long id;
        try {
            id = Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID should be a number");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return id;
    }
}
