package magma;

import magma.api.error.WrappedError;
import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.io.PathLikes;
import magma.api.list.ListLike;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.Compiler;
import magma.app.Lang;

import java.util.HashMap;
import java.util.Map;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        PathLikes.get(".", "src", "java")
                .walk()
                .match(Main::runWithFiles, Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<WrappedError> runWithFiles(final ListLike<PathLike> files) {
        final var sources = files.stream()
                .filter(file -> file.asString()
                        .endsWith(".java"))
                .toList();

        return Main.compileAll(sources)
                .match(Main::writeTarget, Some::new);
    }

    private static Option<WrappedError> writeTarget(final String compiled) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var output = String.join(Lang.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");
        return target.writeString(output)
                .map(WrappedError::new);
    }

    private static Result<String, WrappedError> compileAll(final Iterable<PathLike> sources) {
        return Main.readAll(sources)
                .mapErr(WrappedError::new)
                .flatMapValue(inputs -> Compiler.compileEntryToResult(inputs)
                        .mapErr(WrappedError::new));
    }

    private static Result<Map<String, String>, IOError> readAll(final Iterable<PathLike> sources) {
        Result<Map<String, String>, IOError> maybeInputs = new Ok<>(new HashMap<>());
        for (final var source : sources)
            maybeInputs = Main.attachEntry(source, maybeInputs);
        return maybeInputs;
    }

    private static Result<Map<String, String>, IOError> attachEntry(final PathLike source, final Result<Map<String, String>, IOError> maybeInputs) {
        final var fileName = source.getFileName()
                .asString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return maybeInputs.flatMapValue(inputs -> source.readString()
                .mapValue(input -> {
                    inputs.put(name, input);
                    return inputs;
                }));
    }

}
