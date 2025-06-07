package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class SelfReplicatorTest {

    private byte[] readResource() throws IOException {
        try (InputStream in = SelfReplicator.class.getResourceAsStream("SelfReplicator.class")) {
            if (in == null) {
                throw new IOException("Could not locate class file");
            }
            return in.readAllBytes();
        }
    }

    @Test
    void copiesItselfToDestination() throws IOException {
        Path tempFile = Files.createTempFile("replica", ".class");
        SelfReplicator.copySelf(tempFile);
        byte[] original = readResource();
        byte[] copy = Files.readAllBytes(tempFile);
        assertArrayEquals(original, copy);
    }
}
