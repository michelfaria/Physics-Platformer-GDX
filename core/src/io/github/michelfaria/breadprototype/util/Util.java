package io.github.michelfaria.breadprototype.util;

import java.util.Objects;
import java.util.function.Supplier;

public class Util {
    public static <T> T _n(T o) {
        return Objects.requireNonNull(o);
    }

    public static <T> T _n(T o, String message) {
        return Objects.requireNonNull(o, message);
    }

    public static <T> T _n(T o, Supplier<String> messageSupplier) {
        return Objects.requireNonNull(o, messageSupplier);
    }
}
