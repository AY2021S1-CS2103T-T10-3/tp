package chopchop.model.attributes.units;

import chopchop.commons.util.Result;
import chopchop.model.attributes.Quantity;
import chopchop.model.exceptions.IncompatibleIngredientsException;

public class Volume implements Quantity {
    private static double RATIO_LITRE       = 1.0;
    private static double RATIO_MILLILITRE  = 0.001;
    private static double RATIO_CUP         = 0.250;    // we're using the metric cup, for obvious reasons.
    private static double RATIO_TEASPOON    = 0.005;    // and metric tea and tablespoons.
    private static double RATIO_TABLESPOON  = 0.015;

    private static String UNIT_LITRE        = "l";
    private static String UNIT_MILLILITRE   = "ml";
    private static String UNIT_CUP          = "cup";
    private static String UNIT_CUPS         = "cups";   // this is for convenience.
    private static String UNIT_TEASPOON     = "tsp";
    private static String UNIT_TABLESPOON   = "tbsp";

    private final double value;
    private final double ratio;

    private Volume(double value, double ratio) {
        // see the comment in Mass.java for an explanation of this.
        if (ratio == 1 || ratio == 0.001) {
            if (value > 5000) {
                ratio *= 1000;
                value /= 1000;
            } else if (value < 1.0) {
                ratio /= 1000;
                value *= 1000;
            }
        }

        this.value = value;
        this.ratio = ratio;
    }

    @Override
    public Result<Volume> add(Quantity qty) {
        if (!(qty instanceof Volume)) {
            return Result.error("cannot add '%s' to '%s' (incompatible units)", qty, this);
        } else {
            var vol = (Volume) qty;
            var newval = this.value + (vol.value * (vol.ratio / this.ratio));

            return Result.of(new Volume(newval, this.ratio));
        }
    }

    @Override
    public Volume negate() {
        return new Volume(-this.value, this.ratio);
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Quantity other) {
        if (!(other instanceof Volume)) {
            throw new IncompatibleIngredientsException(
                    String.format("cannot compare '%s' with '%s' (incompatible units)", other, this));
        }

        return Double.compare(this.value * this.ratio, ((Volume) other).value * ((Volume) other).ratio);
    }

    @Override
    public String toString() {
        String unit = "";

        if (this.ratio == RATIO_MILLILITRE) {
            unit = UNIT_MILLILITRE;
        } else if (this.ratio == RATIO_LITRE) {
            unit = UNIT_LITRE;
        } else if (this.ratio == RATIO_CUP) {
            unit = (this.value == 1 ? UNIT_CUP : UNIT_CUPS);
        } else if (this.ratio == RATIO_TABLESPOON) {
            unit = UNIT_TABLESPOON;
        } else if (this.ratio == RATIO_TEASPOON) {
            unit = UNIT_TEASPOON;
        } else {
            unit = "?";
        }

        return String.format("%s%s", Quantity.formatDecimalValue(this.value), unit);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Volume)) {
            return false;
        }

        var v = (Volume) obj;
        return (this.value * this.ratio) == (v.value * v.ratio);
    }

    /**
     * Returns representation of the volume.
     *
     * @param value the numerical value of the quantity
     * @param unit  the unit string, eg. "l" or "cups"
     * @return      the volume quantity, if the unit was valid.
     */
    public static Result<Quantity> of(double value, String unit) {

        double ratio = 1;

        if (unit.equalsIgnoreCase(UNIT_MILLILITRE)) {
            ratio = RATIO_MILLILITRE;
        } else if (unit.equalsIgnoreCase(UNIT_LITRE)) {
            ratio = RATIO_LITRE;
        } else if (unit.equalsIgnoreCase(UNIT_CUP) || unit.equalsIgnoreCase(UNIT_CUPS)) {
            ratio = RATIO_CUP;
        } else if (unit.equalsIgnoreCase(UNIT_TABLESPOON)) {
            ratio = RATIO_TABLESPOON;
        } else if (unit.equalsIgnoreCase(UNIT_TEASPOON)) {
            ratio = RATIO_TEASPOON;
        } else {
            return Result.error("unknown unit '%s'", unit);
        }

        return Result.of(new Volume(value, ratio));
    }

    /**
     * Returns a volume in millilitres
     *
     * @param value the number of millilitres
     * @return      the quantity
     */
    public static Volume millilitres(double value) {
        return new Volume(value, RATIO_MILLILITRE);
    }

    /**
     * Returns a volume in litres
     *
     * @param value the number of litres
     * @return      the quantity
     */
    public static Volume litres(double value) {
        return new Volume(value, RATIO_LITRE);
    }

    /**
     * Returns a volume in (metric) cups -- 250ml.
     *
     * @param value the number of (metric) cups
     * @return      the quantity
     */
    public static Volume cups(double value) {
        return new Volume(value, RATIO_CUP);
    }

    /**
     * Returns a volume in (metric) teaspoons -- 5ml
     *
     * @param value the number of (metric) teaspoons
     * @return      the quantity
     */
    public static Volume teaspoons(double value) {
        return new Volume(value, RATIO_TEASPOON);
    }

    /**
     * Returns a volume in (metric) tablespoons -- 15ml
     *
     * @param value the number of (metric) tablespoons
     * @return      the quantity
     */
    public static Volume tablespoons(double value) {
        return new Volume(value, RATIO_TABLESPOON);
    }
}
