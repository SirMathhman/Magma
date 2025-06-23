package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {
    private interface DivideState {
        DivideState append(char c);

        DivideState advance();

        Collection<String> unwrap();

        boolean isLevel();

        DivideState exit();

        DivideState enter();
    }

    private interface Node {
        Node withString(String key, String value);

        Optional<String> findString(String key);

        Node merge(Node other);

        Stream<Map.Entry<String, String>> streamStrings();
    }

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments;
        private int depth;
        private StringBuilder buffer;

        private MutableDivideState(final Collection<String> segments, final StringBuilder buffer, final int depth) {
            this.segments = new ArrayList<>(segments);
            this.buffer = buffer;
            this.depth = depth;
        }

        private MutableDivideState() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        @Override
        public Collection<String> unwrap() {
            return Collections.unmodifiableCollection(segments);
        }

        @Override
        public boolean isLevel() {
            return 0 == depth;
        }

        @Override
        public DivideState exit() {
            depth--;
            return this;
        }

        @Override
        public DivideState enter() {
            depth++;
            return this;
        }

        @Override
        public DivideState append(final char c) {
            buffer.append(c);
            return this;
        }

        @Override
        public DivideState advance() {
            segments.add(buffer.toString());
            buffer = new StringBuilder();
            return this;
        }
    }

    private static final class MapNode implements Node {
        private final Map<String, String> strings;

        private MapNode(final Map<String, String> strings) {
            this.strings = strings;
        }

        private MapNode() {
            this(new HashMap<>());
        }

        @Override
        public Node withString(final String key, final String value) {
            strings.put(key, value);
            return this;
        }

        @Override
        public Optional<String> findString(final String key) {
            return Optional.ofNullable(strings.get(key));
        }

        @Override
        public Node merge(final Node other) {
            return other.streamStrings()
                    .<Node>reduce(this,
                            (node, entry) -> node.withString(entry.getKey(), entry.getValue()),
                            (_, next) -> next);
        }

        @Override
        public Stream<Map.Entry<String, String>> streamStrings() {
            return strings.entrySet()
                    .stream();
        }
    }

    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var output = Main.compile(input);
            final var target = source.resolveSibling("Main.ts");
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input) {
        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments)
            output.append(Main.compileRootSegment(segment));

        return output.toString();
    }

    private static String compileRootSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import "))
            return "";

        return Main.compileStructure(stripped)
                .orElseGet(() -> Main.generatePlaceholder(stripped));
    }

    private static Optional<String> compileStructure(final String input) {
        final var stripped = input.strip();
        if (stripped.endsWith("}")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());
            final var contentStart = withoutEnd.indexOf('{');
            if (0 <= contentStart) {
                final var beforeContent = withoutEnd.substring(0, contentStart);
                final var node = new MapNode().withString("before-content", beforeContent);

                final var content = withoutEnd.substring(contentStart + "{".length());
                final var node1 = new MapNode().withString("content", content);
                return Main.generate(node.merge(node1));
            }
        }

        return Optional.empty();
    }

    private static Optional<String> generate(final Node node) {
        return Optional.of(Main.generatePlaceholder(node.findString("before-content")
                .orElse("")) + "{" + Main.generatePlaceholder(node.findString("content")
                .orElse("")) + "}");
    }

    private static Collection<String> divide(final CharSequence input) {
        final DivideState state = new MutableDivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('{' == c)
            return appended.enter();
        if ('}' == c)
            return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }
}
