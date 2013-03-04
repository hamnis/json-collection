package net.hamnaberg.json.util;

import java.util.*;

public final class FunctionalList<A> implements List<A> {
    private final List<A> delegate;

    private FunctionalList(List<A> delegate) {
        this.delegate = delegate;
    }

    public Optional<A> headOption() {
        return ListOps.headOption(this);
    }

    public A head() {
        return headOption().orNull();
    }

    public FunctionalList<A> tail() {
        if (isEmpty()) {
            return this;
        }
        return create(delegate.subList(1, size()));
    }

    public <B> FunctionalList<B> map(Function<A, B> f) {
        return create(ListOps.map(this, f));
    }

    public <B> FunctionalList<B> flatMap(Function<A, Iterable<B>> f) {
        if (isEmpty()) {
            return empty();
        }
        else {
            return create(ListOps.flatMap(this, f));
        }
    }

    public FunctionalList<A> filter(Predicate<A> pred) {
        return new FunctionalList<A>(ListOps.filter(this, pred));
    }

    /** factories **/

    public static <B> FunctionalList<B> empty() {
        return new FunctionalList<B>(Collections.<B>emptyList());
    }

    public static <A> FunctionalList<A> of(A... args) {
        return new FunctionalList<A>(Arrays.asList(args));
    }

    public static <A> FunctionalList<A> create(List<A> list) {
        if (list instanceof FunctionalList) {
            return (FunctionalList<A>) list;
        }
        return new FunctionalList<A>(list);
    }


    /** List boilerplate **/

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<A> iterator() {
        return Collections.unmodifiableList(delegate).iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    public boolean add(A a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    public boolean addAll(Collection<? extends A> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean addAll(int index, Collection<? extends A> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public A get(int index) {
        return delegate.get(index);
    }

    public A set(int index, A element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void add(int index, A element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public A remove(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<A> listIterator() {
        return Collections.unmodifiableList(delegate).listIterator();
    }

    @Override
    public ListIterator<A> listIterator(int index) {
        return Collections.unmodifiableList(delegate).listIterator(index);
    }

    @Override
    public List<A> subList(int fromIndex, int toIndex) {
        return Collections.unmodifiableList(delegate).subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
