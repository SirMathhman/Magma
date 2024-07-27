package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record DirectoryTargetSet(Path root) implements TargetSet {
    public static final String MAGMA_EXTENSION = "mgs";

    @Override
    public void writeTarget(Source source) throws IOException {
        var name = source.computeName();
        var parent = source.streamNamespace().reduce(root(), Path::resolve, (previous, next) -> next);
        if (!Files.exists(parent)) Files.createDirectories(parent);

        Files.createFile(parent.resolve(name + PathSource.EXTENSION_SEPARATOR + MAGMA_EXTENSION));
    }
}