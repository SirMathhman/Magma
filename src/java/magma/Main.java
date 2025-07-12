package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private interface DivideState {
        Stream<String> stream();

        DivideState advance();

        DivideState append(char c);
    }

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments = new ArrayList<>();
        private StringBuilder buffer = new StringBuilder();

        @Override
        public Stream<String> stream() {
            return this.segments.stream();
        }

        @Override
        public DivideState advance() {
            this.segments.add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        @Override
        public DivideState append(final char c) {
            this.buffer.append(c);
            return this;
        }
    }

    private Main() {}

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var output = Main.compile(input);
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));
        return output.toString();
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";

        return Main.generatePlaceholder(strip) + System.lineSeparator();
    }

    private static List<String> divide(final CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance().stream().toList();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c) return appended.advance();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }
}
