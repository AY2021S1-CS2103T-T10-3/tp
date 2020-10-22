// Result.java

package chopchop.commons.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Consumer;

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
     * Calls the function with the contained value iff it was not an error. This method is equivalent
     * to {@code map}, but it does not return a new value. Use it to do things to the contained value
     * in the middle of an expression.
     *
     * @param fn the consumer
     * @return   the result, unmodified.
     */
    public Result<T> perform(Consumer<? super T> fn) {
        if (this.hasValue()) {
            fn.accept(this.getValue());
        }

        return this;
    }

    /**
     * Performs a monadic bind (>>=) on the value of this result. This is equivalent to
     * {@code flatMap} in Java (eg. Optional).
     *
     * @param fn the function to bind; it should return a Result.
     * @return   the new result
     */
    public <R> Result<R> then(Function<? super T, ? extends Result<? extends R>> fn) {
        if (this.hasValue()) {

            // well... this is what the jdk does.
            @SuppressWarnings("unchecked")
            var ret = (Result<R>) fn.apply(this.getValue());
            return ret;

        } else {
            return Result.error(this.getError());
        }
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

    /**
     * Converts a {@code Result<T>} into an {@code Optional<T>}. If the Result contained an error, it
     * is discarded, and an empty optional is returned.
     *
     * @return an Optional version of this result.
     */
    public Optional<T> toOptional() {
        return this.isError()
            ? Optional.empty()
            : Optional.of(this.getValue());
    }

    /**
     * If the result contains an error, throws the exception gotten from the supplier; if not, it returns
     * the result unmodified.
     *
     * @param sup the supplier of the exception to throw
     * @return    the result
     * @throws E  if the result contained an error.
     */
    public <E extends Throwable> Result<T> throwIfError(Function<String, ? extends E> sup) throws E {
        if (this.isError()) {
            throw sup.apply(this.getError());
        } else {
            return this;
        }
    }

    /**
     * If the result contains an error, throws the exception gotten from the supplier; if not, it returns
     * the value in the result.
     *
     * @param sup the supplier of the exception to throw
     * @return    the contained value in the result (if an exception was not thrown)
     * @throws E  if the result contained an error.
     */
    public <E extends Throwable> T orElseThrow(Function<String, ? extends E> sup) throws E {
        if (this.isError()) {
            throw sup.apply(this.getError());
        } else {
            return this.getValue();
        }
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
        return result.isError()
            ? Result.error(result.getError())
            : result.getValue();
    }

    /**
     * Flattens an {@code Optional} containing a {@code Result} into just the optional; if the outer
     * optional was empty, or it was present but the inner result was an error, then an empty optional
     * is returned (the error message of the Result, if any, is discarded). Otherwise, it returns
     * an optional containing the Result's value.
     *
     * @param opt the optional to flatten
     * @return    the flattened optional.
     */
    public static <T> Optional<T> flattenOptional(Optional<? extends Result<? extends T>> opt) {
        return opt.isPresent() && opt.get().hasValue()
            ? Optional.of(opt.get().getValue())
            : Optional.empty();
    }

    /**
     * Transposes an {@code Optional} containing a {@code Result} into a {@code Result} containing
     * an {@code Optional}. If the outer optional was empty, then the result is valid, but contains
     * an empty optional. Otherwise, if the inner result contains an error, then a new Result with
     * that error is returned. If not then the inner result's value is returned as an optional.
     *
     * {@code
     *      Result(valid: Optional(10))     = transpose(Optional.of(Result.of(10)))
     *      Result(error: "owo")            = transpose(Optional.of(Result.error("owo")))
     *      Result(valid: Optiona(empty))   = transpose(Optional.empty())
     * }
     *
     * @param opt the optional to transpose.
     * @return    the transposed result.
     */
    public static <T> Result<Optional<T>> transpose(Optional<Result<T>> opt) {
        if (opt.isEmpty()) {
            return Result.of(Optional.empty());
        }

        if (opt.get().isError()) {
            return Result.error(opt.get().getError());
        } else {
            return Result.of(Optional.of(opt.get().getValue()));
        }
    }

    /**
     * Extracts an exception from a {@code Result<Either<E, T>>}, where the left {@code Either} is the
     * exception to throw, and the right side is the value to keep. If the outer {@code Result} contained
     * an error message, then that error message is returned unmodified.
     *
     * If not, then the inner {@code Either} is inspected; if it was a left variant (ie. a
     * {@code Throwable}), then it is thrown. Else, a Result of the right variant is returned.
     *
     * @param result the result to extract an exception from.
     * @return       the resulting value (if no exception was thrown)
     * @throws E     if the result's inner value was a right-variant Either.
     */
    public static <E extends Throwable, T> Result<T> extractException(
        Result<? extends Either<? extends E, ? extends T>> result) throws E {

        if (result.isError()) {
            return Result.error(result.getError());
        } else {
            var value = result.getValue();
            if (value.isLeft()) {
                throw value.fromLeft();
            } else {
                return Result.of(value.fromRight());
            }
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
