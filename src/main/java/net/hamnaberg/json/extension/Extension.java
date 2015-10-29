package net.hamnaberg.json.extension;

import net.hamnaberg.json.Json;

import java.util.*;

public abstract class Extension<A> {
    public abstract A extract(Json.JObject node);
    public abstract Json.JObject apply(A value);

    public static <A, B> Extension<Tuple2<A, B>> combine(Extension<A> a, Extension<B> b) {
        return new Tuple2Extension<A, B>(Tuple2.of(a, b));
    }

    public static <A, B, C> Extension<Tuple3<A, B, C>> combine3(Extension<A> a, Extension<B> b, Extension<C> c) {
        return new Tuple3Extension<A, B, C>(Tuple3.of(a, b, c));
    }

    private static class Tuple2Extension<A, B> extends Extension<Tuple2<A, B>> {
        private Tuple2<Extension<A>, Extension<B>> delegate;

        private Tuple2Extension(Tuple2<Extension<A>, Extension<B>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Tuple2<A, B> extract(Json.JObject node) {
            return new Tuple2<>(delegate._1.extract(node), delegate._2.extract(node));
        }

        @Override
        public Json.JObject apply(Tuple2<A, B> value) {
            Json.JObject first = delegate._1.apply(value._1);
            return first.concat(delegate._2.apply(value._2));
        }
    }

    private static class Tuple3Extension<A, B, C> extends Extension<Tuple3<A, B, C>> {
        private Tuple3<Extension<A>, Extension<B>, Extension<C>> delegate;

        private Tuple3Extension(Tuple3<Extension<A>, Extension<B>, Extension<C>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Tuple3<A, B, C> extract(Json.JObject node) {
            return Tuple3.of(delegate._1.extract(node), delegate._2.extract(node), delegate._3.extract(node));
        }

        @Override
        public Json.JObject apply(Tuple3<A, B, C> value) {
            Json.JObject first = delegate._1.apply(value._1);
            return first
                    .concat(delegate._2.apply(value._2))
                    .concat(delegate._3.apply(value._3));
        }
    }
}
