package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import io.github.michelfaria.breadprototype.fud.FUD;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class FixtureUtil {
    private FixtureUtil() {
    }

    /**
     * Returns a Fixture Pair for a Box2d Contact c.
     */
    public static @NotNull Pair<Fixture> getFixturePair(Contact c) {
        return new Pair<>(c.getFixtureA(), c.getFixtureB());
    }

    /**
     * Returns a FUD pair for a pair of Fixtures p.
     */
    public static @NotNull Pair<FUD> getFUDPair(Pair<Fixture> p) {
        return new Pair<>(getFUD(p.a), getFUD(p.b));
    }

    /**
     * @return The FUD for q. May be null if q has no FUD.
     * @throws RuntimeException If q's UserData cannot be casted to FUD.
     */
    public static @Nullable FUD getFUD(@NotNull Fixture q) {
        try {
            return (FUD) q.getUserData();
        }
        catch (ClassCastException e) {
            throw new RuntimeException("Fixture UserData could not be casted to FUD!", e);
        }
    }

    /**
     * Returns (p.a, p.b) or (p.b, p.a) if the Fixtures' FUD's classes in pair p exclusively are c1 and c2 in any order.
     * <p>
     * Returns (p.a, p.b) if p.a's FUD is c1 and p.b's is c2.
     * Returns (p.b, p.a) if p.b's FUD is c1 and p.a's is c2.
     * Returns null       otherwise.
     */
    public static <T, R> @Nullable Pair<Fixture> getFixturePairMatchByClass(Pair<Fixture> p, Class<T> c1, Class<R> c2) {
        return getPairMatch(p,
                f -> c1.isInstance(getFUD(f)),
                f -> c2.isInstance(getFUD(f)));
    }

    /**
     * Returns (a, b) or (b, a) if a and b in p exclusively pass t1 and t2 in any order.
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
