package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import io.github.michelfaria.breadprototype.Bits;
import io.github.michelfaria.breadprototype.fud.MovingPlatformUD;

public class MovingPlatform extends PhysicsActor {

    private static final float DISTANCE_THRESHOLD = 0.05f;

    public Array<Vector2> destinations = new Array<>(Vector2.class);
    public float speed = 1f;

    private int destIndex = 0;

    public MovingPlatform(World world) {
        super(world);
        setWidth(3);
        setHeight(1);
    }

    @Override
    public void init() {
        super.init();
        initBody();
    }

    protected void initBody() {
        final BodyDef b = new BodyDef();
        b.type = BodyDef.BodyType.KinematicBody;

        final PolygonShape s = new PolygonShape();
        s.setAsBox(getWidth() / 2, getHeight() / 2);

        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.filter.categoryBits = (short) (Bits.BIT_SOLID | Bits.BIT_ENTITY);

        body = world.createBody(b);
        body.createFixture(f).setUserData(new MovingPlatformUD());
        s.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
    }

    private void move() {
        if (destinations.size == 0) {
            return;
        }
        final Vector2 destination = destinations.get(destIndex);
        final float angle = MathUtils.atan2(destination.y - getY(), destination.x - getX());

        body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);

        final float distance = destinations.get(destIndex).cpy().sub(new Vector2(getX(), getY())).len();

        if (distance < DISTANCE_THRESHOLD) {
            body.setLinearVelocity(0, 0);
            destIndex = ++destIndex % destinations.size;
        }
    }
}
