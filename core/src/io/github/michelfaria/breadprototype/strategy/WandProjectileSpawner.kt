package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.michelfaria.breadprototype.actor.WandProjectile

class WandProjectileSpawner(private val stage: Stage, private val world: World, private val atlas: TextureAtlas) {

    fun spawn(x: Float, y: Float, destX: Float, destY: Float): WandProjectile {
        return WandProjectile(world, atlas, destX, destY).also {
            stage.addActor(it)
        }.apply {
            setPosition(x, y)
            goToDestination()
        }
    }
}