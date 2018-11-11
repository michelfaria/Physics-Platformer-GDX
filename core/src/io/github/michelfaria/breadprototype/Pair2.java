package io.github.michelfaria.breadprototype;

import org.jetbrains.annotations.NotNull;

public class Pair2<T, R> {
    public @NotNull T a;
    public @NotNull R b;

    public Pair2(@NotNull T a, @NotNull R b) {
        this.a = a;
        this.b = b;
    }
}
