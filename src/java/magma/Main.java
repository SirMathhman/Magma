package magma;

import magma.api.io.PathLikes;
import magma.app.Application;
import magma.app.CompileApplication;
import magma.app.compile.Compiler;
import magma.app.compile.RuleCompiler;
import magma.app.io.sources.PathSources;
import magma.app.io.sources.Sources;
import magma.app.io.targets.PathTargets;
import magma.app.io.targets.Targets;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var rootDirectory = PathLikes.get(".", "src", "java");
        final Sources sources = new PathSources(rootDirectory);
        final Compiler compiler = new RuleCompiler();
        final Targets targets = new PathTargets(PathLikes.get(".", "diagram.puml"));

        final Application application = new CompileApplication(sources, compiler, targets);
        application.run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
