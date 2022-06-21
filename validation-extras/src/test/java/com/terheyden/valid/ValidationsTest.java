package com.terheyden.valid;

import org.junit.jupiter.api.Test;

import static com.terheyden.valid.ValidationAssertions.assertInvalid;
import static com.terheyden.valid.ValidationAssertions.assertValid;

/**
 * ValidationsTest unit tests.
 */
public class ValidationsTest {

    @Test
    public void test() {

        Employee cora = new Employee("Cora", 12);
        Employee badAge = new Employee("Fluffy", -1);
        Employee badName = new Employee(null, 30);
        Employee nullPerson = null;

        // Good employees should pass.
        assertValid(cora);

        // Bad employees should fail.
        assertInvalid(badAge);
        assertInvalid(badName);
        assertInvalid(nullPerson);
    }
}
