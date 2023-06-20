package com.terheyden.valid;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * ArgValidationTest unit tests.
 */
class ArgValidationTest {

    private static final Logger LOG = getLogger(ArgValidationTest.class);

    @Test
    void invalidMethodParams_throw() {
        assertThrows(ConstraintViolationException.class, () -> shapeToString((Shape) null));
    }

    @Test
    void goodSubclassedMethodParams() {
        Square square = new Square("square", 4);
        shapeToString(square);
    }

    @Test
    void methodVsConstructor() {
        Square square = new Square("square", 4);
        assertNotNull(square.getName());
    }

    private String shapeToString(@NotNull Shape shape) {

        Set<ConstraintViolation<Object>> violations = Validations.checkMethodArgs(this, shape);
        LOG.info("Violations: {}", violations);
        Validations.validateMethodArgs(this, shape);

        return shape.getName() + " has " + shape.getSides() + " sides.";
    }

    /**
     * Same method name, same param count, different param type.
     * This is to test the method finder.
     */
    private String shapeToString(@NotEmpty @Min(3) String name) {
        return name;
    }

    /**
     * Test validation and inheritance.
     */
    private interface Shape {
        String getName();
        int getSides();
    }

    /**
     * Extends Shape.
     */
    private static class Square implements Shape {

        private final String name;
        private final int sides;

        Square(String name, int sides) {
            Validations.validateConstructorArgs(name, sides);
            this.name = name;
            this.sides = sides;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getSides() {
            return sides;
        }
    }

    /**
     * Another test class.
     */
    private static class Employee {

        private final UUID employeeId;

        private Employee(@NotNull UUID employeeId) {
            Validations.validateConstructorArgs(employeeId);
            this.employeeId = employeeId;
        }

        private Employee(@NotNull @Min(10) String employeeId) {
            Validations.validateConstructorArgs(employeeId);
            this.employeeId = UUID.fromString(employeeId);
        }

        public UUID getEmployeeId() {
            return employeeId;
        }
    }
}
