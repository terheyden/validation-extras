package com.terheyden.valid.examples;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ValidCatTest class.
 */
public class ValidCatTest {

    @Test
    public void test() {

        assertThrows(ConstraintViolationException.class, () -> ValidCat.newValidCat("x", 0));
        assertDoesNotThrow(() -> ValidCat.newValidCat("Cora", 10));
    }
}
