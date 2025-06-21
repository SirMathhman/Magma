package magma.app;

import magma.api.io.IOError;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

public record CompileApplication(Sources sources, Targets targets) implements Application {

    @Override
    public OptionalLike<IOError> run() {
        return this.sources.readSourceSet()
                .mapValue(Compiler::compileEntries)
                .match(this::write, Optionals::of);
    }

    private OptionalLike<IOError> write(final String output) {
        return this.targets.write(String.join(Lang.SEPARATOR,
                "@startuml",
                "skinparam linetype ortho",
                output,
                "@enduml"));
    }
}