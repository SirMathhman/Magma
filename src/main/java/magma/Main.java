package magma;

import magma.app.Transpiler;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;

import magma.path.NioPath;
import magma.path.PathLike;
import magma.list.JdkList;
import magma.list.ListLike;

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
        PathLike srcRoot = NioPath.of("src/main/java");
        PathLike outRoot = NioPath.of("src/main/node");

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

    private Result<ListLike<PathLike>> listJavaFiles(PathLike srcRoot) {
        var paths = srcRoot.walk();
        if (!paths.isOk()) {
            return new Err<>(paths.error().get());
        }
        ListLike<PathLike> javaFiles = JdkList.create();
        for (var p : paths.value().get()) {
            if (p.toString().endsWith(".java")) {
                javaFiles.add(p);
            }
        }
        return new Ok<>(javaFiles);
    }

    private Option<String> transpileFile(PathLike srcRoot, PathLike outRoot, PathLike javaFile) {
        try {
            var javaSrc = ((NioPath) javaFile).readString();
            var ts = new Transpiler().toTypeScript(javaSrc);
            var rel = srcRoot.relativize(javaFile);
            var name = rel.toString();
            var withoutExt = name.substring(0, name.length() - 5);
            var outFile = outRoot.resolve(withoutExt + ".ts");
            ((NioPath) outFile.getParent()).createDirectories();
            ((NioPath) outFile).writeString(ts + System.lineSeparator());
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e.getMessage());
        }
    }
}
