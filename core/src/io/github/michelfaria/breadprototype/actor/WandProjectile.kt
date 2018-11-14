package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import io.github.michelfaria.breadprototype.Assets
import io.github.michelfaria.breadprototype.Bits
import io.github.michelfaria.breadprototype.Game
import io.github.michelfaria.breadprototype.fud.WandProjectileFUD
import java.lang.Math.*

class WandProjectile(world: World,
                     private val atlas: TextureAtlas,
                     var destX: Float, var destY: Float) : PhysicsActor(world) {

    companion object {
        const val SPEED = 5
    }

    private val wandParticlePool = newWandParticlePool()
    private val effect = newParticleEffect()

    init {
        width = 0.5f
        height = 0.5f
        initPhysicsBody()
    }

    private fun initPhysicsBody() {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.KinematicBody
        }
        val shape = CircleShape().apply {
            radius = 0.25f
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            filter.categoryBits = Bits.BIT_PROJECTILE
            filter.maskBits = Bits.BIT_SOLID
        }
        body = world.createBody(bodyDef)
        body.createFixture(fixtureDef).userData = WandProjectileFUD()
        shape.dispose()
    }

    private fun newWandParticlePool(): ParticleEffectPool {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_WAND_PARTICLE, atlas)
            scaleEffect(Game.ptm(0.25f))
            setEmittersCleanUpBlendFunction(false)
        }
        return ParticleEffectPool(e, 1, 10)
    }

    private fun newParticleEffect(): ParticleEffectPool.PooledEffect {
        return wandParticlePool.obtain().also {
            it.start()
        }
    }

    fun goToDestination() {
        val angle = atan2(destY - y.toDouble() - originY, destX - x.toDouble() - originX)
        body.setLinearVelocity(cos(angle).toFloat() * SPEED, sin(angle).toFloat() * SPEED)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        drawParticles(batch!!)
    }

    private fun drawParticles(batch: Batch) {
        effect.draw(batch, Gdx.graphics.deltaTime)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun act(delta: Float) {
        super.act(delta)
        effect.setPosition(x + originX, y + originY)
    }
}