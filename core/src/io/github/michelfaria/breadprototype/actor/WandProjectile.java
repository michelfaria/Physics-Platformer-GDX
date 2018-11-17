package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.physics.box2d.*;
import io.github.michelfaria.breadprototype.Bits;
import io.github.michelfaria.breadprototype.fud.TNTBlockUD;
import io.github.michelfaria.breadprototype.fud.WandProjectileUD;
import io.github.michelfaria.breadprototype.strategy.BlockSpawner;

public class WandProjectile extends PhysicsActor {

    private static final float SPEED = 15;
    private static final float LIFESPAN_SECS = 2;

    private final BlockSpawner blockSpawner;
    private final ParticleEffectPool wandParticleEffectPool;
    private float destX;
    private float destY;

    private final ParticleEffectPool.PooledEffect effect;

    public WandProjectile(World world, BlockSpawner blockSpawner, ParticleEffectPool wandParticleEffectPool, float destX, float destY) {
        super(world);
        this.blockSpawner = blockSpawner;
        this.wandParticleEffectPool = wandParticleEffectPool;
        this.destX = destX;
        this.destY = destY;
        setWidth(0.5f);
        setHeight(0.5f);
        this.effect = newParticleEffect();
    }

    @Override
    public void init() {
        super.init();
        initPhysicsBody();
    }

    private void initPhysicsBody() {
        final BodyDef b = new BodyDef();
        b.type = BodyDef.BodyType.DynamicBody;

        final CircleShape s = new CircleShape();
        s.setRadius(0.25f);

        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.filter.categoryBits = Bits.BIT_PROJECTILE;
        f.filter.maskBits = Bits.BIT_SOLID;

        this.body = world.createBody(b);
        this.body.createFixture(f).setUserData(new WandProjectileUD(this));
        s.dispose();
    }

    private ParticleEffectPool.PooledEffect newParticleEffect() {
        final ParticleEffectPool.PooledEffect e = wandParticleEffectPool.obtain();
        e.start();
        return e;
    }

    public void goToDestination() {
        double angle = Math.atan2(
                destY - getY() - getOriginY(),
                destX - getX() - getOriginX());
        body.setLinearVelocity(
                (float) Math.cos(angle) * SPEED,
                (float) Math.sin(angle) * SPEED);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        drawParticles(batch);
    }

    private void drawParticles(Batch batch) {
        effect.draw(batch, Gdx.graphics.getDeltaTime());
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX() + getOriginX(), getY() + getOriginY());
    }

    public void touched(Fixture other) {
        if (other.getUserData() instanceof TNTBlockUD) {
            ((TNTBlockUD) other.getUserData()).trigger();
        } else {
            blockSpawner.spawnBlock(body.getPosition().x, body.getPosition().y);
        }
        dispose();
    }

    @Override
    public void dispose() {
        effect.dispose();
        super.dispose();
    }
}
