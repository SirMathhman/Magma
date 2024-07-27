package magma;

import java.nio.file.Path;

public record PathSource(Path source) implements Source {
    public static final String EXTENSION_SEPARATOR = ".";

    @Override
    public String computeName() {
        var fileName = source().getFileName().toString();
        var separator = fileName.indexOf(EXTENSION_SEPARATOR);
        return fileName.substring(0, separator);
    }
}