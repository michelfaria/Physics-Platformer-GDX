package io.github.michelfaria.breadprototype.util

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import io.github.michelfaria.breadprototype.fud.FUD

object FixtureUtil {

    /**
     * Returns a Fixture Pair for a Box2d Contact c.
     */
    fun getFixturePair(c: Contact): Pair<Fixture> {
        return Pair(c.fixtureA, c.fixtureB)
    }

    /**
     * Returns a FUD pair for a pair of Fixtures p.
     */
    fun getFUDPair(p: Pair<Fixture>): Pair<FUD> {
        return Pair(getFUD(p.a)!!, getFUD(p.b)!!)
    }

    /**
     * @return The FUD for q. May be null if q has no FUD.
     * @throws RuntimeException If q's UserData cannot be casted to FUD.
     */
    fun getFUD(q: Fixture): FUD? {
        try {
            return q.userData as FUD
        } catch (e: ClassCastException) {
            if (q.userData == null) {
                return null
            }
            throw RuntimeException("Fixture's UserData could not be casted to FUD!", e)
        }
    }

    /**
     * Returns (p.a, p.b) or (p.b, p.a) if the Fixtures' FUD's classes in pair p exclusively are c1 and c2 in any order.
     *
     *
     * Returns (p.a, p.b) if p.a's FUD is c1 and p.b's is c2.
     * Returns (p.b, p.a) if p.b's FUD is c1 and p.a's is c2.
     * Returns null       otherwise.
     */
    fun <T, R> getFixturePairMatchByClass(p: Pair<Fixture>, c1: Class<T>, c2: Class<R>): Pair<Fixture>? {
        return getPairMatch(p,
                { f: Fixture -> c1.isInstance(getFUD(f)) },
                { f: Fixture -> c2.isInstance(getFUD(f)) })
    }

    /**
     * Returns (a, b) or (b, a) if a and b in p exclusively pass t1 and t2 in any order.
     *
     *
     * Returns (a, b) if a passes t1 and b passes t2.
     * Returns (b, a) if b passes t1 and a passes t2.
     * Returns null otherwise.
     */
    private fun <T> getPairMatch(p: Pair<T>, t1: (T) -> Boolean, t2: (T) -> Boolean): Pair<T>? {
        if (t1(p.a) && t2(p.b)) {
            return Pair(p.a, p.b)
        }
        return if (t2(p.a) && t1(p.b)) {
            Pair(p.b, p.a)
        } else null
    }
}
