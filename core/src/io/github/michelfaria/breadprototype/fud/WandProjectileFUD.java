package io.github.michelfaria.breadprototype.fud;

import io.github.michelfaria.breadprototype.actor.WandProjectile;
import org.jetbrains.annotations.NotNull;

public class WandProjectileFUD implements FUD {

    private @NotNull WandProjectile wandProjectile;

    private boolean isProjectileAlive = true;

    public WandProjectileFUD(@NotNull WandProjectile wandProjectile) {
        this.wandProjectile = wandProjectile;
    }

    public void killProjectile() {
        if (!isProjectileAlive) {
            throw new IllegalStateException("Already dead");
        }
        wandProjectile.dispose();
        isProjectileAlive = false;
    }

    public boolean isProjectileAlive() {
        return isProjectileAlive;
    }
}