package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import io.github.michelfaria.breadprototype.Bits;
import io.github.michelfaria.breadprototype.fud.BlockUD;
import io.github.michelfaria.breadprototype.fud.PlainBlockUD;
import io.github.michelfaria.breadprototype.strategy.EffectDrawer;

public abstract class Block extends PhysicsActor {

    private final ParticleEffectPool blockCreationEffectPool;
    private final EffectDrawer effectDrawer = new EffectDrawer();
    private final TextureRegion textureRegion;


    public Block(World world, ParticleEffectPool blockCreationEffectPool, TextureRegion textureRegion) {
        super(world);
        this.blockCreationEffectPool = blockCreationEffectPool;
        this.textureRegion = textureRegion;
        setWidth(1);
        setHeight(1);
        setTouchable(Touchable.enabled);
    }

    @Override
    public void init() {
        super.init();
        initPhysicsBody();
        initBodyFixture();
    }

    protected void initPhysicsBody() {
        final BodyDef b = new BodyDef();
        b.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(b);
    }

    protected void initBodyFixture() {
        final PolygonShape s = new PolygonShape();
        s.setAsBox(getWidth() / 2, getHeight() / 2);

        final FixtureDef f = new FixtureDef();
        f.shape = s;
        f.filter.categoryBits = (short) (Bits.BIT_SOLID | Bits.BIT_ENTITY);
        f.density = 70;

        this.body.createFixture(f).setUserData(newUserData());
        s.dispose();
    }

    protected BlockUD newUserData() {
        return new PlainBlockUD();
    }

    public void addNewCreationEffect() {
        final ParticleEffectPool.PooledEffect e = blockCreationEffectPool.obtain();
        e.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        effectDrawer.add(e);
        e.start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (textureRegion == null) {
            throw new IllegalStateException("Texture region not set");
        }
        drawTextureAtBody(batch, textureRegion);
        effectDrawer.drawEffects(batch);
    }
}
