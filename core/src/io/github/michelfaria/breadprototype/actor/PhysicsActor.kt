package io.github.michelfaria.breadprototype.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor

abstract class PhysicsActor : Actor() {

    lateinit var body: Body

    override fun act(delta: Float) {
        super.act(delta)
        syncWithBody()
    }

    protected fun drawTextureAtBody(batch: Batch?, texture: TextureRegion) {
        batch!!.draw(texture, x, y, originX + width / 2, originY + height / 2, width, height, scaleX, scaleY, rotation)
    }

    private fun syncWithBody() {
        x = body.position.x - width / 2
        y = body.position.y - height / 2
        rotation = body.angle * MathUtils.radiansToDegrees
    }
}