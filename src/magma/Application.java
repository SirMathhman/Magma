package magma;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public final class Application {
    public static final String MAGMA_EXTENSION = "mgs";
    private final SourceSet sourceSet;
    private final TargetSet targetSet;

    public Application(SourceSet sourceSet, TargetSet targetSet) {
        this.sourceSet = sourceSet;
        this.targetSet = targetSet;
    }

    private void runWithPath(CompileUnit unit) throws ApplicationException {
        try {
            var input = unit.read();
            var output = Compiler.compile(input);
            targetSet.writeTarget(unit, output);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    void run() throws ApplicationException {
        var unit = streamAndCollect();

        for (var path : unit) {
            runWithPath(path);
        }
    }

    private Set<CompileUnit> streamAndCollect() throws ApplicationException {
        try {
            return sourceSet
                    .streamPaths()
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}