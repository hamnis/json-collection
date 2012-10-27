package net.hamnaberg.json.extension;

public class Tuple3<A, B, C> {
    public final A _1;
    public final B _2;
    public final C _3;

    public Tuple3(A _1, B _2, C _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3 tuple3 = (Tuple3) o;

        if (_1 != null ? !_1.equals(tuple3._1) : tuple3._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple3._2) : tuple3._2 != null) return false;
        if (_3 != null ? !_3.equals(tuple3._3) : tuple3._3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        return result;
    }

    public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
        return new Tuple3<A, B, C>(a, b, c);
    }

}
