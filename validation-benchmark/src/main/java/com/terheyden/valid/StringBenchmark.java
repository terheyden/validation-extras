package com.terheyden.valid;

import java.util.Random;
import java.util.UUID;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * StringBenchmark class.
 */
@Fork(value = 3, warmups = 3) // 3 forked JVM, 3 warmup iterations
@BenchmarkMode(Mode.Throughput) // Measure throughput
@Warmup(iterations = 3, time = 3) // Warmup 3 times, 3 secs each
@Measurement(iterations = 3, time = 3) // Measure 3 times, 3 secs each
public class StringBenchmark {

    @State(Scope.Benchmark)
    public static class RandomStringGenerator {

        private static final Random RANDOM = new Random();

        public String generate() {
            return "%s%s%s%s".formatted(
                RANDOM.nextInt(),
                RANDOM.nextInt(),
                RANDOM.nextInt(),
                RANDOM.nextInt());
        }

        public UUID generateUUID() {
            return UUID.randomUUID();
        }
    }

    @State(Scope.Benchmark)
    public static class User {

        @NotNull
        private final UUID userId;
        @NotBlank
        private final String name;

        public User(UUID userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }
    }

    @Benchmark
    public String stringFormat(RandomStringGenerator generator) {
        return String.format("Hello, %s", generator.generate());
    }

    @Benchmark
    public String stringPlus(RandomStringGenerator generator) {
        return "Hello, " + generator.generate();
    }

    @Benchmark
    public UUID generateUUID(RandomStringGenerator generator) {
        return generator.generateUUID();
    }

    @Benchmark
    public void systemPrintln(RandomStringGenerator generator) {
        System.out.println("Hello, " + generator.generate());
    }

    @Benchmark
    public User createUsers(RandomStringGenerator generator) {
        return new User(generator.generateUUID(), generator.generate());
    }

    @Benchmark
    public User createGoodUsers(RandomStringGenerator generator) {

        User user = new User(generator.generateUUID(), generator.generate());

        if (user.getUserId() == null) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Invalid name.");
        }

        return user;
    }

    @Benchmark
    public User createValidUsers(RandomStringGenerator generator) {

        User user = new User(generator.generateUUID(), generator.generate());
        Validations.validate(user);
        return user;
    }

}
