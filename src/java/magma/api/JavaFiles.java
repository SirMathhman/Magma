package magma.api;

import magma.app.ApplicationError;
import magma.app.ApplicationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JavaFiles {
    public static Optional<ApplicationError> writeString(Path path, CharSequence content) {
        try {
            Files.writeString(path, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ApplicationError(new ThrowableError(e)));
        }
    }

    public static ApplicationResult readString(Path source) {
        try {
            final var input = Files.readString(source);
            return new ApplicationResult.Ok(input);
        } catch (IOException e) {
            return new ApplicationResult.Err(new ApplicationError(new ThrowableError(e)));
        }
    }
}
