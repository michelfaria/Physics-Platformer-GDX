package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import io.github.michelfaria.breadprototype.logic.Positionable

abstract class PhysicsActor(protected val world: World) : Actor(), Positionable {

    lateinit var body: Body

    override fun act(delta: Float) {
        super.act(delta)
        syncWithBody()
    }

    protected fun drawTextureAtBody(batch: Batch, texture: TextureRegion) {
        batch.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    open fun syncWithBody() {
        x = body.position.x - width / 2
        y = body.position.y - height / 2
        originX = width / 2
        originY = height / 2
        rotation = body.angle * MathUtils.radiansToDegrees
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        body.setTransform(x, y, body.angle)
        syncWithBody()
    }

    override fun remove(): Boolean {
        world.destroyBody(body)
        return super.remove()
    }
}