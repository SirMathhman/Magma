package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record Application(Path source) {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");

    void run() throws ApplicationException {
        try {
            if (!Files.exists(source)) return;

            var input = Files.readString(source);
            var output = compile(input);

            var fileName = source.getFileName().toString();
            var separator = fileName.lastIndexOf('.');
            var name = fileName.substring(0, separator);
            var target = ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs");
            Files.writeString(target, output);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private static String compile(String input) throws ParseException {
        if(input.isEmpty()) {
            return "";
        } else {
            throw new ParseException("Invalid root", input);
        }
    }
}