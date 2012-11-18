/*
 * Copyright 2012 Erlend Hamnaberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hamnaberg.json.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/8/11
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListOps {

    public static <A> List<A> of(A... values) {
        return Arrays.asList(values);
    }

    public static <A> ArrayList<A> newArrayList() {
        return new ArrayList<A>();
    }

    public static <A, B> List<B> transform(final List<A> list, final Function<A, B> f) {
        return com.google.common.collect.Lists.transform(list, f);
    }

    public static <A> List<A> filter(final List<A> list, final Predicate<A> f) {
        ImmutableList.Builder<A> copy = new ImmutableList.Builder<A>();
        for (A a : list) {
            if (f.apply(a)) {
                copy.add(a);
            }
        }
        return copy.build();
    }

    public static <A> Optional<A> find(final Collection<A> coll, final Predicate<A> f) {
        for (A a : coll) {
            if (f.apply(a)) {
                return Optional.fromNullable(a);
            }
        }
        return Optional.absent();
    }
}
