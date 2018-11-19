package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class EffectDrawer implements Disposable {

    private Array<PooledEffect> pooledEffects = new Array<>(PooledEffect.class);

    public void drawEffects(Batch batch) {
        for (int i = 0; i < pooledEffects.size; i++) {
            final PooledEffect e = pooledEffects.get(i);
            e.draw(batch, Gdx.graphics.getDeltaTime());
            if (e.isComplete()) {
                e.free();
                pooledEffects.removeIndex(i);
            }
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void add(PooledEffect e) {
        pooledEffects.add(e);
    }

    public boolean isEmpty() {
        assert pooledEffects.size >= 0;
        return pooledEffects.size == 0;
    }

    @Override
    public void dispose() {
        for (PooledEffect e : pooledEffects) {
            e.free();
            e.dispose();
        }
    }
}
