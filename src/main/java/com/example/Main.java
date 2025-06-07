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
    public static void main(String[] args) {
        Option<String> error = new Main().run();
        if (error.isSome()) {
            System.err.println(error.get());
        }
    }

    private Option<String> run() {
        Path srcRoot = Path.of("src/main/java");
        Path outRoot = Path.of("src/main/node");

        Result<List<Path>> files = listJavaFiles(srcRoot);
        if (!files.isOk()) {
            return Option.some(files.error().get());
        }

        for (Path file : files.value().get()) {
            Option<String> err = transpileFile(srcRoot, outRoot, file);
            if (err.isSome()) {
                return err;
            }
        }
        return Option.none();
    }

    private Result<List<Path>> listJavaFiles(Path srcRoot) {
        List<Path> javaFiles = new ArrayList<>();
        try (var stream = Files.walk(srcRoot)) {
            stream.forEach(p -> {
                if (p.toString().endsWith(".java")) {
                    javaFiles.add(p);
                }
            });
            return Result.ok(javaFiles);
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }

    private Option<String> transpileFile(Path srcRoot, Path outRoot, Path javaFile) {
        try {
            String javaSrc = Files.readString(javaFile);
            String ts = new Transpiler().toTypeScript(javaSrc);
            Path rel = srcRoot.relativize(javaFile);
            String name = rel.toString();
            String withoutExt = name.substring(0, name.length() - 5);
            Path outFile = outRoot.resolve(withoutExt + ".ts");
            Files.createDirectories(outFile.getParent());
            Files.writeString(outFile, ts + System.lineSeparator());
            return Option.none();
        } catch (IOException e) {
            return Option.some(e.getMessage());
        }
    }
}
