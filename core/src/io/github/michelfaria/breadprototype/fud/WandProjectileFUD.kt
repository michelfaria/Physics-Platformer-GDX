package io.github.michelfaria.breadprototype.fud

import io.github.michelfaria.breadprototype.actor.WandProjectile

class WandProjectileFUD(private val wandProjectile: WandProjectile) : FUD {
    fun killProjectile() {
        wandProjectile.remove()
    }
}