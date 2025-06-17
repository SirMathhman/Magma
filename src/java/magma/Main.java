package magma;

import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.Lang;
import magma.app.compile.RuleCompiler;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;
import magma.app.io.PathSources;
import magma.app.io.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        handleResult(new PathSources(Paths.get(".", "src", "java")).readAll());
    }

    private static void handleResult(Result<Map<Source, String>, ApplicationError> result) {
        result.match(Main::compileAll, Optional::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<ApplicationError> compileAll(Map<Source, String> inputs) {
        final Compiler compiler = new RuleCompiler(Lang.createJavaRootRule(), Lang.createPlantRootRule());
        return handleCompileResult(compiler.compile(inputs));
    }

    private static Optional<ApplicationError> handleCompileResult(Result<String, ApplicationError> result) {
        return result.match(currentOutput -> {
            final var target = Paths.get(".", "diagram.puml");
            final var content = "@startuml\nskinparam linetype ortho\n" + currentOutput + "@enduml";
            return writeString(target, content).map(ThrowableError::new)
                    .map(ApplicationError::new);
        }, Optional::of);
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
