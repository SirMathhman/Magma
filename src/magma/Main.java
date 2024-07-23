package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var source = Paths.get(".", "src", "magma", "Main.java");
            var input = Files.readString(source);
            var output = compile(input);
            Files.writeString(Paths.get(".", "src", "magma", "Main.mgs"), output);
        } catch (IOException | CompilationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) throws CompilationException {
        throw new CompilationException("Failed to compile", input);
    }
}
