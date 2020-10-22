package chopchop.model.attributes;

import java.util.List;
import java.util.function.BiFunction;

import chopchop.commons.util.Result;
import chopchop.commons.util.StringView;
import chopchop.model.attributes.units.Mass;
import chopchop.model.attributes.units.Count;
import chopchop.model.attributes.units.Volume;

/**
 * The Quantity interface is an abstraction over various kinds of units, including but not limited to:
 * mass, volume, and dimensionless counts.
 *
 * It has no knowledge of how each unit interacts and fits with the others; it simply provides a static
 * method that helps to parse an input, eg. "300 ml" into a {@code Quantity}. Note that the concept of
 * "units" is different from the idea of "prefixes"; eg. 'kg' and 'g' are conceptually the same unit of
 * mass, but they have different prefixes (1000 and 1), so it must be possible to add them together.
 *
 * Each class implementing Quantity is expected to be immutable. "Type" safety is expected to be enforced
 * by the implementation, unless it makes sense to add units of different types together (unlikely).
 *
 * There is no restriction on the actual value of the quantity, ie. they are allowed to be negative.
 *
 * Implementing classes should be responsible for implementing {@code add()} functionality between
 * different ratios (prefixes) of the same unit; eg. it should be possible to add 700g to 2kg to
 * obtain 2.7kg.
 */
public interface Quantity extends Comparable<Quantity> {
    /**
     * Adds a quantity to this, and returns a new quantity (without modifying the original).
     * The input quantity can be negative to perform a subtraction. If the units are incompatible,
     * it returns an appropriate error message.
     *
     * @param qty the addend
     * @return    a new Quantity after performing the addition.
     */
    Result<? extends Quantity> add(Quantity qty);

    /**
     * Convenience function to subtract a quantity from this.
     *
     * @param qty the minuend
     * @return    a new Quantity after performing the subtraction.
     * @see #add(Quantity)
     */
    default Result<? extends Quantity> subtract(Quantity qty) {
        return this.add(qty.negate());
    }

    /**
     * Negates this quantity, primarily used for subtracting quantities.
     *
     * @return a new Quantity representing the negation of this quantity.
     */
    Quantity negate();

    /**
     * Returns the value of this quantity.
     *
     * @return the double value of this quantity.
     */
    double getValue();

    /**
     * Parse a quantity and its associated unit.
     *
     * @param input the string input
     * @return      the parsed input, or an error message.
     */
    static Result<Quantity> parse(String input) {
        final List<BiFunction<Double, String, Result<Quantity>>> knownUnits = List.of(
            Mass::of, Volume::of, Count::of
        );

        if (input.isEmpty()) {
            return Result.error("empty input");
        }

        // this is a bit iffy, but this condition will accept things like "-31.4-48.145.201-4".
        // it's up to parseDouble() to return us an intelligible error message from that.
        var p = new StringView(input).span(c -> Character.isDigit(c) || c == '.' || c == '-');
        var num = p.fst().trim().parseDouble();

        // do-notation would be really nice here.
        if (num.isError()) {
            return Result.error("couldn't parse number: %s", num.getError());
        }

        var unit = p.snd().trim();

        // this loops through each known unit constructor, and returns the first one
        // that gives a non-error result. this way, the knowledge of unit names are
        // not duplicated here and in the actual implementation.
        return Result.flatten(
            Result.ofOptional(
                knownUnits.stream()
                    .map(fn -> num.then(n -> fn.apply(n, unit.toString())))
                    .filter(Result::hasValue)
                    .findFirst(),
                String.format("unknown unit '%s'", unit))
            );
    }

    /**
     * Formats a decimal value in an intelligent manner; mainly by not showing the decimal places if
     * the input is a whole number.
     *
     * @param value the value to format
     * @return      the formatted value as a string
     */
    static String formatDecimalValue(double value) {
        if (value == (int) value) {
            return String.format("%d", (int) value);
        } else {
            return String.format("%.3f", value);
        }
    }
}
