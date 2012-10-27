package net.hamnaberg.json.extension;

public class Tuple2<A, B> {
    public final A _1;
    public final B _2;

    public Tuple2(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2 tuple2 = (Tuple2) o;

        if (_1 != null ? !_1.equals(tuple2._1) : tuple2._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple2._2) : tuple2._2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        return result;
    }

    public static <A, B> Tuple2<A, B> of(A a, B b) {
        return new Tuple2<A, B>(a, b);
    }
}
