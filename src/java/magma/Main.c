/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Stream;*/
struct Main {};
/*
    private interface DivideState {
        Stream<String> stream();

        DivideState advance();

        DivideState append(char c);

        boolean isLevel();

        DivideState enter();

        DivideState exit();

        Optional<Tuple<DivideState, Character>> pop();

        Optional<Tuple<DivideState, Character>> popAndAppendToTuple();

        Optional<DivideState> popAndAppendToOptional();

        boolean isShallow();
    }*//*

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments = new ArrayList<>();
        private final CharSequence input;
        private int depth = 0;
        private StringBuilder buffer = new StringBuilder();
        private int index = 0;

        private MutableDivideState(final CharSequence input) {
            this.input = input;
        }

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

        @Override
        public boolean isLevel() {
            return 0 == this.depth;
        }

        @Override
        public DivideState enter() {
            this.depth++;
            return this;
        }

        @Override
        public DivideState exit() {
            this.depth--;
            return this;
        }

        @Override
        public Optional<Tuple<DivideState, Character>> pop() {
            if (this.index >= this.input.length()) return Optional.empty();
            final var value = this.input.charAt(this.index);
            this.index++;
            return Optional.of(new Tuple<>(this, value));
        }

        @Override
        public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
            return this.pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
        }

        @Override
        public Optional<DivideState> popAndAppendToOptional() {
            return this.popAndAppendToTuple().map(Tuple::left);
        }

        @Override
        public boolean isShallow() {
            return 1 == this.depth;
        }
    }*//*

    private record Tuple<Left, Right>(Left left, Right right) {}*//*

    private Main() {}*//*

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
    }*//*

    private static String compile(final CharSequence input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }*//*

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(mapper.apply(segment));
        return output.toString();
    }*//*

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";
        return Main.compileRootSegmentValue(strip) + System.lineSeparator();
    }*//*

    private static String compileRootSegmentValue(final String input) {
        return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input));
    }*//*

    private static Optional<String> compileClass(final String input) {
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1)) return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "}*//*".length());*//*
        final var contentStart = withoutEnd.indexOf('{');*//*
        if (0 > contentStart) return Optional.empty();*//*

        final var beforeContent = withoutEnd.substring(0, contentStart);*//*
        final var content = withoutEnd.substring(contentStart + "{".length());
        final var keywordIndex = beforeContent.indexOf("class ");
        if (0 > keywordIndex) return Optional.empty();

        final var name = beforeContent.substring(keywordIndex + "class ".length()).strip();
        return Optional.of("struct " + name + " {};" + System.lineSeparator() +
                           Main.compileStatements(content, Main::compileClassSegment));
    }*//*

    private static String compileClassSegment(final String input) {
        return Main.generatePlaceholder(input);
    }*//*

    private static List<String> divide(final CharSequence input) {
        Tuple<Boolean, DivideState> current = new Tuple<>(true, new MutableDivideState(input));
        while (current.left) current = Main.foldAsTuple(current);
        return current.right.advance().stream().toList();
    }*//*

    private static Tuple<Boolean, DivideState> foldAsTuple(final Tuple<Boolean, DivideState> current) {
        final var maybePopped = current.right.pop();
        if (maybePopped.isEmpty()) return new Tuple<>(false, current.right);

        final var popped = maybePopped.get();
        return new Tuple<>(true, Main.foldDecorated(popped.left, popped.right));
    }*//*

    private static DivideState foldDecorated(final DivideState state, final char next) {
        return Main.foldSingleQuotes(state, next).orElseGet(() -> Main.foldStatement(state, next));
    }*//*

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char next) {
        if ('\'' != next) return Optional.empty();

        final var appended = state.append('\'');
        return appended.popAndAppendToTuple().flatMap(Main::foldEscape).flatMap(DivideState::popAndAppendToOptional);
    }*//*

    private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right) return tuple.left.popAndAppendToOptional();
        return Optional.of(tuple.left);
    }*//*

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.advance().exit();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }*//*

    private static String generatePlaceholder(final String input) {
        return "start" + input.replace("start", "start").replace("end", "end") + "end";
    }*//*
*/
/**/
