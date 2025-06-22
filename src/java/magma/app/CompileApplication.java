package magma.app;

import magma.api.optional.Option;
import magma.api.optional.Optionals;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;
import magma.app.compile.result.StringResult;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

public record CompileApplication(Sources sources, Targets targets) implements Application {
    @Override
    public Option<ApplicationError> run() {
        return this.sources.readSourceSet()
                .mapErr(ApplicationError::new)
                .mapValue(Compiler::compileEntries)
                .match(this::writeResult, Optionals::of);
    }

    private Option<ApplicationError> writeResult(final StringResult output) {
        return output.toResult()
                .mapErr(ApplicationError::new)
                .match(this::write, Optionals::of);
    }

    private Option<ApplicationError> write(final String output) {
        return this.targets.write(String.join(Lang.SEPARATOR,
                        "@startuml",
                        "skinparam linetype ortho",
                        output,
                        "@enduml"))
                .map(ApplicationError::new);
    }
}