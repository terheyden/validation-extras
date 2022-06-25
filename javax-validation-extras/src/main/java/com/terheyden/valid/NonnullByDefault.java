package com.terheyden.valid;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Declares the following defaults for the entire package:
 * Methods should not return null.
 * Method params are not null.
 * Fields should never be set to null.
 * <p>
 * Local vars can be null, sure, that's internal biz.
 * <p>
 * This code was copied and modified from {@link ParametersAreNonnullByDefault}.
 * <p>
 * @see
 * <a href="https://stackoverflow.com/questions/13310994/set-findbugs-notnull-as-default-for-all-classes-under-a-package">Stack Overflow (1)</a>
 * <a href="https://stackoverflow.com/questions/3550292/what-do-java-annotation-elementtype-constants-mean">Stack Overflow (2)</a>
 */
@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NonnullByDefault {
    // No code goes here.
}
