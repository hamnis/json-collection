package net.hamnaberg.json.util;

public class Preconditions {
    public static <A> A checkNotNull(A input) {
        return checkNotNull(input, "input was null");
    }

    public static <A> A checkNotNull(A input, String message, Object... args) {
        if (input == null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return input;
    }

    public static void checkArgument(boolean pred, String message, Object... args) {
        if (!pred) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
