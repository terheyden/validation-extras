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
public class PathExistsValidatorTest {

    private static final Logger LOG = getLogger(PathExistsValidatorTest.class);

    private static final MethodValidator PATHS = Validations
        .createMethodValidator(PathExistsValidatorTest.class, "pathTester");

    private static final MethodValidator FILES = Validations
        .createMethodValidator(PathExistsValidatorTest.class, "fileTester");

    private final Path goodPath = Paths
        .get("src/test/java/com/terheyden/valid/annotation/PathExistsValidatorTest.java");

    private final Path badPath = Paths.get("src/test/java/com/terheyden/valid/annotation/DoesNotExist.java");
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

        // Test this obj as a whole, which will test the method annotations.
        Validations.validate(this);
    }

    public void pathTester(@PathExists Path path) {
        LOG.debug("{}", path);
    }

    public void fileTester(@PathExists File file) {
        LOG.debug("{}", file);
    }

    @PathExists
    public Path getGoodPath() {
        return goodPath;
    }

    @PathExists
    public File getGoodFile() {
        return goodFile;
    }
}
