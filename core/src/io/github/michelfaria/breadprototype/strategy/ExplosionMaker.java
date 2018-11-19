package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import io.github.michelfaria.breadprototype.fud.TNTBlockUD;
import io.github.michelfaria.breadprototype.logic.IgnoreExplosions;

public class ExplosionMaker {

    private static final int NUM_RAYS = 1_000;

    public void makeExplosion(World world, Vector2 center, float blastRadius, float blastPower) {
        Vector2 rayDir = new Vector2();
        Vector2 rayEnd = new Vector2();

        for (int i = 0; i < NUM_RAYS; i++) {
            float angle = (i / (float) NUM_RAYS) * 360 * MathUtils.degreesToRadians;
            rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
            rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

            RayCastCallback cb = (fixture, point, normal, fraction) -> {
                if (fixture.getUserData() instanceof IgnoreExplosions) {
                    return 0;
                }
                if (fixture.getUserData() instanceof TNTBlockUD) {
                    TNTBlockUD fud = (TNTBlockUD) fixture.getUserData();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            fud.trigger();
                        }
                    }, 1);
                }
                applyBlastImpulse(fixture.getBody(), center, point, blastPower / (float) NUM_RAYS);
                return 0;
            };
            world.rayCast(cb, center, rayEnd);
        }
    }

    private void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
        float distance = blastDir.len();
        if (distance == 0) {
            return;
        }
        float invDistance = 1 / distance;
        float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f);
        body.applyLinearImpulse(blastDir.nor().scl(impulseMag), applyPoint, true);
    }
}
