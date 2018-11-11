package io.github.michelfaria.breadprototype

import com.badlogic.gdx.physics.box2d.*
import io.github.michelfaria.breadprototype.fud.PlayerFeetFUD
import io.github.michelfaria.breadprototype.fud.SolidFUD
import io.github.michelfaria.breadprototype.util.FixtureUtil
import io.github.michelfaria.breadprototype.util.FixtureUtil.getFixturePair
import io.github.michelfaria.breadprototype.util.FixtureUtil.getFixturePairMatchByClass
import io.github.michelfaria.breadprototype.util.Pair

class MyContactListener : ContactListener {

    override fun beginContact(contact: Contact) {
        val fixtures = getFixturePair(contact)
        doGroundedCheck(fixtures, true)
    }

    override fun endContact(contact: Contact) {
        val fixtures = getFixturePair(contact)
        doGroundedCheck(fixtures, false)
    }

    private fun doGroundedCheck(fixtures: Pair<Fixture>, b: Boolean) {
        val match = getFixturePairMatchByClass(fixtures, PlayerFeetFUD::class.java, SolidFUD::class.java)
        if (match != null) {
            val fud = FixtureUtil.getFUD(match.a) as PlayerFeetFUD
            fud.player.isGrounded = b
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }
}
