package com.terheyden.valid;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

/**
 * GreetingDto class.
 */
public class GreetingDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private final String salutation;

    @NotBlank
    private final String name;

    public GreetingDto(String salutation, String name) {
        this.salutation = salutation;
        this.name = name;
    }

    public GreetingDto(String name) {
        this.salutation = "Hello";
        this.name = name;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getName() {
        return name;
    }
}
