package com.meti;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
    public static final Path Target = Paths.get(".", "__index__.java");
    public static final Path Source = Paths.get(".", "index.ms");

    @Test
    void different_name() throws IOException {
        var otherSource = Paths.get(".", "test.ms");
        var otherTarget = otherSource.resolveSibling("__test__.java");

        new NIOPath(otherSource).ensureAsFile();
        runImpl(otherSource);
        assertTrue(Files.exists(otherTarget));

        Files.deleteIfExists(otherTarget);
        Files.deleteIfExists(otherSource);
    }

    private void runImpl(Path otherSource) {
        try {
            new Application(otherSource).run();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @Test
    void different_name_content() throws IOException {
        var otherSource = Paths.get(".", "test.ms");
        var otherTarget = otherSource.resolveSibling("__test__.java");

        new NIOPath(otherSource).ensureAsFile();
        runImpl(otherSource);
        assertEquals("class __test__{}", Files.readString(otherTarget));

        Files.deleteIfExists(otherTarget);
        Files.deleteIfExists(otherSource);
    }

    @Test
    void read_content() throws IOException {
        Files.writeString(Source, "import native Test from org.junit.jupiter.api;");
        runImpl(Source);

        assertEquals("import org.junit.jupiter.api.Test;class __index__{}", Files.readString(Target));
    }

    @Test
    void read_other_content() throws IOException {
        Files.writeString(Source, "import native IOException from java.io;");
        runImpl(Source);

        assertEquals("import java.io.IOException;class __index__{}", Files.readString(Target));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Target);
        Files.deleteIfExists(Source);
    }

    @Test
    void with_source() throws IOException {
        new NIOPath(Source).ensureAsFile();
        runImpl(Source);
        assertTrue(Files.exists(Target));
    }

    @Test
    void without_source() {
        runImpl(Source);
        assertFalse(Files.exists(Target));
    }

    @Test
    void write_content() throws IOException {
        new NIOPath(Source).ensureAsFile();
        runImpl(Source);

        assertEquals("class __index__{}", Files.readString(Target));
    }
}
