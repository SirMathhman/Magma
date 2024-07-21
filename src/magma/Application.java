package magma;

import java.io.IOException;
import java.util.stream.Collectors;

public final class Application {
    public static final String MAGMA_EXTENSION = "mgs";
    private final SourceSet sourceSet;
    private final TargetSet targetSet;

    public Application(SourceSet sourceSet, TargetSet targetSet) {
        this.sourceSet = sourceSet;
        this.targetSet = targetSet;
    }

    private void runWithPath(CompileUnit unit) throws IOException {
        targetSet.writeTarget(unit);
    }

    void run() throws IOException {
        var unit = sourceSet
                .streamPaths()
                .collect(Collectors.toSet());

        for (var path : unit) {
            runWithPath(path);
        }
    }
}