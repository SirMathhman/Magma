package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Main {

    public static final Path SOURCE_FILE = Paths.get(".", "src", "java", "magma", "Main.java");

    public static void main(String[] args) {
        try {
            final var input = Files.readString(SOURCE_FILE);
            final var output = divideAndCompileStatements(input, Main::compileRootSegment);

            final var target = SOURCE_FILE.resolveSibling("Main.c");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String divideAndCompileStatements(String input, Function<String, String> compiler) {
        final var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        var depth = 0;
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            } else if (c == '}' && depth == 1) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
                depth--;
            } else {
                if (c == '{') depth++;
                if (c == '}') depth--;
            }
        }
        segments.add(buffer.toString());

        final var output = new StringBuilder();
        for (var segment : segments) {
            output.append(compiler.apply(segment));
        }
        return output.toString();
    }

    private static String compileRootSegment(String input) {
        if (input.startsWith("package ")) return "";

        final var stripped = input.strip();
        if (stripped.startsWith("import ")) {
            final var right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                final var namespaceString = right.substring(0, right.length() - ";".length());
                final var namespace = namespaceString.split(Pattern.quote("."));
                final var joinedNamespace = String.join("/", namespace);
                return "#include <%s.h>\n".formatted(joinedNamespace);
            }
        }

        final var classKeyword = input.indexOf("class ");
        if (classKeyword >= 0) {
            final var right = input.substring(classKeyword + "class ".length());
            final var contentStart = right.indexOf("{");
            if (contentStart >= 0) {
                final var name = right.substring(0, contentStart).strip();
                final var withEnd = right.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    final var outputContent = divideAndCompileStatements(inputContent, Main::compileClassSegment);
                    return "struct " +
                            name +
                            " {\n};\n" + outputContent;
                }
            }
        }

        return invalidate("root segment", input);
    }

    private static String invalidate(String type, String input) {
        System.err.println("Invalid " + type + ": " + input);
        return input;
    }

    private static String compileClassSegment(String input) {
        return invalidate("class segment", input);
    }
}
