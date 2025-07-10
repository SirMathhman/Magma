package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sourceDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);
    }

    private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source.getParent());

        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);

        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));

        Files.writeString(target, output.toString());
    }

    private static String compileRootSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ")) return "";
        if (!stripped.isEmpty() && '}' == stripped.charAt(stripped.length() - 1)) {
            final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());
            return Main.generatePlaceholder(withoutEnd) + "}";
        }

        return Main.generatePlaceholder(stripped) + System.lineSeparator();
    }

    private static List<String> divide(final CharSequence input) {
        final var state = Main.foldEarly(new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped)));
        return state.right().advance().stream().toList();
    }

    private static Tuple<Boolean, DivideState> foldEarly(final DivideState initial,
                                                         final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                         final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        Tuple<Boolean, DivideState> tuple = new Tuple<>(true, initial);
        while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }
        return tuple;
    }

    private static Tuple<Boolean, DivideState> foldEarlyElement(final DivideState state,
                                                                final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                                final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        final var maybePopped = mapper.apply(state);
        if (maybePopped.isEmpty()) return new Tuple<>(false, state);
        final var popped = maybePopped.get();
        return folder.apply(popped);
    }

    private static DivideState foldDecorated(final Tuple<DivideState, Character> popped) {
        final var state = popped.left();
        final var c = popped.right();
        return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> Main.foldStatement(state, c));
    }

    private static Optional<DivideState> foldDoubleQuotes(final DivideState state, final char c) {
        if ('\"' != c) return Optional.empty();
        return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right());
    }

    private static Tuple<Boolean, DivideState> foldInDoubleQuotes(final Tuple<DivideState, Character> popped) {
        final var nextAppended = popped.left();
        final var next = popped.right();

        if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));
        if ('\"' == next) return new Tuple<>(false, nextAppended);
        return new Tuple<>(true, nextAppended);
    }

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if ('\'' != c) return Optional.empty();
        return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption);
    }

    private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right()) return tuple.left().popAndAppendToOption();
        return Optional.of(tuple.left());
    }

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }
}
