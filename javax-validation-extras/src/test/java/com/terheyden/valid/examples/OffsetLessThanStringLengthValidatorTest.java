package com.terheyden.valid.examples;

import org.junit.jupiter.api.Test;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.ValidationAssertions;
import com.terheyden.valid.Validations;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * OffsetLessThanStringLengthValidatorTest unit tests.
 */
public class OffsetLessThanStringLengthValidatorTest {

    private static final MethodValidator GETSUBSTRING = Validations
        .createMethodValidator(OffsetLessThanStringLengthValidatorTest.class, "getSubstring");

    private static final String CORA = "Cora";

    @Test
    public void test() {

        // Good values should pass the validator:
        assertEquals("Co", getSubstring(CORA, 2));

        // Invalid offset should cause our validator to throw:
        ValidationAssertions.assertInvalid(() -> getSubstring(CORA, -1));
    }

    /**
     * Example method for creating and testing a method-level (cross-param) constraint.
     * Returns a substring from index 0 to the specified offset.
     *
     * Note that this method is not static! Static things are not supported
     * by Jakarta Bean Validation.
     */
    @OffsetLessThanStringLength
    public String getSubstring(String fullStr, int endOffset) {

        // A MethodValidator is used to exercise method-level constraints.
        GETSUBSTRING.validateParams(this, fullStr, endOffset);
        return fullStr.substring(0, endOffset);
    }
}
