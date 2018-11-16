package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.physics.box2d.*;
import io.github.michelfaria.breadprototype.fud.WandProjectileFUD;
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD;
import io.github.michelfaria.breadprototype.fud.SolidFUD;
import io.github.michelfaria.breadprototype.strategy.BlockSpawner;
import io.github.michelfaria.breadprototype.strategy.TodoListAppender;
import io.github.michelfaria.breadprototype.util.Pair;

import static io.github.michelfaria.breadprototype.util.FixtureUtil.*;

public class MyContactListener implements ContactListener {

    private final BlockSpawner blockSpawner;
    private final TodoListAppender todoListAppender;

    public MyContactListener(BlockSpawner blockSpawner, TodoListAppender todoListAppender) {
        this.blockSpawner = blockSpawner;
        this.todoListAppender = todoListAppender;
    }

    @Override
    public void beginContact(Contact contact) {
        final Pair<Fixture> fs = getFixturePair(contact);
        playerGrounded(fs, true);
        wandProjectileCollision(fs);
    }

    @Override
    public void endContact(Contact contact) {
        final Pair<Fixture> fs = getFixturePair(contact);
        playerGrounded(fs, false);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void playerGrounded(Pair<Fixture> fs, boolean grounded) {
        final Pair<Fixture> m = getFixturePairMatchByClass(fs, PlayerFeetFUD.class, SolidFUD.class);
        if (m != null) {
            final PlayerFeetFUD fud = (PlayerFeetFUD) getFUD(m.a);
            fud.getPlayer().setGrounded(grounded);
        }
    }

    private void wandProjectileCollision(Pair<Fixture> fs) {
        final Pair<Fixture> m = getFixturePairMatchByClass(fs, WandProjectileFUD.class, SolidFUD.class);
        if (m != null) {
            final WandProjectileFUD fud = (WandProjectileFUD) getFUD(m.a);
            todoListAppender.addTask(() -> {
                if (fud.isProjectileAlive()) {
                    fud.killProjectile();
                    blockSpawner.spawnBlock(m.a.getBody().getPosition().x, m.a.getBody().getPosition().y);
                }
            });
        }
    }
}
