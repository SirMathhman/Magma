package magma;

import magma.api.io.path.PathLikes;
import magma.app.Application;
import magma.app.io.PathSources;
import magma.app.io.Sources;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final Sources sources = new PathSources(PathLikes.get(".", "src", "java"));
        Application.run(sources)
                .ifPresent(error -> System.err.println(error.display()));
    }
}
