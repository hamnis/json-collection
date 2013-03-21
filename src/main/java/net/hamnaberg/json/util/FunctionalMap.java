package net.hamnaberg.json.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class FunctionalMap<K, V> implements Map<K, V> {
    private final Map<K, V> delegate;
    private final V defaultValue;

    private FunctionalMap(Map<K, V> delegate) {
        this(delegate, null);
    }

    private FunctionalMap(Map<K, V> delegate, V defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    public static <K,V> FunctionalMap<K,V> create(Map<K, V> map) {
        return create(map, null);
    }

    public static <K,V> FunctionalMap<K,V> create(Map<K, V> map, V defaultValue) {
        if (map instanceof FunctionalMap) {
            FunctionalMap<K, V> fMap = (FunctionalMap<K, V>) map;
            if (isDefaultOverride(defaultValue, fMap)) {
                return new FunctionalMap<K, V>(fMap.delegate, defaultValue);
            }

            return fMap;
        }
        return new FunctionalMap<K, V>(map, defaultValue);
    }

    public FunctionalMap<K, V> withDefaultValue(V value) {
        return create(delegate, value);
    }

    public V getOrElse(K key, V defaultValue) {
        V v = delegate.get(key);
        return v != null ? v : defaultValue;
    }

    public Optional<V> getOptional(K key) {
        return Optional.fromNullable(get(key));
    }

    public static <K,V> FunctionalMap<K, V> empty() {
        return create(Collections.<K, V>emptyMap());
    }


    public <V2> FunctionalMap<K, V2> mapValues(Function<V, V2> f) {
        return create(MapOps.mapValues(delegate, f));
    }

    private static <K, V> boolean isDefaultOverride(V defaultValue, FunctionalMap<K, V> fMap) {
        return (fMap.defaultValue == null && defaultValue != null) || (fMap.defaultValue != null && defaultValue == null);
    }


    /** Map boilerplate **/

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        return getOrElse((K) key, defaultValue);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return Collections.unmodifiableSet(delegate.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
