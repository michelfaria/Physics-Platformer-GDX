package io.github.michelfaria.breadprototype

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class ParticlePools(private val atlas: TextureAtlas) {

    val blockCreationEffectPool: ParticleEffectPool = newBlockCreationEffectPool()
    val wandProjectileEffectPool: ParticleEffectPool = newWandParticlePool()

    private fun newBlockCreationEffectPool(): ParticleEffectPool {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_BLOCK_CREATE, atlas)
            scaleEffect(Game.ptm(1f))
            setEmittersCleanUpBlendFunction(false)
        }
        return ParticleEffectPool(e, 1, 5)
    }

    private fun newWandParticlePool(): ParticleEffectPool {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_WAND_PARTICLE, atlas)
            scaleEffect(Game.ptm(0.25f))
            setEmittersCleanUpBlendFunction(false)
        }
        return ParticleEffectPool(e, 10, 20)
    }
}