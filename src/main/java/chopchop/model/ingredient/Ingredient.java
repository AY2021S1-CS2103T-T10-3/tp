package chopchop.model.ingredient;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.Optional;
import java.util.Comparator;

import chopchop.model.Entry;
import chopchop.model.attributes.Quantity;
import chopchop.model.attributes.ExpiryDate;
import chopchop.model.attributes.Tag;
import chopchop.model.exceptions.IncompatibleIngredientsException;

import static java.util.Objects.requireNonNull;

/**
 * Represents an Ingredient in the recipe manager.
 */
public class Ingredient extends Entry {

    // comparator that compares expiry dates, and puts empty expiries at the end.
    public static final Comparator<Optional<ExpiryDate>> SET_COMPARATOR = (a, b) -> {

        if (a.isEmpty() && b.isEmpty()) {
            return 0;
        } else if (a.isEmpty()) {
            return 1;
        } else if (b.isEmpty()) {
            return -1;
        } else {
            return a.get().compareTo(b.get());
        }
    };

    private final TreeMap<Optional<ExpiryDate>, Quantity> sets;
    private final Set<Tag> tags;

    /**
     * Every field must be present and not null. Use this constructor if expiry date is not present.
     * Guarantees: details are present and not null, field values are validated, immutable.
     */
    public Ingredient(String name, Quantity quantity) {
        this(name, quantity, null, null);
    }

    /**
     * Every field(less tag) must be present and not null. If expiry date is not present, use other constructor.
     * Guarantees: details(less tag) are present and not null, field values are validated, immutable.
     */
    public Ingredient(String name, Quantity quantity, ExpiryDate expiryDate, Set<Tag> tags) {
        super(name);
        requireNonNull(quantity);

        this.sets = new TreeMap<>(SET_COMPARATOR);
        this.sets.put(Optional.ofNullable(expiryDate), quantity);
        if (tags == null) {
            this.tags = new HashSet<>();
        } else {
            this.tags = new HashSet<>(tags);
        }
    }

    /**
     * Constructs a set of ingredients directly from the map of expiry dates and quantities.
     */
    public Ingredient(String name, TreeMap<Optional<ExpiryDate>, Quantity> sets, Set<Tag> tags) {
        super(name);

        assert !sets.isEmpty();
        this.sets = sets;
        this.tags = Objects.requireNonNullElseGet(tags, HashSet::new);
    }

    public Quantity getQuantity() {
        assert !this.sets.isEmpty();

        // we *COULD* make an "identity" for Quantity, but that's too much effort, and each
        // class of Quantity would need to explicitly handle that. Sadge.

        // if we have at least 2, then we can get() the optional.
        if (this.sets.size() >= 2) {

            return this.sets.values()
                .stream()
                .reduce((a, b) -> a.add(b).getValue())
                .get();

        } else {
            // well. then it's just the first one.
            return this.sets.firstEntry().getValue();
        }
    }

    public Optional<ExpiryDate> getExpiryDate() {
        // just return the first expiry date.
        return this.sets.firstEntry().getKey();
    }

    public TreeMap<Optional<ExpiryDate>, Quantity> getIngredientSets() {

        // i want const correctness dammit
        var ret = new TreeMap<Optional<ExpiryDate>, Quantity>(SET_COMPARATOR);
        ret.putAll(this.sets);

        return ret;
    }

    public Set<Tag> getTags() {
        return new HashSet<>(this.tags);
    }

    public String getTagList() {
        if (this.tags.isEmpty()) {
            return "No tags attached";
        }
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (var tag : this.tags) {
            sb.append(index)
                .append(" : ")
                .append(tag.getTagName())
                .append("\n");
            index++;
        }
        return sb.toString();
    }

    /**
     * Combines the quantities of this ingredient and the provided ingredient.
     *
     * @param other the other ingredient
     * @return      a new {@code Ingredient} with the combined quantities
     * @throws IncompatibleIngredientsException if the units of both ingredients were not compatible
     */
    public Ingredient combine(Ingredient other) throws IncompatibleIngredientsException {

        if (!this.isSame(other)) {
            throw new IncompatibleIngredientsException(String.format("cannot combine '%s' with '%s'",
                this.name, other.name));
        }

        // there's no constructor that takes both an existing map and the comparator...
        var newSets = new TreeMap<Optional<ExpiryDate>, Quantity>(SET_COMPARATOR);
        newSets.putAll(this.sets);

        // because of exceptions, we cannot do this using nice lambdas and stuff.
        // so write some dirty imperative code to merge the ingredients.
        for (var entry : other.sets.entrySet()) {

            var exp = entry.getKey();
            var qty = entry.getValue();

            // get the existing quantity of ingredient with the given expiry date
            var existingQty = newSets.get(exp);
            if (existingQty != null) {
                // it exists; time to combine them using Quantity::add()
                // (assuming they are compatible, of course)
                var newQty = existingQty.add(qty).orElseThrow(IncompatibleIngredientsException::new);

                newSets.put(exp, newQty);
            } else {
                // it doesn't exist; so just add it in.
                newSets.put(exp, qty);
            }
        }

        return new Ingredient(this.name.toString(), newSets, this.tags);
    }

    @Override
    public boolean isSame(Entry other) {
        return other == this
                || (other instanceof Ingredient
                && this.name.equals(((Ingredient) other).name));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Ingredient
                && this.name.equals(((Ingredient) other).name)
                && this.sets.equals(((Ingredient) other).sets));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, this.sets);
    }

    @Override
    public String toString() {

        return String.format("%s (%s)%s \nTags: \n%s",
            this.getName(),
            this.getQuantity(),
            this.getExpiryDate().map(d -> String.format(" expires: %s", d))
                .orElse(""),
            getTagList());
    }
}
