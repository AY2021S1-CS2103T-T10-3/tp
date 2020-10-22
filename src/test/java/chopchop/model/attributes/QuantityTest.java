// QuantityTest.java

package chopchop.model.attributes;

import java.util.Map;

import chopchop.commons.util.Pair;
import chopchop.commons.util.Result;
import chopchop.model.attributes.units.Mass;
import chopchop.model.attributes.units.Count;
import chopchop.model.attributes.units.Volume;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class QuantityTest {

    @Test
    void equals_sameUnits_success() {

        assertEquals(Mass.grams(133),       Mass.kilograms(0.133));
        assertEquals(Mass.kilograms(23),    Mass.grams(23000));

        assertEquals(Volume.cups(3.2),      Volume.millilitres(800));
        assertEquals(Volume.teaspoons(3),   Volume.litres(0.015));

        assertEquals(Count.of(3),           Count.of(3));
    }

    @Test
    void equals_notSameUnits_failure() {
        assertNotEquals(Mass.grams(133),        Mass.kilograms(133));
        assertNotEquals(Mass.kilograms(23),     Mass.grams(23));

        assertNotEquals(Volume.cups(3.2),       Volume.millilitres(3.2));
        assertNotEquals(Volume.teaspoons(3),    Volume.litres(2));

        assertNotEquals(Count.of(3),            Count.of(1));

        assertNotEquals(Mass.grams(133),        Count.of(133));
        assertNotEquals(Mass.kilograms(23),     Volume.tablespoons(23));
    }

    @Test
    void parse_validUnits_success() {

        var tests = Map.of(
            "10 g",     Mass.grams(10),
            "3.1kg",    Mass.grams(3100),
            "60kg",     Mass.grams(60000),

            "40ml",     Volume.millilitres(40),
            "3.7tbsp",  Volume.tablespoons(3.7),
            "1 cup",    Volume.teaspoons(50),
            "2 cups",   Volume.litres(0.5),

            "3",        Count.of(3)
        );

        tests.forEach((k, v) -> {
            var parsed = Quantity.parse(k);

            assertTrue(parsed.hasValue());
            assertEquals(v, parsed.getValue());
        });
    }

    @Test
    void parse_invalidUnits_failure() {
        var tests = Map.of(
            "10 lg",        "unknown unit 'lg'",
            "3.1.4 cups",   "couldn't parse number: multiple points",
            "",             "empty input"
        );

        tests.forEach((k, v) -> {
            assertEquals(Result.error(v), Quantity.parse(k));
        });
    }

    @Test
    void add_compatibleUnits_success() {
        var tests = Map.of(
            Pair.of("185 g", "3.7 kg"),     Mass.kilograms(3.885),
            Pair.of("185 g", "-53g"),       Mass.grams(132),
            Pair.of("45ml", "3tsp"),        Volume.tablespoons(4),
            Pair.of("3 cups", "1 cup"),     Volume.litres(1),
            Pair.of("1", "7"),              Count.of(8)
        );

        tests.forEach((k, v) -> {
            var a = k.fst();
            var b = k.snd();
            assertEquals(Result.of(v), Quantity.parse(a)
                .then(x -> Quantity.parse(b).then(y -> x.add(y)))
            );
        });
    }

    @Test
    void add_incompatibleUnits_failure() {
        var tests = Map.of(
            Pair.of("185 g", "3.7 ml"),     "cannot add '3.700ml' to '185g' (incompatible units)",
            Pair.of("1", "7g"),             "cannot add '7g' to '1' (incompatible units)"
        );

        tests.forEach((k, v) -> {
            var a = k.fst();
            var b = k.snd();
            assertEquals(Result.error(v), Quantity.parse(a)
                .then(x -> Quantity.parse(b).then(y -> x.add(y)))
            );
        });
    }
}
