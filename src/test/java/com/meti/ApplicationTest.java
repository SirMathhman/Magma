package com.meti;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
    private static final Path Source = Paths.get(".", "index.mgs");
    private static final Path Target = Paths.get(".", "index.c");

    @Test
    void empty_content() throws IOException {
        Files.createFile(Source);
        Application.run(Source);
        assertEquals(Files.readString(Source), "");
    }

    @Test
    void exists() throws IOException {
        Files.createFile(Source);
        Application.run(Source);
        assertTrue(Files.exists(Target));
    }

    @Test
    void not_exists() throws IOException {
        Application.run(Source);
        assertFalse(Files.exists(Target));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Target);
        Files.deleteIfExists(Source);
    }
}
