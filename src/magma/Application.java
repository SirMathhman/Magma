package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record Application(Path source) {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");

    void run() throws IOException {
        if (!Files.exists(source())) return;

        var fileName = source().getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        var name = fileName.substring(0, separator);
        Files.createFile(ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs"));
    }
}