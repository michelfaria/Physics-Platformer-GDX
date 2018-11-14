package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.michelfaria.breadprototype.actor.Block

class BlockSpawner(private val stage: Stage,
                   private val world: World,
                   private val atlas: TextureAtlas,
                   private val blockCreationEffectPool: ParticleEffectPool) {

    fun spawnBlock(x: Float, y: Float): Block {
        return Block(world, atlas, blockCreationEffectPool).also {
            stage.addActor(it)
        }.apply {
            setPosition(x, y)
            addNewCreationEffect()
        }
    }

    fun removeBlock(x: Float, y: Float) {
        stage.hit(x, y, true)?.apply {
            if (this is Block) {
                kill()
            }
        }
    }
}
