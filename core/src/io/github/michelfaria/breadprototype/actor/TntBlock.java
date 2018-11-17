package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import io.github.michelfaria.breadprototype.TextureRegionNames;
import io.github.michelfaria.breadprototype.fud.BlockUD;
import io.github.michelfaria.breadprototype.fud.TNTBlockUD;
import io.github.michelfaria.breadprototype.strategy.ExplosionMaker;

import static io.github.michelfaria.breadprototype.util.Util._n;

public class TntBlock extends Block {

    private static final float BLAST_RADIUS = 10;
    private static final float BLAST_POWER = 100_000;

    private ExplosionMaker explosionMaker;

    private boolean isTriggered;

    public TntBlock(World world, ParticleEffectPool blockCreationEffectPool, TextureAtlas atlas, ExplosionMaker explosionMaker) {
        super(world, blockCreationEffectPool, _n(atlas.findRegion(TextureRegionNames.TNT)));
        this.explosionMaker = explosionMaker;
    }

    public void trigger() {
        isTriggered = true;
        explode();
    }

    private void explode() {
        explosionMaker.makeExplosion(world, body.getWorldCenter(), BLAST_RADIUS, BLAST_POWER);
        dispose();
    }


    public boolean isTriggered() {
        return isTriggered;
    }

    @Override
    protected BlockUD newUserData() {
        return new TNTBlockUD(this);
    }
}
