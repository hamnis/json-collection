package net.hamnaberg.json.util;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class FunctionalMapTest {
    @Test
    public void construct() {
        FunctionalMap<Object, Object> objects = FunctionalMap.create(Collections.emptyMap());
        assertNotNull(objects);
    }

    @Test
    public void wrappingOfSelfReturnsSame() {
        FunctionalMap<Object, Object> empty = FunctionalMap.empty();
        FunctionalMap<Object, Object> map = FunctionalMap.create(empty);
        assertSame(empty, map);
    }

    @Test
    public void getOrElse() {
        Map<String,Integer> map = MapOps.newHashMap();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        FunctionalMap<String, Integer> fMap = FunctionalMap.create(map);

        Integer foo = fMap.getOrElse("foo", 3);
        assertNotNull(foo);
        foo = fMap.get("foo");
        assertNull(foo);

        FunctionalMap<String, Integer> withDefaultValue = fMap.withDefaultValue(23);
        assertNotNull(withDefaultValue.get("foo"));
        assertEquals(Integer.valueOf(23), withDefaultValue.get("foo"));
    }

    @Test
    public void mapValues() {
        Map<String, String> map = MapOps.newHashMap();
        map.put("one", "1");
        map.put("two", "2");
        map.put("three", "3");
        FunctionalMap<String, String> wrapper = FunctionalMap.create(map);
        FunctionalMap<String, Integer> mappedValues = wrapper.mapValues(new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return Integer.valueOf(input);
            }
        });

        assertNotSame(wrapper, mappedValues);

        assertEquals(Integer.valueOf(3), mappedValues.get("three"));
    }
}
