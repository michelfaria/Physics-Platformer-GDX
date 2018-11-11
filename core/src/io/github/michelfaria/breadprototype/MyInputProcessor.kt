package io.github.michelfaria.breadprototype

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3
import io.github.michelfaria.breadprototype.strategy.BlockSpawningStrategy
import io.github.michelfaria.breadprototype.strategy.UnprojectStrategy

class MyInputProcessor(private val blockSpawningStrategy: BlockSpawningStrategy,
                       private val unprojectStrategy: UnprojectStrategy) : InputProcessor {

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val v = unprojectStrategy.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        blockSpawningStrategy.spawnBlock(v.x, v.y)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }
}
