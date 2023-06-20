package com.terheyden.valid;

import javax.annotation.Nullable;

import org.hibernate.validator.internal.engine.path.PathImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;

/**
 * If the object to validate is null, return a special "null violation" object.
 */
@SuppressWarnings("rawtypes")
final class NullOriginViolation implements ConstraintViolation {

    @Override
    public String getMessage() {
        return "Object to validate is null.";
    }

    @Override
    public String getMessageTemplate() {
        return getMessage();
    }

    @Override
    public Object getRootBean() {
        return this;
    }

    @Override
    public Class getRootBeanClass() {
        return NullOriginViolation.class;
    }

    @Override
    public Object getLeafBean() {
        return this;
    }

    @Override
    @Nullable
    public Object[] getExecutableParameters() {
        return null;
    }

    @Override
    @Nullable
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Path getPropertyPath() {
        return PathImpl.createPathFromString(ValidUtils.EMPTY_STR);
    }

    @Override
    @Nullable
    public Object getInvalidValue() {
        return null;
    }

    @Override
    @Nullable
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public Object unwrap(Class type) {
        return this;
    }
}
