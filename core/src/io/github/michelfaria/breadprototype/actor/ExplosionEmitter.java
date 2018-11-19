package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import io.github.michelfaria.breadprototype.strategy.EffectDrawer;

public class ExplosionEmitter extends Actor implements Disposable {

    private final EffectDrawer effectDrawer = new EffectDrawer();
    private final ParticleEffectPool explosionSmokePEP;
    private final ParticleEffectPool novaPEP;

    public ExplosionEmitter(ParticleEffectPool explosionSmokePEP, ParticleEffectPool novaPEP) {
        this.explosionSmokePEP = explosionSmokePEP;
        this.novaPEP = novaPEP;
    }

    public void init() {
        addSmokeEffect();
        addNovaEffect();
    }

    protected void addSmokeEffect() {
        final ParticleEffectPool.PooledEffect e = explosionSmokePEP.obtain();
        e.setPosition(getX(), getY());
        effectDrawer.add(e);
        e.start();
    }

    protected void addNovaEffect() {
        final ParticleEffectPool.PooledEffect e = novaPEP.obtain();
        e.setPosition(getX(), getY());
        effectDrawer.add(e);
        e.start();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (effectDrawer.isEmpty()) {
            dispose();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        effectDrawer.drawEffects(batch);
    }

    @Override
    public void dispose() {
        remove();
        effectDrawer.dispose();
    }

    public static class ExplosionEmitterFactory {
        private final ParticleEffectPool explosionSmokePEP;
        private final ParticleEffectPool novaPEP;

        public ExplosionEmitterFactory(ParticleEffectPool explosionSmokePEP, ParticleEffectPool novaPEP) {
            this.explosionSmokePEP = explosionSmokePEP;
            this.novaPEP = novaPEP;
        }

        public ExplosionEmitter make() {
            return new ExplosionEmitter(explosionSmokePEP, novaPEP);
        }
    }
}