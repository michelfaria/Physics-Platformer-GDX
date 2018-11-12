package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.michelfaria.breadprototype.actor.Block

class BlockSpawningStrategy(private val world: World, private val atlas: TextureAtlas, private val stage: Stage) {

    fun spawnBlock(x: Float, y: Float) {
        val block = Block(world, atlas, x, y)
        stage.addActor(block)
    }
}
