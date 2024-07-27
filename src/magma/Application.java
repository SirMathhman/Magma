package magma;

import java.io.IOException;
import java.util.stream.Collectors;

public record Application(SourceSet sourceSet, TargetSet targetSet) {
    void run() throws IOException {
        var set = sourceSet().walk().collect(Collectors.toSet());

        for (var path : set) {
            targetSet().writeTarget(path);
        }
    }
}