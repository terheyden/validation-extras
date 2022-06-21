# validation-extras — Helper classes for working with Jakarta Bean Validation

# What is this?

Jakarta Bean Validation is a framework for validating fields and objects _declaratively_.
For example:

```java
// Example without Validation:
public void saveEmployee(Employee employee) {

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

It's cleaner to separate the validation logic from the business logic:

```java
// Same example with Validation:
public void saveEmployee(Employee employee) {

    Validations.validate(employee);
    LOG.info("Saving employee: {}", employee);
    // ...
}
```

This library makes it easier to work with Jakarta Bean Validation,
most notably when you want to use it outside of the Spring framework.

# Maven

### For normal (non-Spring) projects
```xml
<dependency>
    <groupId>com.terheyden</groupId>
    <artifactId>validation-extras</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
``````

### For Spring-based projects

As of 2022, Spring still integrates with the older `javax` validation namespace.
```xml
<dependency>
    <groupId>com.terheyden</groupId>
    <artifactId>javax-validation-extras</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

# Normal (Non-Spring) Usage

Note that validating static fields or parameters is not supported.

## Validating fields or properties

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

## Validating method parameters

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

## Validating constructor parameters

TODO: Validations.findConstructorValidator(clazz)

# Usage From Spring

Note that validating static fields or parameters is not supported.

## Validating fields or properties

## Validating method parameters

## Validating constructor parameters

# Performance considerations

Processing annotations might have you wondering about the performance impact of using
Jakarta Bean Validation or the validation-extras library.

In my JMH benchmarks, performing the validations and using this library
was significantly less expensive than a single `String.format()` call, and of course much,
much faster than a single `LOG.debug()` call.

Feel free to experiment with the benchmarks yourself; check out the `validation-benchmark`
module included in this repository.
