package magma.app.io.target;

import magma.api.io.Paths;

import java.io.IOException;
import java.util.Optional;

public class PathTargets implements Targets {
    @Override
    public Optional<IOException> write(String output) {
        final var path = Paths.get(".", "diagram.puml");
        return path.writeString(output);
    }
}