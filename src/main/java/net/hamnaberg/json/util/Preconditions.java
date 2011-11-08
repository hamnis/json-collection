package net.hamnaberg.json.util;


/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/8/11
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Preconditions {
    

    public static <A> A checkNotNull(A value, String message, Object... arguments) {
        if (value == null) {
            throw new IllegalArgumentException(message == null ? "The validated object was null" : String.format(message, arguments));
        }
        return value;
    }

    public static void checkArgument(boolean predicate, String message, Object... arguments) {
        if (!predicate) {
            throw new IllegalArgumentException(String.format(message, arguments));
        }
    }
}
