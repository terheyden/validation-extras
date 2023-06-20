package com.terheyden.valid;

/**
 * SelfValidating interface.
 */
public interface SelfValidating {

    /**
     * Validate this object's state. Throws if the state is invalid.
     * Call this during construction after all fields are set, or after any setting change.
     * Depends on: {@link Validations#validateObject(Object)}
     */
    default void validateSelf() {
        Validations.validateObject(this);
    }
}
