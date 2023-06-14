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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * ParamValidationTest unit tests.
 */
class ParamValidationTest {

    private static final Logger LOG = getLogger(ParamValidationTest.class);

    @Test
    void invalidMethodParams_throw() {
        assertThrows(ConstraintViolationException.class, () -> shapeToString((Shape) null));
    }

    @Test
    void goodSubclassedMethodParams() {
        Square square = new Square();
        shapeToString(square);
    }

    private String shapeToString(@NotNull Shape shape) {

        Set<ConstraintViolation<ParamValidationTest>> violations = ParamValidation.checkMethodParams(this, shape);
        LOG.info("Violations: {}", violations);
        ParamValidation.validateMethodParams(this, shape);

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

        @Override
        public String getName() {
            return "Square";
        }

        @Override
        public int getSides() {
            return 4;
        }
    }

    /**
     * Another test class.
     */
    private static class Employee {

        private final UUID employeeId;

        private Employee(@NotNull @org.hibernate.validator.constraints.UUID UUID employeeId) {
            ParamValidation.validateConstructorParams(this, employeeId);
            this.employeeId = employeeId;
        }

        private Employee(@NotNull @Min(10) String employeeId) {
            ParamValidation.validateConstructorParams(this, employeeId);
            this.employeeId = UUID.fromString(employeeId);
        }

        public UUID getEmployeeId() {
            return employeeId;
        }
    }
}
