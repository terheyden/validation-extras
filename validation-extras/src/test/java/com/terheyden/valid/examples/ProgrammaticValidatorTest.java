package com.terheyden.valid.examples;

import org.junit.jupiter.api.Test;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.Validations;

import static com.terheyden.valid.ValidationAssertions.assertInvalid;

/**
 * ProgrammaticValidatorTest class.
 */
public class ProgrammaticValidatorTest {

    MethodValidator SAY_HELLO = Validations.createMethodValidator(ProgrammaticValidatorTest.class, "sayHello");

    @Test
    public void testProgrammatic() {

        // Check out: https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-constraint-definition-contribution
        // Search for "Providing constraint definitions"

        // The programmatic validator checks for nulls, so if this throws, we know it's working:
        assertInvalid(() -> SAY_HELLO.validateParams(this, "Hello", null));
    }

    private void sayHello(@ProgramConstraint String greeting, @ProgramConstraint String name) {
        System.out.println("%s, %s!".formatted(greeting, name));
    }
}
