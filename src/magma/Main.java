package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
        var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            buffer.append(c);
            if (c != ';') continue;
            segments.add(buffer.toString());
            buffer = new StringBuilder();
        }

        segments.add(buffer.toString());

        var output = new StringBuilder();
        for (var segment : segments) {
            output.append(compileRootMember(segment));
        }

        return output.toString();
    }

    private static String compileRootMember(String input) throws CompilationException {
        if (input.startsWith("package ")) return "";

        throw new CompilationException("Invalid root member", input);
    }
}
