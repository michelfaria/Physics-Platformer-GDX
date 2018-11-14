package io.github.michelfaria.breadprototype.fud

import io.github.michelfaria.breadprototype.actor.WandProjectile
import java.lang.IllegalStateException

class WandProjectileFUD(private val wandProjectile: WandProjectile) : FUD {

    var isProjectileDead = false
        private set

    fun killProjectile() {
        if (isProjectileDead) {
            throw IllegalStateException("Already dead")
        }
        wandProjectile.kill()
        isProjectileDead = true
    }
}