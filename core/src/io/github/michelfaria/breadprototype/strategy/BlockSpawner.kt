package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.michelfaria.breadprototype.actor.Block

class BlockSpawner(private val stage: Stage,
                   private val blockFactory: Block.Factory) {

    fun spawnBlock(x: Float, y: Float) {
        val block = blockFactory.make(x, y)
        stage.addActor(block)
        block.addNewCreationEffect()
    }

    fun removeBlock(x: Float, y: Float) {
        val hit = stage.hit(x, y, true)
        if (hit != null) {
            if (hit is Block) {
                hit.remove()
                hit.dispose()
            }
        }
    }
}
