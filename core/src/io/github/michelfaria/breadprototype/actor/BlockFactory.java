package io.github.michelfaria.breadprototype.actor;

public interface BlockFactory<T extends Block> {
    T make();
}
