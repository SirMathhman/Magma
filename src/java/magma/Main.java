package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var segments = Main.divide(input);
            final var target = Paths.get(".", "diagram.puml");
            final var output = Main.compile(segments);
            final var pre = List.of("@startuml", "skinparam linetype ortho", "class Main");
            final Collection<String> outputRootSegments = new ArrayList<>(pre);
            outputRootSegments.addAll(output);
            outputRootSegments.add("@enduml");

            final var joined = String.join(Main.LINE_SEPARATOR, outputRootSegments);
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> compile(final Iterable<String> segments) {
        final List<String> output = new ArrayList<>();
        for (final var segment : segments) Main.compileRootSegment(segment, output);
        return output;
    }

    private static void compileRootSegment(final String input, final Collection<String> output) {
        final var strip = input.strip();
        final var length = strip.length();
        if (strip.isEmpty() || ';' != strip.charAt(length - 1)) return;

        final var suffixLength = ";".length();
        final var substring = strip.substring(0, length - suffixLength);
        if (!substring.startsWith("import ")) return;

        final var prefixLength = "import ".length();
        final var substring1 = substring.substring(prefixLength);
        final var separator = substring1.lastIndexOf('.');
        if (0 <= separator) {
            final var infixLength = ".".length();
            final var child = substring1.substring(separator + infixLength);
            output.add("Main --> " + child);
        }
    }

    private static List<String> divide(final CharSequence input) {
        final List<String> segments = new ArrayList<>();
        var buffer = new StringBuilder();
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (';' == c) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
        }
        segments.add(buffer.toString());
        return segments;
    }
}
