package net.hamnaberg.json.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MapOps {
    public static <K,V> Map<K,V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V, V2> Map<K, V2> mapValues(Map<K, V> input, Function<V, V2> f) {
        Map<K, V2> map = MapOps.newHashMap();
        Set<Map.Entry<K,V>> entries = input.entrySet();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), f.apply(entry.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }
}
