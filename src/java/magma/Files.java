package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Files {
    record ExceptionalIOError(IOException exception) implements Main.IOError {
        @Override
        public String display() {
            StringWriter writer = new StringWriter();
            this.exception.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    static Optional<Main.IOError> writeString(Main.Path_ target, String output) {
        try {
            Path first = unwrap(target);
            java.nio.file.Files.writeString(first, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ExceptionalIOError(e));
        }
    }

    private static Path unwrap(Main.Path_ path) {
        List<String> list = path.asList();
        Path first = Paths.get(list.getFirst());
        List<String> slice = list.subList(1, list.size());
        for (String child : slice) {
            first = first.resolve(child);
        }
        return first;
    }

    static Main.Result<String, Main.IOError> readString(Main.Path_ source) {
        try {
            return new Main.Ok<>(java.nio.file.Files.readString(unwrap(source)));
        } catch (IOException e) {
            return new Main.Err<>(new ExceptionalIOError(e));
        }
    }
}
