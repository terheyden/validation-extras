package com.terheyden.valid;

import java.io.IOException;

import org.openjdk.jmh.Main;

/**
 * Hello world.
 */
public final class BenchmarkApp {

    private BenchmarkApp() {
        // Private constructor since this shouldn't be instantiated.
    }

    public static void main(String... args) throws IOException {
        Main.main(args);
    }
}
