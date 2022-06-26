package com.terheyden.valid.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.Validations;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * How to use constraints on containers, such as List, Optional, etc.
 *     The constraint must have {@code ElementType.TYPE_USE} declared in it.
 *     (All built-in constraints have this.)
 *
 * @see <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#container-element-constraints">
 *     Container Element Constraints documentation</a>
 */
public class ContainerValidationsTest {

    /**
     * Test validating a List passed to a method.
     */
    @Test
    public void testListMethodParam() {

        MethodValidator joinStrings = Validations.createMethodValidator(ContainerValidationsTest.class, "joinStrings");

        // List items must be not blank, so this should pass:
        joinStrings.validateParams(this, List.of("a", "b", "c"));
        // This should fail:
        // "ContainerValidationsTest.joinStrings.arg0[1].<list element>: must not be blank"
        assertThrows(ConstraintViolationException.class, () -> joinStrings.validateParams(this, List.of("a", "", "c")));
    }

    /**
     * Test validating an Optional<> method param.
     */
    @Test
    public void testOptionalMethodParam() {

        MethodValidator getLength = Validations.createMethodValidator(ContainerValidationsTest.class, "getLength");

        // The optional value must be not blank, so this should be good:
        getLength.validateParams(this, Optional.of("Hello"));
        // Unexpected — Optional.empty() also throws; I thought it wouldn't try validating empty:
        // "ContainerValidationsTest.getLength.arg0: must not be blank"
        assertThrows(ConstraintViolationException.class, () -> getLength.validateParams(this, Optional.empty()));

        // This should fail — it has a value and the value is blank:
        assertThrows(ConstraintViolationException.class, () -> getLength.validateParams(this, Optional.of("")));
    }

    /**
     * Test validating a local Map object, then a nested Map.
     */
    @Test
    public void testValidMap() {

        Map<String, @Valid Widget> widgets = new HashMap<>();
        widgets.put("widget1", new Widget("widget1").addPartNumber(101));
        widgets.put("widget2", new Widget("widget2").addPartNumber(201));

        // So far the map is valid, so this should pass:
        Validations.validate(widgets);

        // Add an invalid widget (part number is too low):
        widgets.put("widget3", new Widget("widget3").addPartNumber(1));
        // I thought this would fail, but it doesn't.
        // My guess is, the validator can't see the annotations unless they're defined in a class
        // or on a method parameter:
        Validations.validate(widgets);

        // Let's try that map in a class, then.
        // And indeed it does fail:
        // "Factory.widgets[widget3].partNumbers[0].<list element>: must be greater than or equal to 100"
        Factory factory = new Factory(UUID.randomUUID(), widgets);
        assertThrows(ConstraintViolationException.class, () -> Validations.validate(factory));
    }

    private String joinStrings(List<@NotBlank String> strings) {
        return String.join("", strings);
    }

    private int getLength(Optional<@NotBlank String> strOpt) {
        return strOpt.map(String::length).orElse(0);
    }

    private static class Widget {

        @NotBlank
        private final String id;

        private final List<@Min(100) Integer> partNumbers = new ArrayList<>();

        private Widget(String id) {
            this.id = id;
        }

        private String getId() {
            return id;
        }

        private Widget addPartNumber(int partNumber) {
            partNumbers.add(partNumber);
            return this;
        }
    }

    private static class Factory {

        @NotNull
        private final UUID factoryId;

        @NotEmpty
        private final Map<@NotBlank String, @Valid Widget> widgets;

        private Factory(UUID factoryId, Map<String, Widget> widgets) {
            this.factoryId = factoryId;
            this.widgets = Map.copyOf(widgets);
        }
    }
}
