package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Utility to copy its own class file to another location.
 */
public class SelfReplicator {

    /**
     * Copies this class's compiled bytecode to the given destination.
     *
     * @param destination path of the file to write
     * @throws IOException if the copy fails
     */
    public static void copySelf(Path destination) throws IOException {
        try (InputStream in = SelfReplicator.class.getResourceAsStream("SelfReplicator.class")) {
            if (in == null) {
                throw new IOException("Could not locate class file");
            }
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java SelfReplicator <destination.ts>");
            return;
        }
        copySelf(Path.of(args[0]));
    }
}
