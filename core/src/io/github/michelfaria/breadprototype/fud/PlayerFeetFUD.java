package io.github.michelfaria.breadprototype.fud;

import io.github.michelfaria.breadprototype.actor.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerFeetFUD implements FUD {

    private final @NotNull Player player;

    public PlayerFeetFUD(@NotNull Player player) {
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}