package net.hamnaberg.json.util;

import java.util.Iterator;

public abstract class Optional<A> implements Iterable<A> {
    public static None<Object> NONE = new None<Object>();

    Optional() {
    }

    public abstract A get();

    public abstract boolean isSome();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public final boolean isNone() {
        return !isSome();
    }

    public final <B> Optional<B> map(Function<A, B> f) {
        if (isNone()) {
            return none();
        }
        else {
            return fromNullable(f.apply(get()));
        }
    }

    public final <B> Optional<B> flatMap(Function<A, Optional<B>> f) {
        if (isNone()) {
            return none();
        }
        else {
            return f.apply(get());
        }
    }

    public final void foreach(Function<A, Void> f) {
        if(isSome()) {
            f.apply(get());
        }
    }

    public final Optional<A> filter(Predicate<A> input) {
        if (isSome() && input.apply(get())) {
            return this;
        }
        else {
            return none();
        }
    }

    public boolean forall(Predicate<A> input) {
        return ListOps.forall(this, input);
    }

    public boolean exists(Predicate<A> input) {
        return ListOps.exists(this, input);
    }

    public static <A> Optional<A> fromNullable(A value) {
        return value != null ? some(value) : Optional.<A>none();
    }

    public static <A> Optional<A> some(A value) {
        return new Some<A>(Preconditions.checkNotNull(value));
    }

    @SuppressWarnings("unchecked")
    public static <A> Optional<A> none() {
        return (Optional<A>) NONE;
    }

    public A orNull() {
        return isSome() ? get() : null;
    }

    public A getOrElse(A orElse) {
        return isSome() ? get() : orElse;
    }

    public A getOrElse(Supplier<A> orElse) {
        return isSome() ? get() : orElse.get();
    }

    public Optional<A> or(Optional<A> orElse) {
        return isSome() ? this : orElse;
    }

    @Override
    public final Iterator<A> iterator() {
        return new Iterator<A>() {
            private volatile boolean used = false;
            @Override
            public boolean hasNext() {
                return !used && isSome();
            }

            @Override
            public A next() {
                A value = get();
                used = true;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }
}

final class Some<A> extends Optional<A> {

    private final A value;

    Some(A value) {
        this.value = value;
    }

    @Override
    public A get() {
        return value;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Some some = (Some) o;

        if (value != null ? !value.equals(some.value) : some.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Some{%s}", value);
    }
}

final class None<A> extends Optional<A> {
    @Override
    public A get() {
        throw new UnsupportedOperationException("Cannot get from None");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof None;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public String toString() {
        return "None";
    }
}
