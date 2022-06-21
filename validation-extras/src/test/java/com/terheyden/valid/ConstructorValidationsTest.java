package com.terheyden.valid;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import static com.terheyden.valid.ValidationAssertions.assertInvalid;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ConstructorValidationsTest unit tests.
 */
public class ConstructorValidationsTest {

    @Test
    public void testOneConstructor() {

        Animal1 animal1 = new Animal1("Cora", 12);
        // Bad age:
        assertInvalid(() -> new Animal1("Fluffy", -1));
        // Bad name:
        assertInvalid(() -> new Animal1(null, 30));
    }

    @Test
    public void testManyConstructors() {

        // Test both constructors and their validators:
        Animal2 animal2 = new Animal2("Cora", 12);
        Animal2 animal2b = new Animal2(UUID.randomUUID(), "Fluffy", 10);

        // Test bad cases:
        assertInvalid(() -> new Animal2("Cora", -1));
        assertInvalid(() -> new Animal2(null, 10));
        assertInvalid(() -> new Animal2(null, "Fluffy", 10));
    }

    @Test
    public void testInheritance() {

        // Subclasses still trigger the validators of the superclass:
        assertInvalid(() -> new Cat3("Fluffy", -1));
        assertInvalid(() ->  new Cat3(null, 30));
    }

    @Test
    public void testBadFinds() {

        // Error! Animal2 has two constructors.
        assertThrows(IllegalStateException.class, () -> Validations
            .createConstructorValidator(Animal2.class));

        // Error! Animal2 doesn't have a constructor with 5 params.
        assertThrows(IllegalStateException.class, () -> Validations
            .createConstructorValidator(Animal2.class, 5));

        // Error! Animal2 doesn't have a constructor that takes a Boolean.
        assertThrows(IllegalStateException.class, () -> Validations
            .createConstructorValidator(Animal2.class, Boolean.class));
    }

    /**
     * A trivial class with one constructor.
     * Performs constructor validation.
     */
    public static class Animal1 {

        private static final ConstructorValidator ANIMAL1 = Validations.createConstructorValidator(Animal1.class);

        private final String name;
        private final int age;

        public Animal1(@NotBlank String name, @Min(1) int age) {
            ANIMAL1.validateParams(name, age);
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * Another trivial class, but this one has two constructors with different parameters.
     */
    public static class Animal2 {

        // Make a constructor validator by using the param count.
        private static final ConstructorValidator ANIMAL2 = Validations
            .createConstructorValidator(Animal2.class, 2);

        // Make a constructor validator by using the unique param type.
        private static final ConstructorValidator ANIMAL3 = Validations
            .createConstructorValidator(Animal2.class, UUID.class);

        private final UUID animalId;
        private final String name;
        private final int age;

        public Animal2(@NotNull UUID animalId, @NotBlank String name, @Positive int age) {
            ANIMAL3.validateParams(animalId, name, age);
            this.animalId = animalId;
            this.name = name;
            this.age = age;
        }

        public Animal2(@NotBlank String name, @Positive int age) {
            ANIMAL2.validateParams(name, age);
            this.animalId = UUID.randomUUID();
            this.name = name;
            this.age = age;
        }

        public UUID getAnimalId() {
            return animalId;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * Are constraints inherited by subclasses?
     * This has no constraints or validators, but the base class does.
     */
    public static class Cat3 extends Animal2 {

        public Cat3(UUID animalId, String name, int age) {
            super(animalId, name, age);
        }

        public Cat3(String name, int age) {
            super(name, age);
        }
    }
}
