// Slice.java

package chopchop.commons.util;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Represents a non-owning array-like reference. Allows for efficient
 * substring-like operations.
 */
public class Slice<T> {

    public final T[] xs;
    public final int begin;
    public final int length;

    /**
     * Constructs a new Slice from the backing array
     *
     * @param xs     the backing array
     * @param begin  the starting index of the slice
     * @param length the length of the slice
     */
    public Slice(T[] xs, int begin, int length) {
        this.xs     = xs;
        this.begin  = begin;
        this.length = length;
    }

    /**
     * Constructs a new Slice from the backing array. The length of the slice is the
     * length of the backing array, minus the starting offset.
     *
     * @param xs     the backing array
     * @param begin  the starting index of the slice
     */
    public Slice(T[] xs, int begin) {
        this.xs     = xs;
        this.begin  = begin;

        if (begin > xs.length) {
            this.length = 0;
        } else {
            this.length = xs.length - begin;
        }
    }

    /**
     * Constructs a new Slice from the backing array. The slice spans the whole array.
     *
     * @param xs     the backing array
     */
    public Slice(T[] xs) {
        this.xs     = xs;
        this.begin  = 0;
        this.length = xs.length;
    }

    /**
     * Get the starting index of the slice.
     *
     * @return the starting index of the slice.
     */
    public int begin() {
        return this.begin;
    }

    /**
     * Get one-past-the-last index of the slice.
     *
     * @return one-past-the-index of the slice.
     */
    public int end() {
        return this.begin + this.length;
    }

    /**
     * Get the length of the slice.
     *
     * @return the length of the slice.
     */
    public int size() {
        return this.length;
    }

    /**
     * Get the ith element of the slice.
     *
     * @param i the index of the element to get
     * @return the element at the specified index
     */
    public T get(int i) {
        return this.xs[this.begin + i];
    }

    /**
     * Get the first element of the slice.
     *
     * @return the first element of the slice
     */
    public T front() {
        assert this.size() > 0;
        return this.xs[this.begin];
    }

    /**
     * Get the last element of the slice.
     *
     * @return the last element of the slice
     */
    public T back() {
        assert this.size() > 0;
        return this.xs[this.begin + this.length - 1];
    }

    /**
     * Return a new slice without the first n elements. If n is larger than the length of
     * the slice, an empty slice will be returned.
     *
     * @param n the number of elements to drop
     * @return the new subslice
     */
    public Slice<T> drop(int n) {
        if (this.length < n) {
            n = this.length;
        }

        return new Slice<T>(this.xs, this.begin + n, this.length - n);
    }

    /**
     * Return a new slice with only the first n elements. If n is larger than the length of
     * the slice, a copy of the original slice will be returned.
     *
     * @param n the number of elements to take
     * @return the new subslice
     */
    public Slice<T> take(int n) {
        if (this.length < n) {
            n = this.length;
        }

        return new Slice<T>(this.xs, this.begin, n);
    }

    /**
     * Return a new slice with only the last n elements. If n is larger than the length of
     * the slice, a copy of the original slice will be returned.
     *
     * @param n the number of elements to take (from the back)
     * @return the new subslice
     */
    public Slice<T> takeLast(int n) {
        if (this.length < n) {
            n = this.length;
        }

        return new Slice<T>(this.xs, this.begin + this.length - n, n);
    }

    /**
     * Return a new slice with the given bounds.
     *
     * @param start the starting index of the new slice
     * @param len   the length of the new slice
     * @return the new subslice
     */
    public Slice<T> slice(int start, int len) {
        return new Slice<T>(this.xs, this.begin + start, len);
    }

    /**
     * Return a Stream from the elements in the slice
     *
     * @return a Stream of elements
     */
    public Stream<T> stream() {
        return Arrays.stream(this.xs)
            .skip(this.begin())
            .limit(this.size());
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(this.xs, this.begin(), this.end()));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Slice<?>)) {
            return false;
        }

        return ((Slice<?>) other).stream().collect(Collectors.toList())
            .equals(this.stream().collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        // just cheat
        return this.stream()
            .collect(Collectors.toList())
            .hashCode();
    }
}
