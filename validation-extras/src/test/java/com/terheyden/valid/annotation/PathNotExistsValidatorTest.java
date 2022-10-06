package com.terheyden.valid.annotation;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.Validations;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * PathExistsValidatorTest unit tests.
 */
public class PathNotExistsValidatorTest {

    private static final Logger LOG = getLogger(PathNotExistsValidatorTest.class);

    private static final MethodValidator PATHS = Validations
        .createMethodValidator(PathNotExistsValidatorTest.class, "pathTester");

    private static final MethodValidator FILES = Validations
        .createMethodValidator(PathNotExistsValidatorTest.class, "fileTester");

    private final Path goodPath = Paths
        .get("src/test/java/com/terheyden/valid/annotation/PathExistsValidatorTest.java");

    private final Path badPath = Paths.get("src/test/java/com/terheyden/valid/annotation/DoesNotExist.java");
    private final File goodFile = goodPath.toFile();
    private final File badFile = badPath.toFile();

    @Test
    public void test() {

        // Test that the bad paths pass.
        PATHS.validateParams(this, badPath);
        FILES.validateParams(this, badFile);

        // Test that the good paths throw.
        assertThrows(ConstraintViolationException.class, () -> PATHS.validateParams(this, goodPath));
        assertThrows(ConstraintViolationException.class, () -> FILES.validateParams(this, goodFile));
    }

    public void pathTester(@PathNotExists Path path) {
        LOG.debug("{}", path);
    }

    public void fileTester(@PathNotExists File file) {
        LOG.debug("{}", file);
    }
}
