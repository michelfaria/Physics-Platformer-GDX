package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.TextureRegionNames
import io.github.michelfaria.breadprototype.fud.BlockFUD
import kotlin.experimental.or

class Block(world: World, atlas: TextureAtlas, x: Float, y: Float) : PhysicsActor(world) {

    private val texture: TextureRegion

    init {
        texture = atlas.findRegion(TextureRegionNames.DIRT)
        this.x = x
        this.y = y
        width = 1f
        height = 1f
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
        shape.setAsBox(width / 2, height / 2)
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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        drawTextureAtBody(batch, texture)
    }
}