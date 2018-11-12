package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Actor
import io.github.michelfaria.breadprototype.Bits.BIT_ENTITY
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.TextureRegionNames
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD

class Player(atlas: TextureAtlas, world: World) : PhysicsActor() {

    companion object {
        const val MOVE_VEL_X = 6
        const val JUMP_FORCE = 100
    }

    private val idleTexture: TextureRegion
    var isGrounded: Boolean = false

    init {
        idleTexture = atlas.findRegion(TextureRegionNames.PLAYER_IDLE)
        width = 1f
        height = 1f
        makePhysicsBody(world)
    }

    private fun makePhysicsBody(world: World) {
        val bdef = BodyDef()
        bdef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bdef)
        body.isFixedRotation = true
        createBodyFixture()
        createGroundedSensorFixture()
    }

    private fun createBodyFixture() {
        val shape = PolygonShape()
        val fdef = FixtureDef()

        shape.setAsBox(width / 2, height / 2)
        fdef.apply {
            this.shape = shape
            friction = 0f
            filter.categoryBits = BIT_ENTITY
        }
        body.createFixture(fdef)
        shape.dispose()
    }

    private fun createGroundedSensorFixture() {
        val shape = EdgeShape()
        val fdef = FixtureDef()

        val left = x - width / 2 + 0.05f
        val bottom = y - height / 2 - 0.05f
        val right = x + width / 2 - 0.05f

        shape.set(left, bottom, right, bottom)
        fdef.apply {
            this.shape = shape
            isSensor = true
            filter.categoryBits = BIT_ENTITY
            filter.maskBits = BIT_SOLID
        }
        body.createFixture(fdef).userData = PlayerFeetFUD(this)
        shape.dispose()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        drawTextureAtBody(batch, idleTexture)
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
