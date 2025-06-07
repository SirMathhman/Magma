package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import magma.Main;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void buildsFilesUnderSourceDirectory() throws IOException {
        Path javaDir = Paths.get("src/main/java/temp");
        Files.createDirectories(javaDir);
        Path javaFile = javaDir.resolve("A.java");
        Files.writeString(javaFile, "package temp; public class A {}");

        Main.main(new String[0]);

        Path tsFile = Paths.get("src/main/node/temp/A.ts");
        String ts = Files.readString(tsFile);
        assertEquals("export default class A {}" + System.lineSeparator(), ts);

        deleteTree(Paths.get("src/main/node"));
        deleteTree(Paths.get("src/main/java/temp"));
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        java.util.List<Path> paths = new java.util.ArrayList<>();
        try (var stream = Files.walk(root)) {
            stream.forEach(paths::add);
        }
        for (int i = paths.size() - 1; i >= 0; i--) {
            Files.deleteIfExists(paths.get(i));
        }
    }
}
