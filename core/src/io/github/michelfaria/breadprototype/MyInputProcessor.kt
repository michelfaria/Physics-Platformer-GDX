package io.github.michelfaria.breadprototype

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3
import io.github.michelfaria.breadprototype.logic.Positionable
import io.github.michelfaria.breadprototype.strategy.BlockSpawner
import io.github.michelfaria.breadprototype.strategy.Unprojector
import io.github.michelfaria.breadprototype.strategy.WandProjectileSpawner

class MyInputProcessor(private val blockSpawner: BlockSpawner,
                       private val wandProjectileSpawner: WandProjectileSpawner,
                       private val unprojector: Unprojector,
                       private val player: Positionable) : InputProcessor {

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
        val v = unprojector.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        return when (button) {
            Input.Buttons.LEFT -> {
                // blockSpawner.spawnBlock(v.x, v.y)
                wandProjectileSpawner.spawn(player.getX() + player.getWidth() / 2,
                        player.getY() + player.getHeight() / 2, v.x, v.y)
                true
            }
            Input.Buttons.RIGHT -> {
                blockSpawner.removeBlock(v.x, v.y)
                true
            }
            else -> false
        }
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
