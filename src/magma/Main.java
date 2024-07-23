package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            var source = resolve("java");
            var input = Files.readString(source);
            var output = compile(input);
            Files.writeString(resolve("mgs"), output);
        } catch (IOException | CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

    private static String compile(String input) throws CompileException {
        var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            buffer.append(c);
            if (c == ';') {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
        }
        segments.add(buffer.toString());

        var output = new StringBuilder();
        for (String segment : segments) {
            output.append(compileRootMember(segment));
        }

        return output.toString();
    }

    private static String compileRootMember(String rootMember) throws CompileException {
        throw new CompileException("Unknown root member", rootMember);
    }
}
