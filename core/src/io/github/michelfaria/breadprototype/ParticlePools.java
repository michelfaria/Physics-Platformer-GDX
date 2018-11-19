package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public final class ParticlePools {

    private final TextureAtlas atlas;

    public final ParticleEffectPool
            blockCreationPEP,
            wandProjectilePEP,
            explosionSmokePEP,
            novaPEP;

    public ParticlePools(TextureAtlas atlas) {
        this.atlas = atlas;
        blockCreationPEP = newBlockCreationPEP();
        wandProjectilePEP = newWandPEP();
        explosionSmokePEP = newExplosionSmokePEP();
        novaPEP = newNovaPEP();
    }

    private ParticleEffectPool newBlockCreationPEP() {
        final ParticleEffect p = new ParticleEffect();
        p.load(Assets.EFFECT_BLOCK_CREATE, atlas);
        p.scaleEffect(Game.ptm(1));
        p.setEmittersCleanUpBlendFunction(false);
        return new ParticleEffectPool(p, 1, 5);
    }

    private ParticleEffectPool newWandPEP() {
        final ParticleEffect p = new ParticleEffect();
        p.load(Assets.EFFECT_WAND_PARTICLE, atlas);
        p.scaleEffect(Game.ptm(0.25f));
        p.setEmittersCleanUpBlendFunction(false);
        return new ParticleEffectPool(p, 3, 15);
    }

    private ParticleEffectPool newExplosionSmokePEP() {
        final ParticleEffect p = new ParticleEffect();
        p.load(Assets.EFFECT_EXPLOSION_SMOKE, atlas);
        p.scaleEffect(Game.ptm(0.5f));
        p.setEmittersCleanUpBlendFunction(true);
        return new ParticleEffectPool(p, 1, 5);
    }

    private ParticleEffectPool newNovaPEP() {
        final ParticleEffect p = new ParticleEffect();
        p.load(Assets.EFFECT_NOVA, atlas);
        p.scaleEffect(Game.ptm(1));
        p.setEmittersCleanUpBlendFunction(false);
        return new ParticleEffectPool(p, 1, 5);
    }
}
