package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.physics.box2d.*;
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD;
import io.github.michelfaria.breadprototype.fud.WorldSolidFUD;

import static io.github.michelfaria.breadprototype.FixtureUtil.getFixturePair;
import static io.github.michelfaria.breadprototype.FixtureUtil.getFixturePairMatchByClass;

public class MyContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        final Pair<Fixture> fixtures = getFixturePair(contact);
        doGroundedCheck(fixtures, true);
    }

    @Override
    public void endContact(Contact contact) {
        final Pair<Fixture> fixtures = getFixturePair(contact);
        doGroundedCheck(fixtures, false);
    }

    private void doGroundedCheck(Pair<Fixture> fixtures, boolean b) {
        final Pair<Fixture> match = getFixturePairMatchByClass(fixtures, PlayerFeetFUD.class, WorldSolidFUD.class);
        if (match != null) {
            ((PlayerFeetFUD) FixtureUtil.getFUD(match.a))
                    .getPlayer()
                    .setGrounded(b);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
