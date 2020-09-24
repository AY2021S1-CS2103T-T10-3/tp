// StringView.java

package chopchop.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * A class representing a view over a character array (ie. a String), that allows operations like
 * drop(), take(), substr(), etc. to be implemented efficiently (without copying the underlying data).
 *
 * The class behaves like an immutable class -- most methods will return a new copy of the StringView
 * with the appropriate bounds (the array itself is not copied), except for one method -- bisect().
 *
 * See the documentation for bisect() to learn why.
 */
public class StringView {

    private char[] chars;
    private int begin;
    private int end;

    /**
     * Constructs a new string view using the contents of the given string.
     *
     * @param string the string to use
     */
    public StringView(String string) {
        this.chars = string.toCharArray();
        this.begin = 0;
        this.end   = this.chars.length;
    }

    /**
     * Constructs a new string view using the provided array and bounds.
     *
     * @param chars the array of characters to use
     * @param begin the starting index (inclusive) of this string view
     * @param end   the ending index (exclusive) of this string view
     */
    private StringView(char[] chars, int begin, int end) {
        this.chars = chars;
        this.begin = Math.max(begin, 0);
        this.end   = Math.min(end, this.chars.length);

        assert this.begin <= this.end;
    }

    /**
     * Reseats this string view to point to the given character array and bounds. This method
     * is required for bisect(), and violates the immutability of the string view.
     *
     * @param chars the array of characters to use
     * @param begin the starting index (inclusive) of this string view
     * @param end   the ending index (exclusive) of this string view
     */
    private void set(char[] chars, int begin, int end) {
        this.chars = chars;
        this.begin = begin;
        this.end   = end;
    }

    /**
     * Reseats this string view to refer to the same array as the given other string view, with
     * the appropriate bounds etc.
     *
     * @param other the source string view
     */
    private void replaceWith(StringView other) {
        this.chars = other.chars;
        this.begin = other.begin;
        this.end   = other.end;
    }

    /**
     * Bisects this string view into two parts using the given delimiter, by splitting at the first
     * instance of the delimiter in this view. If the delimiter does not exist, the second view returned
     * will be empty, and the first will be a copy of this.
     *
     * Additionally, the second view will be front-trimmed of extra instances of {@code delim}, if they
     * exist.
     *
     * This version of {@code bisect()} does not violate any immutability constraints.
     *
     * @param delim the delimiter to use
     * @return      a {@code Pair} of string views
     */
    public Pair<StringView, StringView> bisect(char delim) {
        var fst = this.take(this.find(delim));
        var snd = this.drop(fst.size() + 1).dropWhile(x -> x == delim);

        return new Pair<>(fst, snd);
    }

    /**
     * Bisects this string view into two parts using the given delimiter, by splitting at the first
     * instance of the delimiter in this view. If the delimiter does not exist, the second view returned
     * will be empty, and the first will be a copy of this.
     *
     * Additionally, the second view will be front-trimmed of extra instances of {@code delim}, if they
     * exist.
     *
     * This version of {@code bisect()} will modify both parameters {@code x} and {@code xs}, and will
     * replace their contents with the appropriate first and second parts of the split.
     *
     * @param x     the view in which to place the first part of the bisection
     * @param delim the delimiter to use
     * @param xs    the view in which to place the second part of the bisection
     */
    public void bisect(StringView x, char delim, StringView xs) {
        var fst = this.take(this.find(delim));
        var snd = this.drop(fst.size() + 1).dropWhile(c -> c == delim);

        x.replaceWith(fst);
        xs.replaceWith(snd);
    }

    /**
     * Bisects this string view into two parts using the given delimiter, by splitting at the first
     * instance of the delimiter in this view. If the delimiter does not exist, the second view returned
     * will be empty, and the first will be a copy of this.
     *
     * Additionally, the second view will be front-trimmed of extra instances of {@code delim}, if they
     * exist.
     *
     * This version of {@code bisect()} will modify the {@code xs} parameter, and will
     * replace its content with the second part of the split. The first part is returned normally.
     *
     * @param delim the delimiter to use
     * @param xs    the view in which to place the second part of the bisection
     * @return      the first part of the bisection
     */
    public StringView bisect(char delim, StringView xs) {
        var fst = this.take(this.find(delim));
        var snd = this.drop(fst.size() + 1).dropWhile(c -> c == delim);

        xs.replaceWith(snd);
        return fst;
    }

    /**
     * Gets the size of the string view.
     *
     * @return the size
     */
    public int size() {
        return this.end - this.begin;
    }

    /**
     * Checks if the string view is empty.
     *
     * @return true iff the view is empty
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Finds the first index at which the substring occurs.
     *
     * @param sub the substring to search for
     * @return the index at which the given substring first occurs,
     *         or {@code -1} if it does not exist
     */
    public int find(String sub) {
        return this.find(new StringView(sub));
    }

    /**
     * Finds the first index at which the substring occurs.
     *
     * @param sub the substring to search for
     * @return the index at which the given substring first occurs,
     *         or {@code -1} if it does not exist
     */
    public int find(StringView sub) {
        return Collections.indexOfSubList(Arrays.asList(this.chars), Arrays.asList(sub.chars));
    }

    /**
     * Finds the first index at which the character occurs.
     *
     * @param ch the character to search for
     * @return the index of the character, or {@code -1} if it does not exist.
     */
    public int find(char ch) {
        for (int i = this.begin; i < this.end; i++) {
            if (this.chars[i] == ch) {
                return i - this.begin;
            }
        }

        return -1;
    }

    /**
     * Checks if the string view starts with the given substring. This is equivalent to
     * {@code sv.find(sub) == 0}.
     *
     * @param sub the substring to search for
     * @return true iff this string view starts with the substring.
     */
    public boolean startsWith(String sub) {
        return this.find(sub) == 0;
    }

    /**
     * Checks if the string view starts with the given substring. This is equivalent to
     * {@code sv.find(sub) == 0}.
     *
     * @param sub the substring to search for
     * @return true iff this string view starts with the substring.
     */
    public boolean startsWith(StringView sub) {
        return this.find(sub) == 0;
    }

    /**
     * Obtains the character at the given index.
     *
     * @param idx the index
     * @return the character
     */
    public char at(int idx) {
        return this.chars[this.begin + idx];
    }

    /**
     * Returns a new string view, without the first {@code n} characters. If {@code n} is larger
     * than the size of this view, then an empty view is returned. {@code n} cannot be negative.
     *
     * @param n the number of characters to drop.
     * @return a new string view without the first {@code n} characters.
     */
    public StringView drop(int n) {
        assert n >= 0;
        return new StringView(this.chars, Math.min(this.begin + n, this.end), this.end);
    }

    /**
     * Returns a new string view, with only the first {@code n} characters. If {@code n} is larger
     * than the size of this view or is negative, then a copy of this view is returned.
     *
     * @param n the number of characters to take.
     * @return a new string view with only first {@code n} characters.
     */
    public StringView take(int n) {
        if (n < 0) {
            n = this.size();
        }

        return new StringView(this.chars, this.begin, Math.min(this.begin + n, this.end));
    }

    /**
     * Returns a new string view, with only the last {@code n} characters. If {@code n} is larger
     * than the size of this view, then a copy of this view is returned. {@code n} cannot be negative.
     *
     * @param n the number of characters to take.
     * @return a new string view with only the last {@code n} characters.
     */
    public StringView takeLast(int n) {
        assert n >= 0;
        return new StringView(this.chars, Math.min(this.begin + n, this.end), this.end);
    }

    /**
     * Returns a new view by dropping characters as long as the given predicate holds true;
     * at the first instance where the predicate returns true, the string is returned.
     *
     * @param pred the predicate to use
     * @return a new string view
     */
    public StringView dropWhile(Predicate<Character> pred) {
        int i = this.begin;
        while (i < this.end && pred.test(this.chars[i])) {
            i += 1;
        }

        return new StringView(this.chars, i, this.end);
    }

    /**
     * Returns a new view by taking characters as long as the given predicate holds true;
     * at the first instance where the predicate returns false, the string is returned.
     *
     * @param pred the predicate to use
     * @return a new string view
     */
    public StringView takeWhile(Predicate<Character> pred) {
        int n = this.begin;
        while (n < this.end && pred.test(this.chars[n])) {
            n += 1;
        }

        return new StringView(this.chars, this.begin, n);
    }

    /**
     * Returns a new view containing {@code n} characters at the front which were previously dropped.
     * For example:
     * {@code
     *     var sv = StringView("asdf").drop(1);
     *     assert sv.undrop(1).equals("asdf");
     * }
     *
     * @param n the number of characters to undrop
     * @return a new string view
     */
    public StringView undrop(int n) {
        return new StringView(this.chars, Math.max(0, this.begin - n), this.end);
    }


    @Override
    public String toString() {
        return new String(this.chars, this.begin, this.size());
    }

    /**
     * Provides operator== for StringView. This method works for both {@code String} instances
     * as well as {@code StringView} instances.
     *
     * @param obj the object to compare
     * @return true iff the string contents are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {

            var other = (String) obj;
            return Arrays.equals(
                this.chars, this.begin, this.end,
                other.toCharArray(), 0, other.length()
            );

        } else if (obj instanceof StringView) {
            var other = (StringView) obj;
            return Arrays.equals(
                this.chars, this.begin, this.end,
                other.chars, other.begin, other.end
            );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
