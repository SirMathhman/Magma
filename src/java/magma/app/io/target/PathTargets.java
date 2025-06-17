package magma.app.io.target;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PathTargets implements Targets {
    public static Optional<IOException> writeString(Path path, CharSequence output) {
        try {
            Files.writeString(path, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    @Override
    public Optional<IOException> write(String output) {
        final var path = Paths.get(".", "diagram.puml");
        return writeString(path, output);
    }
}