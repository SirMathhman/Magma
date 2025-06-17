package magma;

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
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        final Sources sources = new PathSources(Paths.get(".", "src", "java"));
        handleResult(sources.readAll());
    }

    private static void handleResult(Result<Map<Source, String>, ApplicationError> result) {
        (switch (result) {
            case Ok(Map<Source, String> value) ->
                    ((Function<Map<Source, String>, Optional<ApplicationError>>) Main::compileAll).apply(value);
            case Err(ApplicationError error1) ->
                    ((Function<ApplicationError, Optional<ApplicationError>>) Optional::of).apply(error1);
            default -> null;
        })
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<ApplicationError> compileAll(Map<Source, String> inputs) {
        final Compiler compiler = new RuleCompiler(Lang.createJavaRootRule(), Lang.createPlantRootRule());
        return handleCompileResult(compiler.compile(inputs));
    }

    private static Optional<ApplicationError> handleCompileResult(Result<String, ApplicationError> result) {
        return switch (result) {
            case Ok(String value) -> ((Function<String, Optional<ApplicationError>>) currentOutput -> {
                final var target = Paths.get(".", "diagram.puml");
                final var content = "@startuml\nskinparam linetype ortho\n" + currentOutput + "@enduml";
                return writeString(target, content).map(ThrowableError::new)
                        .map(ApplicationError::new);
            }).apply(value);
            case Err(ApplicationError error) ->
                    ((Function<ApplicationError, Optional<ApplicationError>>) Optional::of).apply(error);
            default -> null;
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
