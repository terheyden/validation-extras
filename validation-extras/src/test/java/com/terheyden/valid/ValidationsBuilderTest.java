package com.terheyden.valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.terheyden.valid.examples.Empty;
import com.terheyden.valid.examples.IsPresent;
import com.terheyden.valid.examples.IsPresentValidator;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;

import static com.terheyden.valid.Validations.createMethodValidator;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ValidationsBuilderTest unit tests.
 */
public class ValidationsBuilderTest {

    // For validating method params.
    private static final MethodValidator SAYHELLO = createMethodValidator(ValidationsBuilderTest.class, "sayHello");

    @Test
    public void testDynamicValidator() {

        // Without the custom association, the validation will throw.
        assertThrows(UnexpectedTypeException.class, () -> sayHello(Optional.empty()));

        // The basket obj also won't get validated:
        assertThrows(UnexpectedTypeException.class, () -> Validations.validate(new Basket()));

        // Let's create some custom validation associations:
        ValidationsBuilder builder = new ValidationsBuilder();
        builder.addConstraintValidator(IsPresent.class, IsPresentValidator.class);

        // Now let's also add a lambda validator.
        builder.addConstraintValidator(
            Empty.class,
            List.class,
            list -> list == null || list.isEmpty());

        // Check it out, we can assign multiple validators to the same annotation now.
        builder.addConstraintValidator(
            Empty.class,
            String.class,
            str -> str == null || str.isEmpty());

        // Attach the new custom validations.
        Validations.setValidator(builder);

        // These should pass:
        sayHello(Optional.of("Hello"));
        Validations.validate(new Basket());

        // And these should fail:
        assertThrows(ConstraintViolationException.class, () -> sayHello(Optional.empty()));
        assertThrows(ConstraintViolationException.class, () -> Validations.validate(new Basket("bread")));
        assertThrows(ConstraintViolationException.class, () -> Validations.validate(new Basket().setId("100")));
    }

    /**
     * Example method to test @IsPresent.
     */
    private void sayHello(@IsPresent Optional<String> name) {

        SAYHELLO.validateParams(this, name);
        // The validator has ensured that the optional has a value, so no need to check.
        System.out.println("Hello, " + name.get());
    }

    /**
     * Example class that, when validated, must be empty.
     * (Doesn't make a lot of sense, but it's an example.)
     */
    private static class Basket {

        @Empty
        private final List<String> items = new ArrayList<>();

        @Empty
        private String id = "";

        private Basket() {
            // Empty basket constructor.
        }

        private Basket(String... things) {
            items.addAll(Arrays.asList(things));
        }

        private Basket(List<String> items) {
            this.items.addAll(items);
        }

        private void addItem(String item) {
            items.add(item);
        }

        private List<String> getItems() {
            return items;
        }

        public String getId() {
            return id;
        }

        public Basket setId(String id) {
            this.id = id;
            return this;
        }
    }
}
