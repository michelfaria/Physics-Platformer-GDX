package io.github.michelfaria.breadprototype;

import org.jetbrains.annotations.NotNull;

public class Pair<T>
{
    public @NotNull T a;
    public @NotNull T b;

    public Pair(@NotNull T a, @NotNull T b)
    {
        this.a = a;
        this.b = b;
    }
}
