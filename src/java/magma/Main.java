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

        Node retype(String type);

        boolean is(String type);
    }

    private interface Rule {
        Optional<String> generate(Node node);

        Optional<Node> lex(String input);
    }

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments;
        private int depth;
        private StringBuilder buffer;

        private MutableDivideState(final Collection<String> segments, final StringBuilder buffer) {
            this.segments = new ArrayList<>(segments);
            this.buffer = buffer;
            depth = 0;
        }

        private MutableDivideState() {
            this(new ArrayList<>(), new StringBuilder());
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
        private final Optional<String> maybeType;
        private final Map<String, String> strings;

        private MapNode(final Optional<String> maybeType, final Map<String, String> strings) {
            this.maybeType = maybeType;
            this.strings = strings;
        }

        private MapNode() {
            this(Optional.empty(), new HashMap<>());
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

        @Override
        public Node retype(final String type) {
            return new MapNode(Optional.of(type), strings);
        }

        @Override
        public boolean is(final String type) {
            return maybeType.isPresent() && maybeType.get()
                    .contentEquals(type);
        }
    }

    private record StringRule(String key) implements Rule {
        @Override
        public Optional<String> generate(final Node node) {
            return node.findString(key);
        }

        @Override
        public Optional<Node> lex(final String input) {
            return Optional.of(new MapNode().withString(key(), input));
        }
    }

    private record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
        @Override
        public Optional<String> generate(final Node node) {
            return leftRule.generate(node)
                    .flatMap(leftResult -> rightRule.generate(node)
                            .map(rightResult -> leftResult + infix + rightResult));
        }

        @Override
        public Optional<Node> lex(final String input) {
            final var index = input.indexOf(infix());
            if (0 > index)
                return Optional.empty();

            final var beforeContent = input.substring(0, index);
            final var leftResult = leftRule().lex(beforeContent);

            final var content = input.substring(index + infix().length());
            final var rightResult = rightRule().lex(content);

            return leftResult.flatMap(leftValue -> rightResult.map(leftValue::merge));
        }
    }

    private record SuffixRule(Rule rule, String suffix) implements Rule {
        @Override
        public Optional<String> generate(final Node node) {
            return rule.generate(node)
                    .map(result -> result + suffix);
        }

        @Override
        public Optional<Node> lex(final String input) {
            if (!input.endsWith(suffix()))
                return Optional.empty();

            final var withoutEnd = input.substring(0, input.length() - suffix().length());
            return rule().lex(withoutEnd);
        }
    }

    private record StripRule(Rule rule) implements Rule {
        @Override
        public Optional<String> generate(final Node node) {
            return rule.generate(node);
        }

        @Override
        public Optional<Node> lex(final String input) {
            final var stripped = input.strip();
            return rule.lex(stripped);
        }
    }

    private record TypeRule(String type, Rule rule) implements Rule {
        @Override
        public Optional<String> generate(final Node node) {
            if (node.is(type))
                return rule.generate(node);
            return Optional.empty();
        }

        @Override
        public Optional<Node> lex(final String input) {
            return rule.lex(input)
                    .map(node -> node.retype(type));
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

        return Main.createStructureRule()
                .lex(stripped)
                .flatMap(Main::generate)
                .orElseGet(() -> Main.generatePlaceholder(stripped));
    }

    private static Rule createStructureRule() {
        return new TypeRule("structure",
                new StripRule(new SuffixRule(new InfixRule(new StringRule("before-content"),
                        "{",
                        new StringRule("content")), "}")));
    }

    private static Optional<String> generate(final Node node) {
        return Optional.of(Main.generatePlaceholder(new StringRule("before-content").generate(node)
                .orElse("")) + "{" + Main.generatePlaceholder(new StringRule("content").generate(node)
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
