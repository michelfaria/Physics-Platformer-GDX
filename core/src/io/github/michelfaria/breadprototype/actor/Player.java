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
import io.github.michelfaria.breadprototype.fud.BlockFUD;
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD;

public class Player extends PhysicsActor {

    public static final float
            MOVE_VEL_X = 6f,
            JUMP_FORCE = 140f,
            KICK_RANGE = 0.5f;

    private TextureRegion idleTexture;

    private float facing = 1; // 1=right, -1=left
    private int grounded = 0;
    private boolean kickButtonPressed;

    public Player(World world, TextureAtlas atlas) {
        super(world);
        idleTexture = atlas.findRegion(TextureRegionNames.PLAYER_IDLE);
        setWidth(1f);
        setHeight(1f);
    }

    @Override
    public void init() {
        super.init();
        initBody();
        initBodyFixture();
        initGroundedSensorFixture();
    }

    protected void initBody() {
        final BodyDef b = new BodyDef();
        b.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(b);
        this.body.setFixedRotation(true);
    }

    protected void initBodyFixture() {
        final PolygonShape s = new PolygonShape();
        s.setAsBox(getWidth() / 2, getHeight() / 2);
        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.friction = 0;
        f.filter.categoryBits = Bits.BIT_ENTITY;
        this.body.createFixture(f);
        s.dispose();
    }

    protected void initGroundedSensorFixture() {
        final float left = getX() - getWidth() / 2 + 0.05f;
        final float bottom = getY() - getHeight() / 2 - 0.05f;
        final float right = getX() + getWidth() / 2 - 0.05f;

        final EdgeShape s = new EdgeShape();
        s.set(left, bottom, right, bottom);

        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.isSensor = true;
        f.filter.categoryBits = Bits.BIT_ENTITY;
        f.filter.maskBits = Bits.BIT_SOLID;

        this.body.createFixture(f).setUserData(new PlayerFeetFUD(this));
        s.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
        final Vector2 vel = body.getLinearVelocity();
        boolean movedX = false;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(MOVE_VEL_X, vel.y);
            movedX = true;
            facing = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-MOVE_VEL_X, vel.y);
            movedX = true;
            facing = -1;
        }
        if (!movedX) {
            body.setLinearVelocity(0, vel.y);
        }
    }

    private void handleInputJump() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && isGrounded()) {
            body.applyForce(0, JUMP_FORCE, body.getWorldCenter().x, body.getWorldCenter().y, true);
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

    private void kick() {
        System.out.println("Player.kick");
        world.QueryAABB(f -> {
                    if (f.getUserData() instanceof BlockFUD) {
                        BlockFUD fud = (BlockFUD) f.getUserData();
                        System.out.println("fud = " + fud);
                        return true;
                    }
                    return false;
                },
                getX() + getWidth() / 2,
                getY(),
                getX() + getWidth() / 2 + getWidth(),
                getY() + getHeight());
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
}
