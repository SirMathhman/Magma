package magma;

import magma.Main.Err;
import magma.Main.Ok;
import magma.Main.Result;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class Files {
    static Optional<IOException> writeString(Path target, String output) {
        try {
            java.nio.file.Files.writeString(target, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(java.nio.file.Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }
}
