// ResultTest.java

package chopchop.commons.util;

import java.util.Optional;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static chopchop.testutil.Assert.assertThrows;

public class ResultTest {

    private final Result<Integer> r1 = Result.of(1234);
    private final Result<Integer> e1 = Result.error("an error");

    @Test
    public void test_values() {
        assertEquals(1234, r1.getValue());
        assertEquals(Optional.of(1234), r1.getValueOpt());
        assertEquals(Optional.empty(), r1.getErrorOpt());

        assertEquals("an error", e1.getError());
        assertEquals(Optional.of("an error"), e1.getErrorOpt());
        assertEquals(Optional.empty(), e1.getValueOpt());

        assertThrows(NoSuchElementException.class, () -> r1.getError());
        assertThrows(NoSuchElementException.class, () -> e1.getValue());

        assertTrue(r1.hasValue());
        assertFalse(r1.isError());

        assertFalse(e1.hasValue());
        assertTrue(e1.isError());
    }

    @Test
    public void test_mapping() {
        assertEquals(r1.map(x -> x + x), Result.of(2468));
        assertEquals(e1.map(x -> x + x), Result.error("an error"));

        assertThrows(RuntimeException.class, () -> r1.perform(x -> {
            throw new RuntimeException("kekw");
        }));

        // there's no assertNoThrow, so just perform the thing -- if we threw, then the test will fail anyway.
        e1.perform(x -> {
            throw new RuntimeException("shouldn't be thrown!");
        });
    }

    @Test
    public void test_monadic() {
        assertEquals(1234, e1.orElse(1234));

        assertEquals(Result.of(2468), r1.then(x -> Result.of(x + x)));
        assertEquals(e1, e1.then(x -> Result.of(x + x)));

        assertEquals(Optional.of(1234), r1.toOptional());
        assertEquals(Optional.empty(), e1.toOptional());

        assertThrows(RuntimeException.class, () -> e1.throwIfError(e -> {
            throw new RuntimeException("kekw");
        }));

        // same thing -- this shouldn't throw.
        r1.throwIfError(e -> {
            throw new RuntimeException("shouldn't be thrown!");
        });

        assertEquals(1234, r1.orElseThrow(e -> new RuntimeException("")));
        assertThrows(RuntimeException.class, () -> e1.orElseThrow(e -> new RuntimeException("")));
    }

    @Test
    public void test_toString() {
        assertEquals("Result(1234)", r1.toString());
        assertEquals("Error(an error)", e1.toString());
    }

    @Test
    public void test_equals() {
        assertNotEquals(r1, 1234);
        assertNotEquals(e1, "an error");

        assertEquals(r1, r1);
        assertEquals(e1, e1);

        assertNotEquals(r1, e1);
        assertNotEquals(r1, Result.of(2345));
        assertNotEquals(e1, Result.<Integer>error("some other error"));

        assertEquals(r1, Result.of(1234));
        assertEquals(e1, Result.<Integer>error("an error"));

        assertEquals(e1, Result.ofNullable(null, "an error"));
        assertEquals(r1, Result.ofNullable(1234, "an error"));

        assertEquals(e1, Result.ofOptional(Optional.empty(), "an error"));
        assertEquals(r1, Result.ofOptional(Optional.of(1234), "an error"));
    }

    @Test
    public void test_flattening() {
        assertEquals(Result.flatten(Result.of(r1)), r1);
        assertEquals(Result.flatten(Result.of(e1)), e1);
        assertEquals(Result.flatten(Result.<Result<Integer>>error("owo")), Result.<Integer>error("owo"));


        assertEquals(Optional.empty(), Result.flattenOptional(Optional.of(e1)));
        assertEquals(Optional.empty(), Result.flattenOptional(Optional.empty()));

        assertEquals(Optional.of(1234), Result.flattenOptional(Optional.of(r1)));
    }

    @Test
    public void test_transposition() {

        assertEquals(Result.transpose(Optional.of(r1)), Result.of(Optional.of(1234)));
        assertEquals(Result.transpose(Optional.empty()), Result.of(Optional.empty()));

        assertEquals(Result.transpose(Optional.of(e1)), e1);
    }

    @Test
    public void test_extraction() {
        assertEquals(Result.extractException(Result.error("an error")), Result.error("an error"));

        var re1 = Result.of(Either.left(new RuntimeException("oops")));
        var rr1 = Result.of(Either.<RuntimeException, Integer>right(1234));

        assertThrows(RuntimeException.class, () -> Result.extractException(re1));
        assertEquals(Result.of(1234), Result.extractException(rr1));
    }
}
