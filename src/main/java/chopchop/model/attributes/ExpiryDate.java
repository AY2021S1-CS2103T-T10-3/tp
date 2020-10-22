package chopchop.model.attributes;

import static chopchop.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ExpiryDate implements Comparable<ExpiryDate> {

    public static final String MESSAGE_CONSTRAINTS =
        "Expiry date should be of the form yyyy-MM-dd";
    // TODO: Accept a wider range of date/time formats
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final LocalDate date;

    /**
     * Constructs an {@code ExpiryDate}.
     *
     * @param date A valid string in the date format, yyyy-MM-dd
     */
    public ExpiryDate(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_CONSTRAINTS);
        this.date = LocalDate.parse(date);
    }

    /**
     * Checks if the input date is valid.
     *
     * @param test A string of unknown date format
     * @return true if the date format is valid. Otherwise, false.
     */
    public static boolean isValidDate(String test) {
        try {
            LocalDate.parse(test, FORMAT);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return this.date.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof ExpiryDate // instanceof handles nulls
            && this.date.equals(((ExpiryDate) other).date));
    }

    @Override
    public int compareTo(ExpiryDate other) {
        return this.date.compareTo(other.date);
    }
}
