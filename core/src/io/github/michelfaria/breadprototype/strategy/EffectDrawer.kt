package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.Array;

public class EffectDrawer {
    private val pooledEffects = Array<ParticleEffectPool.PooledEffect>(ParticleEffectPool.PooledEffect::class.java)

    fun drawEffects(batch: Batch) {
        pooledEffects.forEachIndexed { i, e ->
            e.draw(batch, Gdx.graphics.deltaTime)
            if (e.isComplete) {
                e.free()
                pooledEffects.removeIndex(i)
            }
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    fun add(effect: ParticleEffectPool.PooledEffect) {
        pooledEffects.add(effect)
    }
}
