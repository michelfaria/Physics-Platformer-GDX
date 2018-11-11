package io.github.michelfaria.breadprototype.strategy

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3

class UnprojectStrategy(private val camera: Camera) {

    fun unproject(screenCoords: Vector3, viewportX: Float, viewportY: Float, viewportWidth: Float, viewportHeight: Float): Vector3 {
        return camera.unproject(screenCoords, viewportX, viewportY, viewportWidth, viewportHeight)
    }

    fun unproject(screenCoords: Vector3): Vector3 {
        return camera.unproject(screenCoords)
    }
}
