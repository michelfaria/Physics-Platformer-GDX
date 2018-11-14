package io.github.michelfaria.breadprototype

import com.badlogic.gdx.physics.box2d.*
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD
import io.github.michelfaria.breadprototype.fud.SolidFUD
import io.github.michelfaria.breadprototype.fud.WandProjectileFUD
import io.github.michelfaria.breadprototype.strategy.BlockSpawner
import io.github.michelfaria.breadprototype.strategy.TodoListAppender
import io.github.michelfaria.breadprototype.util.FixtureUtil.getFUD
import io.github.michelfaria.breadprototype.util.FixtureUtil.getFixturePair
import io.github.michelfaria.breadprototype.util.FixtureUtil.getFixturePairMatchByClass
import io.github.michelfaria.breadprototype.util.Pair

class MyContactListener(private val blockSpawner: BlockSpawner,
                        private val todoListAppender: TodoListAppender) : ContactListener {

    override fun beginContact(contact: Contact) {
        val fixtures = getFixturePair(contact)
        playerGrounded(fixtures, true)
        wandParticleCollision(fixtures)
    }

    override fun endContact(contact: Contact) {
        val fixtures = getFixturePair(contact)
        playerGrounded(fixtures, false)
    }

    private fun playerGrounded(fixtures: Pair<Fixture>, b: Boolean) {
        val match = getFixturePairMatchByClass(fixtures, PlayerFeetFUD::class.java, SolidFUD::class.java)
        if (match != null) {
            val fud = getFUD(match.a) as PlayerFeetFUD
            fud.player.isGrounded = b
        }
    }

    private fun wandParticleCollision(fixtures: Pair<Fixture>) {
        val match = getFixturePairMatchByClass(fixtures, WandProjectileFUD::class.java, SolidFUD::class.java)
        if (match != null) {
            val fud = getFUD(match.a) as WandProjectileFUD
            todoListAppender.addUpdate {
                if (!fud.isProjectileDead) {
                    fud.killProjectile()
                    blockSpawner.spawnBlock(match.a.body.position.x, match.a.body.position.y)
                }
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }
}
