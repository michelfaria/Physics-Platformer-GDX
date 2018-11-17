package io.github.michelfaria.breadprototype.fud;

import io.github.michelfaria.breadprototype.actor.Player;

public class PlayerFeetUD implements PlayerUD {

    private final Player player;

    public PlayerFeetUD(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}