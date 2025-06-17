package magma;

import magma.api.Error;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.Lang;
import magma.app.compile.RuleCompiler;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;
import magma.app.io.PathSources;
import magma.app.io.Source;
import magma.app.io.Sources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final Sources sources = new PathSources(Paths.get(".", "src", "java"));
        handleResult(sources.readAll());
    }

    private static void handleResult(Result<Map<Source, String>, Error> result) {
        (switch (result) {
            case Ok(Map<Source, String> value) -> compileAll(value);
            case Err(Error error1) -> Optional.of(error1);
        }).ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<Error> compileAll(Map<Source, String> inputs) {
        final Compiler compiler = new RuleCompiler(Lang.createJavaRootRule(), Lang.createPlantRootRule());
        return handleCompileResult(compiler.compile(inputs));
    }

    private static Optional<Error> handleCompileResult(Result<String, Error> result) {
        return switch (result) {
            case Ok(String value) -> {
                final var target = Paths.get(".", "diagram.puml");
                final var content = "@startuml\nskinparam linetype ortho\n" + value + "@enduml";
                yield writeString(target, content).map(ThrowableError::new)
                        .map(ApplicationError::new);
            }
            case Err(Error error) -> Optional.of(error);
        };
    }

    private static Optional<IOException> writeString(Path target, CharSequence content) {
        try {
            Files.writeString(target, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

}
