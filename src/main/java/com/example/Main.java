package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple command line interface for the Transpiler.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Path srcRoot = Path.of("src/main/java");
        Path outRoot = Path.of("src/main/node");

        List<Path> javaFiles = new ArrayList<>();
        try (var stream = Files.walk(srcRoot)) {
            stream.forEach(p -> {
                if (p.toString().endsWith(".java")) {
                    javaFiles.add(p);
                }
            });
        }

        for (Path file : javaFiles) {
            transpileFile(srcRoot, outRoot, file);
        }
    }

    private static void transpileFile(Path srcRoot, Path outRoot, Path javaFile) throws IOException {
        String javaSrc = Files.readString(javaFile);
        String ts = new Transpiler().toTypeScript(javaSrc);
        Path rel = srcRoot.relativize(javaFile);
        String name = rel.toString();
        String withoutExt = name.substring(0, name.length() - 5);
        Path outFile = outRoot.resolve(withoutExt + ".ts");
        Files.createDirectories(outFile.getParent());
        Files.writeString(outFile, ts + System.lineSeparator());
    }
}
