package magma;

import magma.app.Transpiler;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

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

        return files.value().get().iterator().fold(
            new None<String>(),
            (Option<String> acc, PathLike file) -> {
                if (acc.isSome()) {
                    return acc;
                }
                var err = transpileFile(srcRoot, outRoot, file);
                return err.isSome() ? err : acc;
            });
    }

    private Result<ListLike<PathLike>> listJavaFiles(PathLike srcRoot) {
        var paths = srcRoot.walk();
        if (!paths.isOk()) {
            return new Err<>(paths.error().get());
        }
        ListLike<PathLike> javaFiles = JdkList.create();
        var pathIt = paths.value().get().iterator();
        while (pathIt.hasNext()) {
            var p = pathIt.next();
            if (p.toString().endsWith(".java")) {
                javaFiles.add(p);
            }
        }
        return new Ok<>(javaFiles);
    }

    private Option<String> transpileFile(PathLike srcRoot, PathLike outRoot, PathLike javaFile) {
        var javaSrcResult = javaFile.readString();
        if (!javaSrcResult.isOk()) {
            return new Some<>(javaSrcResult.error().get());
        }
        var javaSrc = javaSrcResult.value().get();
        var ts = new Transpiler().toTypeScript(javaSrc);
        var rel = srcRoot.relativize(javaFile);
        var name = rel.toString();
        var withoutExt = name.substring(0, name.length() - 5);
        var outFile = outRoot.resolve(withoutExt + ".ts");
        var err = outFile.getParent().createDirectories();
        if (err.isSome()) {
            return err;
        }
        err = outFile.writeString(ts + System.lineSeparator());
        return err;
    }
}
