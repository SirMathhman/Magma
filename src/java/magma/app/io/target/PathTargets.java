package magma.app.io.target;

import magma.api.io.Paths;
import magma.api.option.Option;

import java.io.IOException;

public class PathTargets implements Targets {
    @Override
    public Option<IOException> write(String output) {
        final var path = Paths.get(".", "diagram.puml");
        return path.writeString(output);
    }
}