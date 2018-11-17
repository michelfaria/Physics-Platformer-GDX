package io.github.michelfaria.breadprototype.fud;

import com.badlogic.gdx.physics.box2d.Fixture;
import io.github.michelfaria.breadprototype.actor.WandProjectile;

public class WandProjectileUD implements UD {

    private WandProjectile wandProjectile;

    public WandProjectileUD(WandProjectile wandProjectile) {
        this.wandProjectile = wandProjectile;
    }

    public void touched(Fixture other) {
        wandProjectile.touched(other);
    }

    public boolean isDisposed() {
        return wandProjectile.isDisposed();
    }
}