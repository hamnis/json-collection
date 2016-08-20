package net.hamnaberg.json.util;

import java.math.BigDecimal;
import java.util.List;
import javaslang.control.Option;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;

public final class PropertyFunctions {

    private PropertyFunctions() {
    }

    private static <A, B> Function<A, Option<B>> optF() { return (ignore) -> Option.none();}

    private static final Function<Value, Option<String>> valueStringF =
            input -> input.fold(optF(), s -> Option.of(s.value), optF(), Option::none);

    private static final Function<Value, Option<BigDecimal>> valueNumberF =
            input -> input.fold(optF(), optF(), n -> Option.of(n.value), Option::none);

    private static final Function<Value, Option<Boolean>> valueBooleanF =
            input -> input.fold(n -> Option.of(n == Value.BooleanValue.TRUE), optF(), optF(), Option::none);

    public static final Function<Property, Option<String>> propertyToValueStringF = input -> input.getValue().flatMap(valueStringF);

    public static final Function<Property, Option<BigDecimal>> propertyToValueNumberF = input -> input.getValue().flatMap(valueNumberF);

    public static final Function<Property, Option<Boolean>> propertyToValueBooleanF = input -> input.getValue().flatMap(valueBooleanF);

    public static final Function<Property, List<String>> propertyToArrayStringF =
            input -> input.getArray()
                          .stream()
                          .map(valueStringF)
                          .flatMap(javaslang.Value::toJavaStream)
                          .collect(Collectors.toList());

    public static final Function<Property, List<Number>> propertyToArrayNumberF =
            input -> input.getArray()
                          .stream()
                          .map(valueNumberF)
                          .flatMap(javaslang.Value::toJavaStream)
                          .collect(Collectors.toList());

    public static final Function<Property, List<Boolean>> propertyToArrayBooleanF =
            input -> input.getArray()
                          .stream()
                          .map(valueBooleanF)
                          .flatMap(javaslang.Value::toJavaStream)
                          .collect(Collectors.toList());

}
