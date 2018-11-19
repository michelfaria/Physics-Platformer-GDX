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

    private final ExplosionMaker explosionMaker;
    private final ExplosionEmitter.ExplosionEmitterFactory explosionEmitterFactory;

    private boolean isTriggered;

    public TntBlock(World world, ParticleEffectPool blockCreationPEP, TextureAtlas atlas, ExplosionMaker explosionMaker,
                    ExplosionEmitter.ExplosionEmitterFactory explosionEmitterFactory) {
        super(world, blockCreationPEP, _n(atlas.findRegion(TextureRegionNames.TNT)));
        this.explosionMaker = explosionMaker;
        this.explosionEmitterFactory = explosionEmitterFactory;
    }

    public void trigger() {
        if (!isTriggered) {
            isTriggered = true;
            explode();
        }
    }

    private void explode() {
        explosionMaker.makeExplosion(world, body.getWorldCenter(), BLAST_RADIUS, BLAST_POWER);
        final ExplosionEmitter e = explosionEmitterFactory.make();
        e.setX(getX() + getWidth() / 2);
        e.setY(getY() + getHeight() / 2);
        e.init();
        getStage().addActor(e);
        dispose();
    }

    @Override
    protected BlockUD newUserData() {
        return new TNTBlockUD(this);
    }

    public static class TntBlockFactory implements BlockFactory<TntBlock> {
        private final World world;
        private final ParticleEffectPool blockCreationPEP;
        private final TextureAtlas atlas;
        private final ExplosionMaker explosionMaker;
        private final ExplosionEmitter.ExplosionEmitterFactory explosionEmitterFactory;

        public TntBlockFactory(World world, ParticleEffectPool blockCreationPEP, TextureAtlas atlas,
                               ExplosionMaker explosionMaker,
                               ExplosionEmitter.ExplosionEmitterFactory explosionEmitterFactory) {
            this.world = world;
            this.blockCreationPEP = blockCreationPEP;
            this.atlas = atlas;
            this.explosionMaker = explosionMaker;
            this.explosionEmitterFactory = explosionEmitterFactory;
        }

        public TntBlock make() {
            return new TntBlock(world, blockCreationPEP, atlas, explosionMaker, explosionEmitterFactory);
        }
    }
}
