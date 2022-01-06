package com.meti;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {
    private static final Path Source = NIOPath.Root.resolveChild("index.mg");
    private static final Path Target = NIOPath.Root.resolveChild("index.c");

    @Test
    void test() {
        assertFalse(Files.exists(Target));
    }

    @Test
    void test1() throws IOException {
        Files.createFile(Source);
        if (Files.exists(Source)) {
            Files.createFile(Target);
        }

        assertTrue(Files.exists(Target));
    }
}
