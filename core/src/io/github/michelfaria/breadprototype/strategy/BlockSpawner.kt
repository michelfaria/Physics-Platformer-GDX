package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.michelfaria.breadprototype.actor.Block

class BlockSpawner(private val stage: Stage,
                   private val blockFactory: Block.Factory) {

    fun spawnBlock(x: Float, y: Float) {
        blockFactory.make().also {
            stage.addActor(it)
        }.apply {
            setBodyPosition(Vector2(x, y))
            addNewCreationEffect()
        }
    }

    fun removeBlock(x: Float, y: Float) {
        stage.hit(x, y, true)?.apply {
            if (this is Block) {
                remove()
                dispose()
            }
        }
    }
}
