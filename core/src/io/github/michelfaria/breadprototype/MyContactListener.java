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
        setPlayerGroundedIfContact(fixtures, true);
    }

    @Override
    public void endContact(Contact contact) {
        final Pair<Fixture> fixtures = getFixturePair(contact);
        setPlayerGroundedIfContact(fixtures, false);
    }

    private void setPlayerGroundedIfContact(Pair<Fixture> fixtures, boolean grounded) {
        final Pair<Fixture> match = getFixturePairMatchByClass(fixtures, PlayerFeetFUD.class, WorldSolidFUD.class);
        if (match != null) {
            ((PlayerFeetFUD) FixtureUtil.getFUD(match.a))
                    .getPlayer()
                    .setGrounded(grounded);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
