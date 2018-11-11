package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.physics.box2d.World
import io.github.michelfaria.breadprototype.actor.Block

class BlockSpawningStrategy(private val world: World) {

    companion object {
        const val BLOCK_SIZE = 1f
    }

    fun spawnBlock(x: Float, y: Float) {
        Block(world, x, y)
    }
}
