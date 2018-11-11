package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD;

import java.util.Objects;

import static io.github.michelfaria.breadprototype.Bits.BIT_ENTITY;
import static io.github.michelfaria.breadprototype.Bits.BIT_WORLD;

public class Player extends Actor {
    public static final int MOVE_VEL_X = 6;
    public static final int JUMP_FORCE = 100;

    private final TextureRegion idle;
    private       Body          body;

    private boolean isGrounded;

    public Player(TextureAtlas atlas, World world) {
        this.idle = Objects.requireNonNull(atlas.findRegion("player-idle"));
        setWidth(1);
        setHeight(1);
        makePhysicsBody(world);
    }

    private void makePhysicsBody(World world) {
        final BodyDef bdef = new BodyDef();

        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);
        body.setFixedRotation(true);

        createBodyFixture();
        createGroundedSensorFixture();
    }

    private void createBodyFixture() {
        final PolygonShape shape = new PolygonShape();
        final FixtureDef   fdef  = new FixtureDef();

        shape.setAsBox(getWidth() / 2, getHeight() / 2);
        fdef.shape = shape;
        fdef.friction = 5;
        fdef.filter.categoryBits = BIT_ENTITY;
        fdef.filter.maskBits = BIT_WORLD;
        body.createFixture(fdef);
        shape.dispose();
    }

    private void createGroundedSensorFixture() {
        final EdgeShape  shape = new EdgeShape();
        final FixtureDef fdef  = new FixtureDef();

        final float left   = getX() - getWidth() / 2 + 0.05f;
        final float bottom = getY() - getHeight() / 2 - 0.05f;
        final float right  = getX() + getWidth() / 2 - 0.05f;

        shape.set(left, bottom, right, bottom);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = BIT_ENTITY;
        fdef.filter.maskBits = BIT_WORLD;
        body.createFixture(fdef)
            .setUserData(
                    new PlayerFeetFUD(this));
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(idle, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        handleInput();
        syncPositions();
        System.out.println("isGrounded = " + isGrounded);
    }

    private void handleInput() {
        final Vector2 vel = body.getLinearVelocity();

        boolean movedX = false;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(MOVE_VEL_X, vel.y);
            movedX = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-MOVE_VEL_X, vel.y);
            movedX = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (isGrounded) {
                body.applyForce(0, JUMP_FORCE, body.getWorldCenter().x, body.getWorldCenter().y, true);
            }
        }
        if (!movedX) {
            body.setLinearVelocity(0, vel.y);
        }
    }

    private void syncPositions() {
        setX(body.getPosition().x - getWidth() / 2);
        setY(body.getPosition().y - getHeight() / 2);
    }

    public Body getBody() {
        return body;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }
}
