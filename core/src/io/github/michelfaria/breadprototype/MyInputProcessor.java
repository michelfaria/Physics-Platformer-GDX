package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import io.github.michelfaria.breadprototype.logic.Positionable;
import io.github.michelfaria.breadprototype.strategy.BlockSpawner;
import io.github.michelfaria.breadprototype.strategy.Unprojector;
import io.github.michelfaria.breadprototype.strategy.WandProjectileSpawner;

public class MyInputProcessor implements InputProcessor {

    private final BlockSpawner blockSpawner;
    private final WandProjectileSpawner wandProjectileSpawner;
    private final Unprojector unprojector;
    private final Positionable player;

    public MyInputProcessor(BlockSpawner blockSpawner, WandProjectileSpawner wandProjectileSpawner,
                            Unprojector unprojector, Positionable player) {
        this.blockSpawner = blockSpawner;
        this.wandProjectileSpawner = wandProjectileSpawner;
        this.unprojector = unprojector;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.O) {
            blockSpawner.spawnDirt = true;
            return true;
        } else if (keycode == Input.Keys.P) {
            blockSpawner.spawnDirt = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        final Vector3 v = unprojector.unproject(new Vector3(screenX, screenY, 0));
        switch (button) {
            case Input.Buttons.LEFT:
                wandProjectileSpawner.spawn(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, v.x, v.y);
                break;
            case Input.Buttons.RIGHT:
                blockSpawner.removeBlock(v.x, v.y);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
