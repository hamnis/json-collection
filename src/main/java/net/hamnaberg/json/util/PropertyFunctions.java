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

    private static <A, B> Function<A, Optional<B>> optF() { return (ignore) -> Optional.empty();}

    private static final Function<Value, Optional<String>> valueStringF =
            input -> input.fold(optF(), s -> Optional.of(s.value), optF(), Optional::empty);

    private static final Function<Value, Optional<Number>> valueNumberF =
            input -> input.fold(optF(), optF(), n -> Optional.of(n.value), Optional::empty);

    private static final Function<Value, Optional<Boolean>> valueBooleanF =
            input -> input.fold(n -> Optional.of(n == Value.BooleanValue.TRUE), optF(), optF(), Optional::empty);

    public static final Function<Property, Optional<String>> propertyToValueStringF = input -> input.getValue().flatMap(valueStringF);

    public static final Function<Property, Optional<Number>> propertyToValueNumberF = input -> input.getValue().flatMap(valueNumberF);

    public static final Function<Property, Optional<Boolean>> propertyToValueBooleanF = input -> input.getValue().flatMap(valueBooleanF);

    public static final Function<Property, List<String>> propertyToArrayStringF =
            input -> input.getArray()
                          .stream()
                          .map(valueStringF)
                          .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
                          .collect(Collectors.toList());

    public static final Function<Property, List<Number>> propertyToArrayNumberF =
            input -> input.getArray()
                          .stream()
                          .map(valueNumberF)
                          .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
                          .collect(Collectors.toList());

    public static final Function<Property, List<Boolean>> propertyToArrayBooleanF =
            input -> input.getArray()
                          .stream()
                          .map(valueBooleanF)
                          .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
                          .collect(Collectors.toList());

}
