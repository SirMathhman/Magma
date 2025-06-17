package magma;

import magma.api.io.Paths;
import magma.app.Application;
import magma.app.compile.CompilerImpl;
import magma.app.io.source.PathSources;
import magma.app.io.target.PathTargets;

public class Main {
    public static void main(String[] args) {
        final var sources = new PathSources(Paths.get(".", "src", "java"));
        final var compiler = new CompilerImpl();
        final var targets = new PathTargets();
        final var application = new Application(sources, compiler, targets);
        final var result = application.run();
        result.ifPresent(Throwable::printStackTrace);
    }
}
