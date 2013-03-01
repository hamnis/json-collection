package net.hamnaberg.json.util;

public final class Predicates {
    private Predicates() {
    }

    public static <A> Predicate<A> not(final Predicate<A> p) {
        return new Predicate<A>() {
            @Override
            public boolean apply(A input) {
                return !p.apply(input);
            }
        };
    }


    public static <A> Predicate<A> and(final Predicate<A> p, final Predicate<A> p2) {
        return new Predicate<A>() {
            @Override
            public boolean apply(A input) {
                return p.apply(input) && p2.apply(input);
            }
        };
    }

    public static <A> Predicate<A> or(final Predicate<A> p, final Predicate<A> p2) {
        return new Predicate<A>() {
            @Override
            public boolean apply(A input) {
                return p.apply(input) || p2.apply(input);
            }
        };
    }


    @SuppressWarnings("unchecked")
    public static <A> Predicate<A> alwaysTrue() {
        return (Predicate<A>) TRUE;
    }


    public static Predicate<Object> TRUE = new Predicate<Object>() {
        @Override
        public boolean apply(Object input) {
            return true;
        }
    };

}
