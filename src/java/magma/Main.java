package magma;

import magma.api.error.WrappedError;
import magma.api.io.PathLikes;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.Compiler;
import magma.app.compile.RuleCompiler;
import magma.app.compile.lang.Lang;
import magma.app.io.sources.PathSources;
import magma.app.io.sources.Sources;
import magma.app.io.targets.PathTargets;
import magma.app.io.targets.Targets;

import java.util.Map;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var rootDirectory = PathLikes.get(".", "src", "java");
        final Sources sources = new PathSources(rootDirectory);
        final Compiler compiler = new RuleCompiler();
        final Targets targets = new PathTargets(PathLikes.get(".", "diagram.puml"));

        sources.collect()
                .match(inputs -> Main.compileAndWrite(inputs, compiler, targets), Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<WrappedError> compileAndWrite(final Map<String, String> inputs, final Compiler compiler, final Targets targets) {
        return compiler.compile(inputs)
                .mapErr(WrappedError::new)
                .match(compiled -> Main.writeTarget(compiled, targets), Some::new);
    }

    private static Option<WrappedError> writeTarget(final String compiled, final Targets targets) {
        final var output = String.join(Lang.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");
        return targets.write(output);
    }
}
