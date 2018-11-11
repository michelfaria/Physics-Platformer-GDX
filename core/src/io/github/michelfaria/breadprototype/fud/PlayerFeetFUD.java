package io.github.michelfaria.breadprototype.fud;

import io.github.michelfaria.breadprototype.Player;

public class PlayerFeetFUD implements FUD {

    private final Player player;

    public PlayerFeetFUD(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
