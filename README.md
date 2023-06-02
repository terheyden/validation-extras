# Java Jakarta Bean Validation — Helper Class

Helper classes for working with Java's Jakarta Bean Validation.

## What is this?

I think [Java Validation](https://hibernate.org/validator/) is an under-appreciated feature.
This repo offers a couple of helper classes to make it easier to use!

No additional dependencies are required, I've made the classes self-contained.
Just copy and paste them into your project, modify as needed, and you're good to go.
(There is a dependency version also though if that's easier for you.)

Validation is baked into Spring Boot and is super easy to use;
These helpers are more for those non-Spring-powered classes.

## What can be validated?

The helpers help you validate:
* Object fields or properties

Things that are harder to validate:
* Method parameters
* Constructor parameters

Things currently impossible to validate:
* Static fields or parameters

## `Validations` class

This is the main helper class. It gives you:

```java
Validations.check(obj)    // returns a list of violations, or empty list
Validations.validate(obj) // throws an exception if there are any violations
```

There are a few other variations and helper methods but these are the main ones.

## `SelfValidating` Interface

This is an interface that you can implement to make your objects self-validating.
Check out how cool this is:

```java
/**
 * This class validates itself upon construction.
 *   - implements the SelfValidating interface
 *   - calls validateSelf() in the constructor
 */
class User implements SelfValidating {

    @NotBlank
    @Size(min = 3)
    private final String name;

    @Min(18)
    private final int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
        validateSelf();
    }
}
```
