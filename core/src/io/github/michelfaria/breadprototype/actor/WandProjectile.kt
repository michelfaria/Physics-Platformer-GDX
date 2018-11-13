package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.*
import io.github.michelfaria.breadprototype.Assets
import io.github.michelfaria.breadprototype.Bits
import io.github.michelfaria.breadprototype.Game
import io.github.michelfaria.breadprototype.fud.WandProjectileFUD
import io.github.michelfaria.breadprototype.strategy.EffectDrawer

class WandProjectile(world: World, private val atlas: TextureAtlas) : PhysicsActor(world) {

    override val body: Body

    private val effectDrawer: EffectDrawer = EffectDrawer()
    private val wandParticlePool: ParticleEffectPool = newWandParticlePool()

    init {
        width = 0.5f
        height = 0.5f
        body = newPhysicsBody()
    }

    private fun newPhysicsBody(): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.KinematicBody
            position.x = x
            position.y = y
        }
        return world.createBody(bodyDef).apply {
            newMainFixture(this)
        }
    }

    private fun newMainFixture(body: Body) {
        val shape = CircleShape().apply {
            radius = 0.25f
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            filter.categoryBits = Bits.BIT_PROJECTILE
        }
        body.createFixture(fixtureDef).userData = WandProjectileFUD()
        shape.dispose()
    }

    private fun newWandParticlePool(): ParticleEffectPool {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_WAND_PARTICLE, atlas)
            scaleEffect(Game.ptm(0.5f))
            setEmittersCleanUpBlendFunction(false)
        }
        return ParticleEffectPool(e, 1, 2)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        effectDrawer.drawEffects(batch!!)
    }
}