package com.terheyden.valid.annotation;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.Validations;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * PathExistsValidatorTest unit tests.
 */
public class IsRegularFileValidatorTest {

    private static final Logger LOG = getLogger(IsRegularFileValidatorTest.class);

    private static final MethodValidator PATHS = Validations
        .createMethodValidator(IsRegularFileValidatorTest.class, "pathTester");

    private static final MethodValidator FILES = Validations
        .createMethodValidator(IsRegularFileValidatorTest.class, "fileTester");

    private final Path goodPath = Paths
        .get("src/test/java/com/terheyden/valid/annotation/IsRegularFileValidatorTest.java");

    private final Path badPath = Paths.get("src/test/java/com/terheyden/valid/annotation");
    private final File goodFile = goodPath.toFile();
    private final File badFile = badPath.toFile();

    @Test
    public void test() {

        // Test the good paths.
        PATHS.validateParams(this, goodPath);
        FILES.validateParams(this, goodFile);

        // Test that the bad paths throw.
        assertThrows(ConstraintViolationException.class, () -> PATHS.validateParams(this, badPath));
        assertThrows(ConstraintViolationException.class, () -> FILES.validateParams(this, badFile));
    }

    public void pathTester(@IsRegularFile Path path) {
        LOG.debug("{}", path);
    }

    public void fileTester(@IsRegularFile File file) {
        LOG.debug("{}", file);
    }
}
