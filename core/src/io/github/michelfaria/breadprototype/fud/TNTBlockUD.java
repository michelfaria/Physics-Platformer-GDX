package io.github.michelfaria.breadprototype.fud;

import io.github.michelfaria.breadprototype.actor.TntBlock;

public class TNTBlockUD implements BlockUD {

    private TntBlock tntBlock;

    public TNTBlockUD(TntBlock tntBlock) {
        this.tntBlock = tntBlock;
    }

    public void trigger() {
        tntBlock.trigger();
    }
}
