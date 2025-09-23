package jp.mincra.mincramagics.utils;

public class Tuple<A,B> {
    private final A a;
    private final B b;

    private Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A,B> Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

    A a() {
        return this.a;
    }

    B b() {
        return this.b;
    }
}
