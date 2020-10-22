package chopchop.model.attributes.units;

import chopchop.commons.util.Result;
import chopchop.model.attributes.Quantity;
import chopchop.model.exceptions.IncompatibleIngredientsException;

/**
 * This class represents a quantity of mass, eg. grams, kilograms, etc. For the purposes of this class,
 * a ratio of 1.0 represents the gram (because it is unlikely you will be cooking with kilograms of
 * ingredients).
 */
public class Mass implements Quantity {
    private final double value;
    private final double ratio;

    /**
     * Constructs a new mass quantity with the given value and ratio.
     */
    private Mass(double value, double ratio) {
        // adjust the ratio if we have to. don't want to show 0.0001kg for 100mg,
        // nor 15000g for 15kg. for now, the cutoff is 5; ie. if you have 5001g of something,
        // it'll show as 5.00kg. if you have 0.499kg, it'll show as 499g. of course, this
        // only works for SI units. if you decide to be a dunce and use customary or imperial
        // units, ownself settle.
        if (ratio == 1 || ratio == 0.001) {
            if (value > 5000) {
                ratio *= 1000;
                value /= 1000;
            }
        } else if (ratio == 1000) {
            if (value < 0.500) {
                ratio /= 1000;
                value *= 1000;
            }
        }

        this.value = value;
        this.ratio = ratio;
    }

    @Override
    public Result<Mass> add(Quantity qty) {
        if (!(qty instanceof Mass)) {
            return Result.error("cannot add '%s' to '%s' (incompatible units)", qty, this);
        } else {
            var mass = (Mass) qty;
            var newval = this.value + (mass.value * (mass.ratio / this.ratio));

            return Result.of(new Mass(newval, this.ratio));
        }
    }

    @Override
    public Mass negate() {
        return new Mass(-this.value, this.ratio);
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Quantity other) {
        if (!(other instanceof Mass)) {
            throw new IncompatibleIngredientsException(
                    String.format("cannot compare '%s' with '%s' (incompatible units)", other, this));
        }

        return Double.compare(this.value * this.ratio, ((Mass) other).value * ((Mass) other).ratio);
    }

    @Override
    public String toString() {
        var unit = "";
        if (this.ratio == 1) {
            unit = "g";
        } else if (this.ratio == 1000) {
            unit = "kg";
        } else if (this.ratio == 0.001) {
            unit = "mg";
        } else {
            unit = "?";
        }

        return String.format("%s%s", Quantity.formatDecimalValue(this.value), unit);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mass)) {
            return false;
        }

        var m = (Mass) obj;
        return (this.value * this.ratio) == (m.value * m.ratio);
    }

    /**
     * Returns representation of the mass.
     *
     * @param value the numerical value of the quantity
     * @param unit  the unit string, eg. "kg" or "g"
     * @return      the mass quantity, if the unit was valid.
     */
    public static Result<Quantity> of(double value, String unit) {
        switch (unit.toLowerCase()) {
        case "mg":  return Result.of(milligrams(value));
        case "g":   return Result.of(grams(value));
        case "kg":  return Result.of(kilograms(value));
        default:    return Result.error("invalid unit '%s'", unit);
        }
    }

    /**
     * Returns a mass in grams
     *
     * @param value the number of grams
     * @return      the quantity
     */
    public static Mass grams(double value) {
        return new Mass(value, 1.0);
    }

    /**
     * Returns a mass in milligrams
     *
     * @param value the number of milligrams
     * @return      the quantity
     */
    public static Mass milligrams(double value) {
        return new Mass(value, 0.001);
    }

    /**
     * Returns a mass in kilograms
     *
     * @param value the number of kilograms
     * @return      the quantity
     */
    public static Mass kilograms(double value) {
        return new Mass(value, 1000);
    }
}
