package net.hamnaberg.json;

import com.google.common.base.Preconditions;

public class ValueFactory {
    public static Value createValue(Object value) {
        Preconditions.checkArgument(checkValue(value), "Illegal value %s", value);
        return new ValueImpl(value);
    }

    private static boolean checkValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return true;
        }
        else if (value instanceof Boolean) {
            return true;
        }
        else if (value instanceof Number) {
            return true;
        }
        return false;
    }
}
