package magma.app;

import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public record Application(Path source) {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");

    public void run() throws ApplicationException {
        try {
            if (!Files.exists(source)) return;

            var input = Files.readString(source);
            var result = Compiler.compile(input)
                    .mapValue(this::writeValue)
                    .match(value -> value, Optional::of);

            if (result.isPresent()) {
                throw result.get();
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private Optional<ApplicationException> writeValue(String value) {
        try {
            var fileName = source.getFileName().toString();
            var separator = fileName.lastIndexOf('.');
            var name = fileName.substring(0, separator);
            var target = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs");
            Files.writeString(target, value);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ApplicationException(e));
        }
    }

}