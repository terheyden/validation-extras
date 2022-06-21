# validation-extras
Helper classes for working with Jakarta Bean Validation.

- [What is this?](#what-is-this-)
    * [Java validation in a nutshell](#java-validation-in-a-nutshell)
    * [What can be validated?](#what-can-be-validated-)
    * [`validation-extras` features](#-validation-extras--features)
- [Maven](#maven)
    * [For normal (non-Spring) projects](#for-normal--non-spring--projects)
    * [For Spring-based projects](#for-spring-based-projects)
- [How to use](#how-to-use)
    * [Normal (Non-Spring) usage](#normal--non-spring--usage)
        + [Validating fields or properties](#validating-fields-or-properties)
        + [Validating method parameters](#validating-method-parameters)
        + [Validating constructor parameters](#validating-constructor-parameters)
    * [Usage From Spring](#usage-from-spring)
        + [Validating fields or properties](#validating-fields-or-properties-1)
        + [Validating method parameters](#validating-method-parameters-1)
        + [Validating constructor parameters](#validating-constructor-parameters-1)
- [Validation cookbook](#validation-cookbook)
    + [How to assign a constraint validator programmatically](#how-to-assign-a-constraint-validator-programmatically)
- [Performance considerations](#performance-considerations)

# What is this?

The `validation-extras` library makes it easier to work with Jakarta Bean Validation,
most notably when you want to use it outside of the Spring framework.

## Java validation in a nutshell

Jakarta Bean Validation is a framework for validating fields and objects _declaratively_.
For example:

```java
// Example without Validation:
public void saveEmployee(Employee employee) {

    // Guard clauses:
    if (employee == null) {
        throw new IllegalArgumentException("Employee cannot be null.");
    }

    if (employee.getName() == null || employee.getName().isEmpty()) {
        throw new IllegalArgumentException("Employee name is missing.");
    }

    if (employee.getAge() < 1) {
        throw new IllegalArgumentException("Employee age must be greater than 0.");
    }

    LOG.info("Saving employee: {}", employee);
    // ...
}
```

It's cleaner to separate the validation logic from the business logic,
and the Jakarta Bean Validation framework allows us to do this:

```java
// Same example with Validation:
public void saveEmployee(Employee employee) {

    Validations.validate(employee);
    LOG.info("Saving employee: {}", employee);
    // ...
}
```

Where exactly did all those guard clauses go?
They turned into constraints inside the `Employee` class, for example:

```java
public class Employee {
    @NotBlank
    private String name;
    // ...
}
```

Then when `Validations.validate(employee)` is called, those constraints are checked.

## What can be validated?

Things you can validate:
* Object fields or properties
* Method parameters
* Constructor parameters

Things you cannot validate:
* Static fields or parameters
    * This is a limitation the Jakarta Bean Validation API

## `validation-extras` features

Everything in this library starts with the `Validations` class.
That keeps it easy to remember.

```
Validations.check(obj)                      — validates an object and returns violations
Validations.validate(obj)                   — validates an object and throws if there are violations
Validations.createMethodValidator(...)      — creates a validator for a method (more info below)
Validations.createConstructorValidator(...) — creates validator for a class constructor
Validations.violation(...)                  — used in custom constraints: helps creates violation messages
``````

Those are the basics. More details and examples are down below.

# Maven

This project provides two dependencies, one for Spring projects,
and one for "normal" non-Spring projects.
You only need to add one of them to your project.

## For normal (non-Spring) projects

Uses the latest `3.0.x` Bean Validation API.
```xml
<dependency>
    <groupId>com.terheyden</groupId>
    <artifactId>validation-extras</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
``````

## For Spring-based projects

Spring's validation support (as of 2022) still uses the `2.0.x` version of the Bean Validation API.
This version of `validation-extras` is compiled with those older Spring-compatible libraries.
```xml
<dependency>
    <groupId>com.terheyden</groupId>
    <artifactId>javax-validation-extras</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

# How to use

Spring and non-Spring usage is very different, because Spring provides a lot of validation support.

## Normal (Non-Spring) usage

### Validating fields or properties

1. Annotate your fields or getter properties (either is fine):

```java
public class Employee {

    private final UUID userId;

    @NotBlank
    private final String name;

    @Min(1)
    private final int age;

    // ...
}
```

2. Call `Validations.validate(obj)`:

```java
private void doSomeWork(Employee employee){
    Validations.validate(employee);
    // ...
}
```

### Validating method parameters

1. Create a static `MethodValidator` for each method that uses validating
2. Call `XXX.validate(this, param1, param2, ...)` at the beginning of each method:

```java
class MyClass {

    // Validator for the greet() method and its parameters.
    private static final MethodValidator GREET = Validations
        .createMethodValidator(MyClass.class, "greet");

    // ...

    private String greet(@NotBlank String greeting, @Valid @NotNull Employee employee) {

        GREET.validate(this, greeting, employee);
        return "%s, %s!".formatted(greeting, employee.getName());
    }
}
```

### Validating constructor parameters

TODO: Validations.findConstructorValidator(clazz)

## Usage From Spring

If you're using Spring Boot, you'll need to add validation support:

```xml
<!-- Add Jakarta Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Spring takes care of validating objects and method parameters for any Spring-aware classes.
* DTO-like classes with data have validation annotations as usual
* Service-like classes that expect valid data have a `@Validated` annotation
* `@Valid` on a parameter tells the validator to _recurse_ into that object

For example:

```java
@Validated
@RestController
public class MyController {

    @GetMapping
    public String greet(@NotBlank String greeting, @Valid @NotNull Employee employee) {
        return "%s, %s!".formatted(greeting, employee.getName());
    }
}
```


# Validation cookbook

### How to assign a constraint validator programmatically

Decorating your classes with something like `@NotNull` makes sense in a 'lib' or 'dto' layer.
However, validators might make more sense in a logic or 'service' layer.
So how do we decouple the two?

1. Create your constraint but leave the validator _empty_
   1. `@Constraint(validatedBy = { })`
2. Create a validator for your constraint as usual
3. Create the constraint metadata file
   1. (Normal projects) `src/main/resources/META-INF/services/jakarta.validation.ConstraintValidator`
   2. (Spring projects) `src/main/resources/META-INF/services/javax.validation.ConstraintValidator`
4. Add an entry to the metadata file for each dynamic validator you create

_Example `src/main/resources/META-INF/services/jakarta.validation.ConstraintValidator` file:_
```
# Just add your dynamic validators in here.
# The association to the right annotation will happen automatically.
com.terheyden.validation.constraints.NotNullValidator
```

Check out the [implementation docs](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-constraint-definition-contribution)
for more information.

### Easy copy-and-paste
```shell
# Normal project:
mkdir -pv src/main/resources/META-INF/services && touch src/main/resources/META-INF/services/jakarta.validation.ConstraintValidator
# Spring project:
mkdir -pv src/main/resources/META-INF/services && touch src/main/resources/META-INF/services/javax.validation.ConstraintValidator
```

Now, in your IDE, just search for the file and add to it.

# Performance considerations

Processing annotations might have you wondering about the performance impact of using
Jakarta Bean Validation or the validation-extras library.

In my JMH benchmarks, performing the validations and using this library
was significantly less expensive than a single `String.format()` call, and of course much,
much faster than a single `LOG.debug()` call.

Feel free to experiment with the benchmarks yourself; check out the `validation-benchmark`
module included in this repository.
