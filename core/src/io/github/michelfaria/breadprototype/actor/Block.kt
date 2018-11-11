package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Actor
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.fud.BlockFUD
import io.github.michelfaria.breadprototype.strategy.BlockSpawningStrategy
import kotlin.experimental.or

class Block(private val world: World, x: Float, y: Float) : Actor() {

    lateinit var body: Body

    init {
        this.x = x
        this.y = y
        createPhysicsBody()
    }

    private fun createPhysicsBody() {
        val bdef = BodyDef()
        val shape = PolygonShape()
        val fdef = FixtureDef()

        bdef.apply {
            type = BodyDef.BodyType.DynamicBody
            position.x = x
            position.y = y
        }
        shape.setAsBox(BlockSpawningStrategy.BLOCK_SIZE / 2, BlockSpawningStrategy.BLOCK_SIZE / 2)
        fdef.apply {
            this.shape = shape
            filter.categoryBits = BIT_SOLID or BIT_ENTITY
            density = 1f
        }
        body = world.createBody(bdef)
        body.createFixture(fdef).apply {
            userData = BlockFUD()
        }
        shape.dispose()
    }
}