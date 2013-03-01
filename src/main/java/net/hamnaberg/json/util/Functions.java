package net.hamnaberg.json.util;

public class Functions {
    public static <A> Function<A, A> identity() {
        return new Function<A, A>() {
            @Override
            public A apply(A input) {
                return input;
            }
        };
    }

    public static <A, B, C> Function<A, C> compose(final Function<A, B> f, final Function<B, C> f2) {
        return new Function<A, C>() {
            @Override
            public C apply(A input) {
                return f2.apply(f.apply(input));
            }
        };
    }
}
