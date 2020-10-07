// Result.java

package chopchop.util;

import java.util.Optional;
import java.util.function.Function;

/**
 * The Result class is used to encapsulate the result of some computation
 * producing a value of type {@code T} which may or may not fail. It also holds
 * a string value describing the reason for the failure or error, if indeed the
 * wanted value of {@code T} was not produced successfully.
 *
 * In other words, a {@code Result<T>} is an {@code Optional<T>} that also contains
 * a reason for the error.
 */
public class Result<T> extends Either<String, T> {

    /**
     * This constructor is private; use the of() and error() methods to create
     * Results.
     */
    private Result(T value, String message) {

        // note that, by convention, an Either<L, R> holds the failure case in the
        // left variant, and the success case in the right variant.
        super(message, value);
    }

    /**
     * Gets the contained value.
     *
     * @return the contained value
     * @throws NoSuchElementException if the Result was invalid (ie. does not have a value)
     */
    public T getValue() {
        return super.fromRight();
    }

    /**
     * Gets the contained value.
     *
     * @return the contained value, optionally
     */
    public Optional<T> getValueOpt() {
        return super.fromRightOpt();
    }

    /**
     * Checks whether this Result contains a value.
     *
     * @return true iff the Result contains a value.
     */
    public boolean hasValue() {
        return super.isRight();
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     * @throws NoSuchElementException if the Result was valid (ie. does not have an error message)
     */
    public String getError() {
        return super.fromLeft();
    }

    /**
     * Gets the error message.
     *
     * @return the error message, optionally
     */
    public Optional<String> getErrorOpt() {
        return super.fromLeftOpt();
    }

    /**
     * Checks whether this Result contains an error message.
     *
     * @return true iff the Result contains an error message.
     */
    public boolean isError() {
        return super.isLeft();
    }

    /**
     * Performs a functor map on the value of this Result, returning a new result
     * with the modified value. If the original result was an error variant, then
     * the error message is forwarded unchanged.
     *
     * @param fn the function to apply
     * @return   the new result
     */
    public <R> Result<R> map(Function<? super T, ? extends R> fn) {
        return new Result<R>(
            super.fromRightOpt().map(fn).orElse(null),
            super.fromLeftOpt().orElse(null)
        );
    }

    /**
     * Performs a monadic bind (>>=) on the value of this result. This is equivalent to
     * {@code flatMap} in Java (eg. Optional).
     *
     * @param fn the function to bind; it should return a Result.
     * @return   the new result
     */
    public <R> Result<R> then(Function<? super T, Result<R>> fn) {
        return super.fromRightOpt().map(fn).orElseGet(() -> Result.error(this.getError()));
    }

    /**
     * If the Result contains a value, return it, otherwise return {@code other}.
     *
     * @param other the alternative value to use
     * @return      either the value contained in the result, or the alternative provided
     */
    public T orElse(T other) {
        return super.fromRightOpt().orElse(other);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",
            this.hasValue() ? "Result" : "Error",
            this.hasValue() ? this.getValue() : this.getError());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Result<?>)) {
            return false;
        }

        var other = (Result<?>) obj;
        if (other.hasValue() != this.hasValue()) {
            return false;
        }

        return this.hasValue()
            ? this.getValue().equals(other.getValue())
            : this.getError().equals(other.getError());

    }

    /**
     * Flattens a result containing another result into just a single-layer result; this does not
     * work recursively (eg. {@code flatten(Result<Result<Result<Int>>>)} will only give you
     * {@code Result<Result<Int>>}.
     *
     * NB: I would have liked this to be a non-static method so you could call {@code expr.flatten()},
     * but by the time you have an expression, generics are already type-erased so there is no way to
     * check if the value type (T) is actually a Result or not.
     *
     * @param result the result to flatten
     * @return       a Result with one level of nesting removed.
     */
    public static <T> Result<T> flatten(Result<Result<T>> result) {
        if (result.isError()) {
            return Result.error(result.getError());
        } else {
            return result.getValue();
        }
    }

    /**
     * Creates a new {@code Result} containing the successfully-computed value.
     *
     * @param value the value
     * @return      a {@code Result} containing the given value
     */
    public static <T> Result<T> of(T value) {
        return new Result<T>(value, null);
    }

    /**
     * Creates a new {@code Result} with an error message describing why a value
     * could not be computed.
     *
     * @param message   the error message
     * @return          a {@code Result} without a value, with the given error message.
     */
    public static <T> Result<T> error(String message) {
        return new Result<T>(null, message);
    }

    /**
     * Creates a new {@code Result} with an error message describing why a value
     * could not be computed. This version functions like {@code String::format}.
     *
     * @param message   the error message (format string)
     * @param args      the variadic format args
     * @return          a {@code Result} without a value, with the given error message.
     */
    public static <T> Result<T> error(String message, Object... args) {
        return new Result<T>(null, String.format(message, args));
    }

    /**
     * Creates a new {@code Result} containing the successfully-computed value if it was
     * non-null, or an error result if it was null.
     *
     * @param value the value (may be null)
     * @param error the error message to use if the value was null
     * @return      a result.
     */
    public static <T> Result<T> ofNullable(T value, String error) {
        return new Result<T>(value, value == null ? error : null);
    }

    /**
     * Creates a new {@code Result} containing the successfully-computed value if the
     * optional contained a value, or the error message if it was empty.
     *
     * @param value the value-containing optional
     * @param error the error message to use if the optional was empty
     * @return      a result.
     */
    public static <T> Result<T> ofOptional(Optional<T> value, String error) {
        return new Result<T>(value.orElse(null), value.isPresent() ? null : error);
    }
}