package net.hamnaberg.json;

public interface Value {
    boolean isBoolean();
    boolean isString();
    boolean isNumeric();
    boolean isNull();

    String asString();

    boolean asBoolean();

    Number getNumber();
}
