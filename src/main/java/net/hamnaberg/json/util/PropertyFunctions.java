package net.hamnaberg.json.util;

import net.hamnaberg.funclite.Function;
import net.hamnaberg.funclite.FunctionalList;
import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;

import java.util.List;

public final class PropertyFunctions {
    private PropertyFunctions() {
    }

    private static final Function<Value, Optional<String>> valueStringF = new Function<Value, Optional<String>>() {
        @Override
        public Optional<String> apply(Value input) {
            return input.isString() ? Optional.some(input.asString()) : Optional.<String>none();
        }
    };

    private static final Function<Value, Optional<Number>> valueNumberF = new Function<Value, Optional<Number>>() {
        @Override
        public Optional<Number> apply(Value input) {
            return input.isNumeric() ? Optional.some(input.asNumber()) : Optional.<Number>none();
        }
    };

    private static final Function<Value, Optional<Boolean>> valueBooleanF = new Function<Value, Optional<Boolean>>() {
        @Override
        public Optional<Boolean> apply(Value input) {
            return input.isBoolean() ? Optional.some(input.asBoolean()) : Optional.<Boolean>none();
        }
    };


    public static final Function<Property, Optional<String>> propertyToValueStringF = new Function<Property, Optional<String>>() {
        @Override
        public Optional<String> apply(Property input) {
            return input.getValue().flatMap(valueStringF);
        }
    };

    public static final Function<Property, Optional<Number>> propertyToValueNumberF = new Function<Property, Optional<Number>>() {
        @Override
        public Optional<Number> apply(Property input) {
            return input.getValue().flatMap(valueNumberF);
        }
    };

    public static final Function<Property, Optional<Boolean>> propertyToValueBooleanF = new Function<Property, Optional<Boolean>>() {
        @Override
        public Optional<Boolean> apply(Property input) {
            return input.getValue().flatMap(valueBooleanF);
        }
    };

    public static final Function<Property, List<String>> propertyToArrayStringF = new Function<Property, List<String>>() {
        @Override
        public List<String> apply(Property input) {
            return FunctionalList.create(input.getArray()).flatMap(liftOptional(valueStringF));
        }
    };

    public static final Function<Property, List<Number>> propertyToArrayNumberF = new Function<Property, List<Number>>() {
        @Override
        public List<Number> apply(Property input) {
            return FunctionalList.create(input.getArray()).flatMap(liftOptional(valueNumberF));
        }
    };

    public static final Function<Property, List<Boolean>> propertyToArrayBooleanF = new Function<Property, List<Boolean>>() {
        @Override
        public List<Boolean> apply(Property input) {
            return FunctionalList.create(input.getArray()).flatMap(liftOptional(valueBooleanF));
        }
    };

    public static <A, B> Function<A, Iterable<B>> liftOptional(final Function<A, Optional<B>> f) {
        return new Function<A, Iterable<B>>() {
            @Override
            public Iterable<B> apply(A a) {
                return f.apply(a);
            }
        };
    }
}
