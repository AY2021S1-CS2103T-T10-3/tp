// Pair.java

package chopchop.commons.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * A container class with two values, of types L and R. No restrictions are
 * imposed on the types or their values.
 */
public class Pair<T, U> {

    private final T one;
    private final U two;

    /**
     * Constructs a new Pair with the specified values.
     *
     * @param a the first value.
     * @param b the second value.
     */
    public Pair(T a, U b) {
        this.one = a;
        this.two = b;
    }

    /**
     * Obtains the first value from the pair.
     *
     * @return the first value.
     */
    public T fst() {
        return this.one;
    }

    /**
     * Obtains the second value from the pair.
     *
     * @return the second value.
     */
    public U snd() {
        return this.two;
    }

    /**
     * Performs a map on both values of the pair, returning the new pair;
     * the original pair is left unmodified. The BiFunction should return
     * the new pair.
     *
     * @param fn the function to map the values on.
     * @return   the new Pair.
     */
    public <T1, U1> Pair<T1, U1> map(BiFunction<? super T, ? super U, Pair<T1, U1>> fn) {
        return fn.apply(this.one, this.two);
    }

    /**
     * Performs a map on only the first value of the pair, returning the
     * new pair; the original pair is left unmodified. The second value of
     * the pair is returned as-is.
     *
     * @param fn the function to map the first value on.
     * @return   the new Pair.
     */
    public <T1> Pair<T1, U> mapFst(Function<? super T, ? extends T1> fn) {
        return new Pair<T1, U>(fn.apply(this.one), this.two);
    }

    /**
     * Performs a map on only the second value of the pair, returning the
     * new pair; the original pair is left unmodified. The first value of
     * the pair is returned as-is.
     *
     * @param fn the function to map the second value on.
     * @return   the new Pair.
     */
    public <U1> Pair<T, U1> mapSnd(Function<? super U, ? extends U1> fn) {
        return new Pair<T, U1>(this.one, fn.apply(this.two));
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.one, this.two);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>)) {
            return false;
        }

        var other = (Pair<?, ?>) obj;
        return other.one.equals(this.one)
            && other.two.equals(this.two);
    }

    /**
     * Creates a pair, consisting of the provided elements. This exists as a helper method
     * because it's shorter to type than {@code new Pair<>(a, b)}.
     *
     * @param fst the first element of the pair
     * @param snd the second element of the pair
     * @return    the new Pair.
     */
    public static <T, U> Pair<T, U> of(T fst, U snd) {
        return new Pair<T, U>(fst, snd);
    }

    /**
     * Returns a comparator that compares using the first element of the pair only.
     */
    public static <A extends Comparable<A>, B> Comparator<Pair<A, B>> comparingFirst() {
        return (a, b) -> a.fst().compareTo(b.fst());
    }

    /**
     * Returns a comparator that compares using the second element of the pair only.
     */
    public static <A, B extends Comparable<B>> Comparator<Pair<A, B>> comparingSecond() {
        return (a, b) -> a.snd().compareTo(b.snd());
    }

}
