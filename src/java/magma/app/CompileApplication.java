package magma.app;

import magma.api.error.WrappedError;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.Compiler;
import magma.app.io.sources.Sources;
import magma.app.io.targets.Targets;

import java.util.Map;

public record CompileApplication(Sources sources, Compiler compiler, Targets targets) implements Application {
    private static Option<WrappedError> compileAndWrite(final Map<String, String> inputs, final Compiler compiler, final Targets targets) {
        return compiler.compile(inputs)
                .mapErr(WrappedError::new)
                .match(compiled -> CompileApplication.writeTarget(compiled, targets), Some::new);
    }

    private static Option<WrappedError> writeTarget(final String compiled, final Targets targets) {
        final var output = String.join(System.lineSeparator(),
                "@startuml",
                "skinparam linetype ortho",
                compiled,
                "@enduml");
        return targets.write(output);
    }

    @Override
    public Option<WrappedError> run() {
        return this.sources()
                .collect()
                .match(inputs -> CompileApplication.compileAndWrite(inputs, this.compiler(), this.targets()),
                        Some::new);
    }
}