package net.hamnaberg.json.util;

import java.util.HashMap;
import java.util.Map;

public final class MapOps {
    public static <K,V> Map<K,V> newHashMap() {
        return new HashMap<K, V>();
    }
}
