package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.michelfaria.breadprototype.Bits;
import io.github.michelfaria.breadprototype.TextureRegionNames;
import io.github.michelfaria.breadprototype.fud.BlockUD;
import io.github.michelfaria.breadprototype.fud.PlayerBodyUD;
import io.github.michelfaria.breadprototype.fud.PlayerFeetUD;
import io.github.michelfaria.breadprototype.util.Pair;

public class Player extends PhysicsActor {

    public static final float
            MOVE_FORCE_X = 400,
            SPEED_LIMIT_X = 5,
            JUMP_FORCE = 2_000,
            KICK_FORCE = 40_000;

    private TextureRegion idleTexture;

    private float facing = 1; // 1=right, -1=left
    private int grounded = 0;
    private boolean kickButtonPressed;

    public Player(World world, TextureAtlas atlas) {
        super(world);
        idleTexture = atlas.findRegion(TextureRegionNames.PLAYER_IDLE);
        setWidth(2);
        setHeight(2);
    }

    @Override
    public void init() {
        super.init();
        initBody();
        initBodyFixtures();
        initGroundedSensorFixture();
    }

    protected void initBody() {
        final BodyDef b = new BodyDef();
        b.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(b);
        this.body.setFixedRotation(true);
    }

    protected void initBodyFixtures() {
        final PlayerBodyUD userData = new PlayerBodyUD();
        final PolygonShape s = new PolygonShape();
        s.setAsBox(bodyFixtureWidth(), bodyFixtureHeight());

        final FixtureDef f = new FixtureDef();
        // main fixture
        f.shape = s;
        f.friction = 5;
        f.density = 7;
        f.filter.categoryBits = Bits.BIT_ENTITY;
        this.body.createFixture(f).setUserData(userData);

        // 0-friction left side to prevent sticking to walls
        s.setAsBox(0.05f, bodyFixtureHeight() - 0.01f, new Vector2(getX() - bodyFixtureWidth(), getY()), 0);
        f.friction = 0;
        f.density = 0;
        this.body.createFixture(f).setUserData(userData);

        // 0-friction right side to prevent sticking to walls
        s.setAsBox(0.05f, bodyFixtureHeight() - 0.01f, new Vector2(getX() + bodyFixtureWidth(), getY()), 0);
        f.friction = 0;
        f.density = 0;
        this.body.createFixture(f).setUserData(userData);

        s.dispose();
    }

    protected void initGroundedSensorFixture() {
        final float left = getX() - bodyFixtureWidth() + 0.05f;
        final float bottom = getY() - bodyFixtureHeight() - 0.05f;
        final float right = getX() + bodyFixtureWidth() - 0.05f;

        final EdgeShape s = new EdgeShape();
        s.set(left, bottom, right, bottom);

        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.isSensor = true;
        f.filter.categoryBits = Bits.BIT_ENTITY;
        f.filter.maskBits = Bits.BIT_SOLID;

        this.body.createFixture(f).setUserData(new PlayerFeetUD(this));
        s.dispose();
    }

    private float bodyFixtureWidth() {
        return getWidth() / 4;
    }

    private float bodyFixtureHeight() {
        return getHeight() / 2;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (facing > 0 && idleTexture.isFlipX() || facing < 0 && !idleTexture.isFlipX()) {
            idleTexture.flip(true, false);
        }
        drawTextureAtBody(batch, idleTexture);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        assert facing != 0;
        handleInput();
    }

    private void handleInput() {
        handleInputMovement();
        handleInputJump();
        handleInputKick();
    }

    private void handleInputMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (body.getLinearVelocity().x < SPEED_LIMIT_X) {
                body.applyForceToCenter(MOVE_FORCE_X, 0, true);
            }
            facing = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (body.getLinearVelocity().x > -SPEED_LIMIT_X) {
                body.applyForceToCenter(-MOVE_FORCE_X, 0, true);
            }
            facing = -1;
        }
    }

    private void handleInputJump() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && isGrounded()) {
            body.applyForceToCenter(0, JUMP_FORCE, true);
        }
    }

    private void handleInputKick() {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            if (!kickButtonPressed) {
                kickButtonPressed = true;
                kick();
            }
        } else {
            kickButtonPressed = false;
        }
    }

    /**
     * Query the kick AABB and then kick any blocks in it
     */
    private void kick() {
        final Pair<Vector2> kickArea = getKickArea();
        world.QueryAABB(f -> {
            if (f.getUserData() instanceof BlockUD) {
                kick(f);
            }
            return true;
        }, kickArea.a.x, kickArea.a.y, kickArea.b.x, kickArea.b.y);
    }

    /**
     * Kicks the specified fixture
     */
    private void kick(Fixture f) {
        final Body body = f.getBody();
        body.applyForce(KICK_FORCE * facing, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
    }

    public Pair<Vector2> getKickArea() {
        final float x = getX() + getWidth() / 4 * facing;
        final float y = getY() + getHeight() / 10;
        return new Pair<>(new Vector2(x, y), new Vector2(x + getWidth(), y + getHeight() / 2));
    }

    public void incrementGrounded() {
        this.grounded++;
    }

    public void decrementGrounded() {
        if (grounded == 0) {
            throw new IllegalStateException("Tried to decrement grounded from 0");
        }
        this.grounded--;
    }

    public boolean isGrounded() {
        return grounded > 0;
    }

    public float getFacing() {
        return facing;
    }
}
