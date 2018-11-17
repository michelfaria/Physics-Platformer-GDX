package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import io.github.michelfaria.breadprototype.logic.Positionable;

public class PhysicsActor extends Actor implements Positionable, Disposable {

    protected final World world;

    protected Body body;

    private boolean isDisposed;

    public PhysicsActor(World world) {
        this.world = world;
    }

    public void init() {
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        syncWithBody();
    }

    protected void drawTextureAtBody(Batch batch, TextureRegion region) {
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private void syncWithBody() {
        setX(body.getPosition().x - getWidth() / 2);
        setY(body.getPosition().y - getHeight() / 2);
        setOriginX(getWidth() / 2);
        setOriginY(getHeight() / 2);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        body.setTransform(x, y, body.getAngle());
        syncWithBody();
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            throw new IllegalStateException("Tried to dispose a "
                    + PhysicsActor.class.getName() + " but it was already disposed.");
        }
        remove();
        world.destroyBody(body);
        body = null;
        isDisposed = true;
    }

    public void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public boolean isDisposed() {
        return isDisposed;
    }
}
