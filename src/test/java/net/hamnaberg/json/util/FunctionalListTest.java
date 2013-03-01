package net.hamnaberg.json.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static net.hamnaberg.json.util.Optional.none;
import static net.hamnaberg.json.util.Optional.some;
import static org.junit.Assert.*;

public class FunctionalListTest {
    @Test
    public void construct() {
        FunctionalList<Object> objects = FunctionalList.create(new ArrayList<Object>());
        assertNotNull(objects);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void wrappingOfSelfFails() {
        FunctionalList.create(FunctionalList.empty());
    }

    @Test
    public void mapStringToInt() {
        FunctionalList<String> list = FunctionalList.of("1", "2", "3", "4", "5");
        FunctionalList<Integer> ints = FunctionalList.of(1, 2, 3, 4, 5);
        assertEquals(ints, list.map(new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return Integer.valueOf(input);
            }
        }));
    }


    @Test
    public void flatMapStringToInt() {
        FunctionalList<String> list = FunctionalList.of("1", "2", "one", "3");
        FunctionalList<Integer> ints = FunctionalList.of(1, 2, 3);
        assertEquals(ints, list.flatMap(new Function<String, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(String input) {
                try {
                    return some(Integer.parseInt(input));
                }
                catch (Exception e) {
                    return Optional.none();
                }
            }
        }));

    }
}
