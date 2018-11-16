package io.github.michelfaria.breadprototype.fud

import io.github.michelfaria.breadprototype.actor.WandProjectile

class WandProjectileFUD(private val wandProjectile: WandProjectile) : FUD {

    var isProjectileAlive = true
        private set

    fun killProjectile() {
        if (!isProjectileAlive) {
            throw IllegalStateException("Already dead")
        }
        wandProjectile.dispose()
        isProjectileAlive = false
    }
}