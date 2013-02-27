package net.hamnaberg.json.util;

public final class StringUtils {
    private StringUtils() {
    }


    public static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }
}
