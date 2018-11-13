package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Touchable
import io.github.michelfaria.breadprototype.Assets
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.Game
import io.github.michelfaria.breadprototype.TextureRegionNames
import io.github.michelfaria.breadprototype.fud.BlockFUD
import io.github.michelfaria.breadprototype.strategy.EffectDrawer
import kotlin.experimental.or

class Block(world: World, private val atlas: TextureAtlas) : PhysicsActor(world) {

    override val body: Body

    private val texture: TextureRegion
    private val effectDrawer: EffectDrawer = EffectDrawer()
    private val creationEffectPool: ParticleEffectPool = newCreationEffectPool()

    init {
        texture = atlas.findRegion(TextureRegionNames.DIRT)
        width = 1f
        height = 1f
        touchable = Touchable.enabled
        body = newPhysicsBody()
    }

    private fun newPhysicsBody(): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.x = x
            position.y = y
        }
        return world.createBody(bodyDef).apply {
            newMainFixture(body)
        }
    }

    private fun newMainFixture(body: Body) {
        val shape = PolygonShape().apply {
            setAsBox(width / 2, height / 2)
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            filter.categoryBits = BIT_SOLID or BIT_ENTITY
            density = 1f
        }
        body.createFixture(fixtureDef).userData = BlockFUD()
        shape.dispose()
    }

    private fun newCreationEffectPool(): ParticleEffectPool {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_BLOCK_CREATE, atlas)
            scaleEffect(Game.ptm(1f))
            setEmittersCleanUpBlendFunction(false)
        }
        return ParticleEffectPool(e, 1, 5)
    }

    fun addNewCreationEffect() {
        creationEffectPool.obtain().apply {
            setPosition(x + width / 2, y + height / 2)
        }.also {
            effectDrawer.add(it)
            it.start()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch!!
        drawTextureAtBody(batch, texture)
        effectDrawer.drawEffects(batch)
    }

    class Factory(private val world: World, private val atlas: TextureAtlas) {
        fun make(): Block {
            return Block(world, atlas)
        }
    }
}