package net.hamnaberg.json.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;

public final class PropertyFunctions {

    private PropertyFunctions() {
    }

    private static final Function<Value, Optional<String>> valueStringF =
            input -> input.isString() ? Optional.of(input.asString()) : Optional.<String>empty();

    private static final Function<Value, Optional<Number>> valueNumberF =
            input -> input.isNumeric() ? Optional.of(input.asNumber()) : Optional.<Number>empty();

    private static final Function<Value, Optional<Boolean>> valueBooleanF =
            input -> input.isBoolean() ? Optional.of(input.asBoolean()) : Optional.<Boolean>empty();

    public static final Function<Property, Optional<String>> propertyToValueStringF = input -> input.getValue().flatMap(valueStringF);

    public static final Function<Property, Optional<Number>> propertyToValueNumberF = input -> input.getValue().flatMap(valueNumberF);

    public static final Function<Property, Optional<Boolean>> propertyToValueBooleanF = input -> input.getValue().flatMap(valueBooleanF);

    public static final Function<Property, List<String>> propertyToArrayStringF =
            input -> input.getArray()
                          .stream()
                          .map(valueStringF)
                          .flatMap(opt -> opt.map(s -> Stream.of(s)).orElse(Stream.empty()))
                          .collect(Collectors.toList());

    public static final Function<Property, List<Number>> propertyToArrayNumberF =
            input -> input.getArray()
                          .stream()
                          .map(valueNumberF)
                          .flatMap(opt -> opt.map(n -> Stream.of(n)).orElse(Stream.empty()))
                          .collect(Collectors.toList());

    public static final Function<Property, List<Boolean>> propertyToArrayBooleanF =
            input -> input.getArray()
                          .stream()
                          .map(valueBooleanF)
                          .flatMap(opt -> opt.map(b -> Stream.of(b)).orElse(Stream.empty()))
                          .collect(Collectors.toList());

}
