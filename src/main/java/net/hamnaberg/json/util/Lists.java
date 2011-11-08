package net.hamnaberg.json.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/8/11
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Lists {

    public static <A> List<A> of(A... values) {
        return Arrays.asList(values);
    }

    public static <A> ArrayList<A> newArrayList() {
        return new ArrayList<A>();
    }

    public static <A, B> List<B> map(final List<A> list, final F<A, B> f) {
        return new AbstractList<B>() {
            @Override
            public B get(int i) {
                return f.apply(list.get(i));
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    public static <A> List<A> filter(final List<A> list, final Predicate<A> f) {
        ArrayList<A> copy = new ArrayList<A>();
        for (A a : list) {
            if (f.apply(a)) {
                copy.add(a);
            }
        }
        return copy;
    }
}
