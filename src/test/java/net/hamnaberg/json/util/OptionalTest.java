package net.hamnaberg.json.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class OptionalTest {

    @Test(expected = IllegalArgumentException.class)
    public void failWhenAddingNullToSome() {
        Optional.some(null);
    }

    @Test
    public void iterableIsWorkingCorrectly() {
        int count = 0;
        for (String s : Optional.some("hello")) {
            assertEquals("hello", s);
            count++;
        }

        assertEquals("Iterator of Some did not produce values", 1, count);
    }
    @Test
    public void iterableOfNoneIsWorkingCorrectly() {
        for (String s : Optional.<String>none()) {
            fail("Iterator of none produced values");
        }
    }
}
