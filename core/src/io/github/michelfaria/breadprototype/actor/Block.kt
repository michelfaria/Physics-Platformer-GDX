package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Array
import io.github.michelfaria.breadprototype.Assets
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.Game
import io.github.michelfaria.breadprototype.TextureRegionNames
import io.github.michelfaria.breadprototype.fud.BlockFUD
import kotlin.experimental.or

class Block(world: World, private val atlas: TextureAtlas, x: Float, y: Float, val assetManager: AssetManager) : PhysicsActor(world) {

    private val texture: TextureRegion
    private val pooledEffects: Array<ParticleEffectPool.PooledEffect> = Array(ParticleEffectPool.PooledEffect::class.java)

    private lateinit var creationEffectPool: ParticleEffectPool

    init {
        texture = atlas.findRegion(TextureRegionNames.DIRT)
        this.x = x
        this.y = y
        width = 1f
        height = 1f
        touchable = Touchable.enabled
        initPhysicsBody()
        initCreationEffectPool()
    }

    private fun initPhysicsBody() {
        val bdef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.x = x
            position.y = y
        }
        val shape = PolygonShape().apply {
            setAsBox(width / 2, height / 2)
        }
        val fdef = FixtureDef().apply {
            this.shape = shape
            filter.categoryBits = BIT_SOLID or BIT_ENTITY
            density = 1f
        }
        this.body = world.createBody(bdef)
        body.createFixture(fdef).userData = BlockFUD()
        shape.dispose()
    }

    private fun initCreationEffectPool() {
        val e = ParticleEffect().apply {
            load(Assets.EFFECT_BLOCK_CREATE, atlas)
            setEmittersCleanUpBlendFunction(false)
        }
        creationEffectPool = ParticleEffectPool(e, 1, 2)
    }

    fun addNewCreationEffect() {
        creationEffectPool.obtain().apply {
            setPosition(x, y)
            scaleEffect(Game.ptm(1f))
        }.also {
            pooledEffects.add(it)
            it.start()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        drawTextureAtBody(batch, texture)
        drawEffects(batch)
    }

    private fun drawEffects(batch: Batch?) {
        pooledEffects.forEachIndexed { i, e ->
            e.draw(batch!!, Gdx.graphics.deltaTime)
            if (e.isComplete) {
                e.free()
                pooledEffects.removeIndex(i)
            }
        }
        batch!!.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    class Factory(private val world: World, private val atlas: TextureAtlas, private val assetManager: AssetManager) {
        fun make(x: Float, y: Float): Block {
            return Block(world, atlas, x, y, assetManager)
        }
    }
}