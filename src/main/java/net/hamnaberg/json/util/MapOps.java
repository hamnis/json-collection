package net.hamnaberg.json.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MapOps {
    public static <K,V> Map<K,V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K,V> Map<K,V> newHashMap(Map<K, V> fromMap) {
        return new HashMap<K, V>(fromMap);
    }

    public static <K,V> Map<K,V> newHashMap(K k1, V v1) {
        HashMap<K, V> hm = new HashMap<K, V>();
        hm.put(k1, v1);
        return hm;
    }

    public static <K,V> Map<K,V> newHashMap(K k1, V v1, K k2, V v2) {
        HashMap<K, V> hm = new HashMap<K, V>();
        hm.put(k1, v1);
        hm.put(k2, v2);
        return hm;
    }

    public static <K,V> Map<K,V> newHashMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap<K, V> hm = new HashMap<K, V>();
        hm.put(k1, v1);
        hm.put(k2, v2);
        hm.put(k3, v3);
        return hm;
    }

    public static <K,V> Map<K,V> newHashMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap<K, V> hm = new HashMap<K, V>();
        hm.put(k1, v1);
        hm.put(k2, v2);
        hm.put(k3, v3);
        hm.put(k4, v4);
        return hm;
    }

    public static <K,V> Map<K,V> newHashMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        HashMap<K, V> hm = new HashMap<K, V>();
        hm.put(k1, v1);
        hm.put(k2, v2);
        hm.put(k3, v3);
        hm.put(k4, v4);
        hm.put(k5, v5);
        return hm;
    }

    public static <K, V, V2> Map<K, V2> mapValues(Map<K, V> input, Function<V, V2> f) {
        Map<K, V2> map = MapOps.newHashMap();
        Set<Map.Entry<K,V>> entries = input.entrySet();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), f.apply(entry.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }

    public static <K,V> Map<K, V> filterKeys(Map<K, V> input, final Predicate<K> predicate) {
        return filter(input, new Predicate<Map.Entry<K, V>>() {
            @Override
            public boolean apply(Map.Entry<K, V> input) {
                return predicate.apply(input.getKey());
            }
        });
    }

    public static <K,V> Map<K, V> filter(Map<K, V> input, Predicate<Map.Entry<K, V>> predicate) {
        Map<K, V> map = MapOps.newHashMap();
        for (Map.Entry<K, V> entry : input.entrySet()) {
            if (predicate.apply(entry)) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
