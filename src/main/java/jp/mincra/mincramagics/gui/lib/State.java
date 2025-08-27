package jp.mincra.mincramagics.gui.lib;

import java.util.function.Function;

public class State<T> {
    private final T val;
    private final Function<Function<T, T>, Void> setter;

    public State(T value, Function<Function<T, T>, Void> setter) {
        this.val = value;
        this.setter = setter;
    }

    public T value() {
        return val;
    }

    public void set(Function<T, T> function) {
        setter.apply(function);
    }

    public void set(T value) {
        set(old -> value);
    }
}
