package io.github.michelfaria.breadprototype.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import io.github.michelfaria.breadprototype.fud.UD;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static io.github.michelfaria.breadprototype.util.Util._n;

public class FixtureUtil {

    public static Pair<Fixture> getFixturePair(Contact c) {
        return new Pair<>(c.getFixtureA(), c.getFixtureB());
    }

    public static @Nullable UD getFUD(Fixture f) {
        try {
            return (UD) f.getUserData();
        } catch (ClassCastException ex) {
            if (f.getUserData() == null) {
                return null;
            }
            throw new RuntimeException("Fixture's UserData could not be casted to UD!", ex);
        }
    }

    /**
     * @return The UD for q. May be null if q has no UD.
     * @throws RuntimeException If q's UserData cannot be casted to UD.
     */
    public static Pair<UD> getFUDPair(Pair<Fixture> p) {
        return new Pair<>(Util._n(getFUD(p.a)), Util._n(getFUD(p.b)));
    }

    /**
     * Returns (p.a, p.b) or (p.b, p.a) if the Fixtures' UD's classes in pair p exclusively are c1 and c2 in any order.
     * <p>
     * <p>
     * Returns (p.a, p.b) if p.a's UD is c1 and p.b's is c2.
     * Returns (p.b, p.a) if p.b's UD is c1 and p.a's is c2.
     * Returns null       otherwise.
     */
    public static <T, R> @Nullable Pair<Fixture> getFixturePairMatchByClass(
            Pair<Fixture> p, Class<T> c1, Class<R> c2) {
        return getPairMatch(p,
                f -> c1.isInstance(getFUD(f)),
                f -> c2.isInstance(getFUD(f)));
    }

    /**
     * Returns (a, b) or (b, a) if a and b in p exclusively pass t1 and t2 in any order.
     * <p>
     * <p>
     * Returns (a, b) if a passes t1 and b passes t2.
     * Returns (b, a) if b passes t1 and a passes t2.
     * Returns null otherwise.
     */
    public static <T> @Nullable Pair<T> getPairMatch(Pair<T> p, Predicate<T> t1, Predicate<T> t2) {
        if (t1.test(p.a) && t2.test(p.b)) {
            return new Pair<>(p.a, p.b);
        }
        if (t2.test(p.a) && t1.test(p.b)) {
            return new Pair<>(p.b, p.a);
        }
        return null;
    }
}
