package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.TextureRegionNames
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD
import io.github.michelfaria.breadprototype.logic.Positionable

class Player(world: World, atlas: TextureAtlas) : PhysicsActor(world), Positionable {

    companion object {
        const val MOVE_VEL_X = 6
        const val JUMP_FORCE = 100
    }

    private val idleTexture: TextureRegion
    var isGrounded = false

    init {
        idleTexture = atlas.findRegion(TextureRegionNames.PLAYER_IDLE)
        width = 1f
        height = 1f
        initPhysicsBody()
        initBodyFixture()
        initGroundedSensorBodyFixture()
    }

    private fun initPhysicsBody() {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
        }
        body = world.createBody(bodyDef)
        body.isFixedRotation = true
    }

    private fun initBodyFixture() {
        val shape = PolygonShape().apply {
            setAsBox(width / 2, height / 2)
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            friction = 0f
            filter.categoryBits = BIT_ENTITY
        }
        body.createFixture(fixtureDef)
        shape.dispose()
    }

    private fun initGroundedSensorBodyFixture() {
        val left = x - width / 2 + 0.05f
        val bottom = y - height / 2 - 0.05f
        val right = x + width / 2 - 0.05f

        val shape = EdgeShape().apply {
            set(left, bottom, right, bottom)
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            isSensor = true
            filter.categoryBits = BIT_ENTITY
            filter.maskBits = BIT_SOLID
        }
        body.createFixture(fixtureDef).userData = PlayerFeetFUD(this)
        shape.dispose()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        drawTextureAtBody(batch!!, idleTexture)
    }

    override fun act(delta: Float) {
        super.act(delta)
        handleInput()
    }

    private fun handleInput() {
        val vel = body.linearVelocity

        var movedX = false

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(MOVE_VEL_X.toFloat(), vel.y)
            movedX = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity((-MOVE_VEL_X).toFloat(), vel.y)
            movedX = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (isGrounded) {
                body.applyForce(0f, JUMP_FORCE.toFloat(), body.worldCenter.x, body.worldCenter.y, true)
            }
        }
        if (!movedX) {
            body.setLinearVelocity(0f, vel.y)
        }
    }
}
