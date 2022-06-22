package com.terheyden.valid.examples;

import org.junit.jupiter.api.Test;

import com.terheyden.valid.ValidationAssertions;

/**
 * Simple test of a class-level annotation.
 */
public class ValidEmployeeValidatorTest {

    @Test
    public void test() {

        // Cora is under 21 so it should fail.
        Employee cora = new Employee("Cora", 11);
        ValidationAssertions.assertInvalid(cora);
    }
}
