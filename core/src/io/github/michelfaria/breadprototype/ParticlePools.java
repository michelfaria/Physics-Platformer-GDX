package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public final class ParticlePools {

    private final TextureAtlas atlas;

    public final ParticleEffectPool
            blockCreationEffectPool,
            wandProjectileEffectPool;

    public ParticlePools(TextureAtlas atlas) {
        this.atlas = atlas;
        blockCreationEffectPool = newBlockCreationEffectPool();
        wandProjectileEffectPool = newWandParticlePool();
    }

    private ParticleEffectPool newBlockCreationEffectPool() {
        final ParticleEffect p = new ParticleEffect();
        {
            p.load(Assets.EFFECT_BLOCK_CREATE, atlas);
            p.scaleEffect(Game.ptm(1));
            p.setEmittersCleanUpBlendFunction(false);
        }
        return new ParticleEffectPool(p, 1, 5);
    }

    private ParticleEffectPool newWandParticlePool() {
        final ParticleEffect p = new ParticleEffect();
        {
            p.load(Assets.EFFECT_WAND_PARTICLE, atlas);
            p.scaleEffect(Game.ptm(0.25f));
            p.setEmittersCleanUpBlendFunction(false);
        }
        return new ParticleEffectPool(p, 30, 50);
    }
}
