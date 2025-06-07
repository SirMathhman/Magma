package com.example;

import com.example.app.Transpiler;
import com.example.option.None;
import com.example.option.Option;
import com.example.option.Some;
import com.example.result.Err;
import com.example.result.Ok;
import com.example.result.Result;

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
            return new Some<>(files.error().get());
        }

        for (Path file : files.value().get()) {
            Option<String> err = transpileFile(srcRoot, outRoot, file);
            if (err.isSome()) {
                return err;
            }
        }
        return new None<>();
    }

    private Result<List<Path>> listJavaFiles(Path srcRoot) {
        List<Path> javaFiles = new ArrayList<>();
        try (var stream = Files.walk(srcRoot)) {
            stream.forEach(p -> {
                if (p.toString().endsWith(".java")) {
                    javaFiles.add(p);
                }
            });
            return new Ok<>(javaFiles);
        } catch (IOException e) {
            return new Err<>(e.getMessage());
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
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e.getMessage());
        }
    }
}
