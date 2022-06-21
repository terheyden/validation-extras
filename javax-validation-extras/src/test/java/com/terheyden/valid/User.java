package com.terheyden.valid;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

/**
 * Example user class.
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Null is considered valid.
     */
    private final boolean isValid = true;

    private final boolean isInvalid = false;

    private final UUID id;

    /**
     * \@Size works on Strings, arrays, and Collections.
     * Null is considered valid for some reason.
     */
    @Length
    private final String name;

    private final int age;

    private final String[] hobbies = new String[] { "running", "sleeping" };

    private final String blackHole = null;

    public User(UUID id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    /**
     * Create a new User with a new random ID.
     */
    public User(String name, int age) {
        this(UUID.randomUUID(), name, age);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
