package magma;

import magma.api.io.path.PathLikes;
import magma.app.Application;
import magma.app.CompileApplication;
import magma.app.io.source.PathSources;
import magma.app.io.source.Sources;
import magma.app.io.target.PathTargets;
import magma.app.io.target.Targets;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = PathLikes.get(".", "src", "java");
        final var targetRoot = PathLikes.get(".", "diagram.puml");

        final Sources sources = new PathSources(sourceRoot);
        final Targets targets = new PathTargets(targetRoot);
        final Application application = new CompileApplication(sources, targets);
        application.run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
