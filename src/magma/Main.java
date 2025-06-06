package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var target = source.resolveSibling("Main.ts");

            final var input = Files.readString(source);
            final var output = compile(input);
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        final var segments = new ArrayList<String>();
        final var length = input.length();
        var buffer = new StringBuilder();
        var depth = 0;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else if (c == '}' && depth == 1) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
                depth--;
            }
            else {
                if (c == '{') {
                    depth++;
                }
                if (c == '}') {
                    depth--;
                }
            }
        }
        segments.add(buffer.toString());

        final var output = new StringBuilder();
        for (var segment : segments) {
            output.append(mapper.apply(segment));
        }

        return output.toString();
    }

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        final var classIndex = stripped.indexOf("class ");
        if (classIndex >= 0) {
            final var afterClass = stripped.substring(classIndex + "class ".length());
            final var contentStart = afterClass.indexOf("{");
            if (contentStart >= 0) {
                final var name = afterClass.substring(0, contentStart).strip();
                final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    final var outputContent = compileStatements(inputContent, Main::compileClassSegment);
                    return "export class " + name + " {" + outputContent + "}";
                }
            }
        }

        return generatePlaceholder(input);
    }

    private static String compileClassSegment(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var beforeParams = input.substring(0, paramStart).strip();
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var withBraces = withParams.substring(paramEnd + ")".length());
                return "\n\t" + generatePlaceholder(beforeParams) + "(" + generatePlaceholder(params) + ")" + generatePlaceholder(withBraces);
            }
        }

        return generatePlaceholder(input);
    }

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }
}