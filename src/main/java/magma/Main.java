package magma;

import magma.app.Transpiler;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

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
        var error = new Main().run();
        if (error.isSome()) {
            System.err.println(error.get());
        }
    }

    private Option<String> run() {
        var srcRoot = Path.of("src/main/java");
        var outRoot = Path.of("src/main/node");

        var files = listJavaFiles(srcRoot);
        if (!files.isOk()) {
            return new Some<>(files.error().get());
        }

        for (var file : files.value().get()) {
            var err = transpileFile(srcRoot, outRoot, file);
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
            var javaSrc = Files.readString(javaFile);
            var ts = new Transpiler().toTypeScript(javaSrc);
            var rel = srcRoot.relativize(javaFile);
            var name = rel.toString();
            var withoutExt = name.substring(0, name.length() - 5);
            var outFile = outRoot.resolve(withoutExt + ".ts");
            Files.createDirectories(outFile.getParent());
            Files.writeString(outFile, ts + System.lineSeparator());
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e.getMessage());
        }
    }
}
