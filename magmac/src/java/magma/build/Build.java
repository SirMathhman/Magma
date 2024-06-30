package magma.build;

import java.nio.file.Path;

public record Build(BuildSet sourceDirectory, BuildSet targetDirectory, Path debugDirectory) {
}