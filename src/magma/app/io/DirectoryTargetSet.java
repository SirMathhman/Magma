package magma.app.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public record DirectoryTargetSet(Path root) implements TargetSet {
    public static final String MAGMA_EXTENSION = "mgs";

    private void writeTarget0(Source source, String output) throws IOException {
        var name = source.computeName();
        var parent = source.streamNamespace().reduce(root(), Path::resolve, (previous, next) -> next);
        if (!Files.exists(parent)) Files.createDirectories(parent);

        Files.createFile(parent.resolve(name + PathSource.EXTENSION_SEPARATOR + MAGMA_EXTENSION));
    }

    @Override
    public Optional<IOException> writeTarget(Source source, String output) {
        try {
            writeTarget0(source, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }
}