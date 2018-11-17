package io.github.michelfaria.breadprototype.strategy;


import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.michelfaria.breadprototype.actor.WandProjectile;

public class WandProjectileSpawner {
    private final Stage stage;
    private final World world;
    private final BlockSpawner blockSpawner;
    private final ParticleEffectPool wandProjectileEffectPool;

    public WandProjectileSpawner(Stage stage, World world, BlockSpawner blockSpawner, ParticleEffectPool wandProjectileEffectPool) {
        this.stage = stage;
        this.world = world;
        this.blockSpawner = blockSpawner;
        this.wandProjectileEffectPool = wandProjectileEffectPool;
    }

    public WandProjectile spawn(float x, float y, float destX, float destY) {
        final WandProjectile p = new WandProjectile(world, blockSpawner, wandProjectileEffectPool, destX, destY);
        p.init();
        stage.addActor(p);
        p.setPosition(x, y);
        p.goToDestination();
        return p;
    }
}
