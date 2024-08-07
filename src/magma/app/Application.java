package magma.app;

import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class Application {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");
    private final SourceSet sourceSet;
    private final TargetSet targetSet;

    public Application(SourceSet sourceSet, TargetSet targetSet) {
        this.sourceSet = sourceSet;
        this.targetSet = targetSet;
    }

    public void run() throws ApplicationException {
        try {
            var set = sourceSet.collectSources();

            for (var unit : set) {
                var input = unit.read();
                var result = Compiler.compile(unit, input)
                        .mapValue(value1 -> targetSet.writeValue(unit, value1))
                        .match(value -> value, Optional::of);

                if (result.isPresent()) throw result.get();
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}