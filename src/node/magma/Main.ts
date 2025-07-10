/*import magma.divide.DivideState;*/
/*import magma.divide.MutableDivideState;*/
/*import magma.node.MapNode;*/
/*import magma.rule.PlaceholderRule;*/
/*import magma.rule.Rule;*/
/*import magma.rule.StringRule;*/
/*import magma.rule.SuffixRule;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.BiFunction;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
export class Main {
	/*private static final String LINE_SEPARATOR = System.lineSeparator()*/;
	/*private Main() {}*/
	/*public static void main(final String[] args) {
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
    }*/
	/*private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);
    }*/
	/*private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source.getParent());

        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);

        final var output = Main.compileStatements(input, Main::compileRootSegment);
        Files.writeString(target, output);
    }*/
	/*private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(mapper.apply(segment));
        return output.toString();
    }*/
	/*private static String compileRootSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ")) return "";
        return Main.compileClass(stripped).orElseGet(() -> PlaceholderRule.wrap(stripped) + Main.LINE_SEPARATOR);
    }*/
	/*private static Optional<String> compileClass(final String input) {
        return Main.compileSuffix(input, "}", withoutEnd -> Main.compileInfix(withoutEnd, "{",
                                                                              (beforeContent, content) -> Optional.of(
                                                                                      Main.compileClassHeader(
                                                                                              beforeContent) + " {" +
                                                                                      Main.compileStatements(content,
                                                                                                             Main::compileClassStatement) +
                                                                                      Main.LINE_SEPARATOR + "}")));
    }*/
	/*private static String compileClassStatement(final String input) {
        final var stripped = input.strip();
        if (stripped.isEmpty()) return "";
        return Main.LINE_SEPARATOR + "\t" + Main.compileClassStatementValue(stripped);
    }*/
	/*private static String compileClassStatementValue(final String input) {
        return Main.compileSuffix(input, ";",
                                  slice1 -> Main.createClassStatementRule().generate(MapNode.createMapNode(slice1)))
                   .orElseGet(() -> PlaceholderRule.wrap(input));
    }*/
	/*private static Rule createClassStatementRule() {
        return new SuffixRule(new PlaceholderRule(new StringRule("value")), ";");
    }*/
	/*private static Optional<String> compileSuffix(final String input,
                                                  final String suffix,
                                                  final Function<String, Optional<String>> mapper) {
        if (!input.endsWith(suffix)) return Optional.empty();
        final var slice = input.substring(0, input.length() - suffix.length());
        return mapper.apply(slice);
    }*/
	/*private static String compileClassHeader(final String input) {
        return Main.compileInfix(input, "class ", (oldModifiers, name) -> Optional.of(
                           Main.compileModifiers(oldModifiers) + "class " + name.strip()))
                   .orElseGet(() -> PlaceholderRule.wrap(input));
    }*/
	/*private static String compileModifiers(final String oldModifiers) {
        final var stripped = oldModifiers.strip();
        if ("public".contentEquals(stripped)) return "export ";
        return "";
    }*/
	/*private static Optional<String> compileInfix(final String input,
                                                 final String infix,
                                                 final BiFunction<String, String, Optional<String>> mapper) {
        final var index = input.indexOf(infix);
        if (0 > index) return Optional.empty();
        final var beforeKeyword = input.substring(0, index);
        final var afterKeyword = input.substring(index + infix.length());
        return mapper.apply(beforeKeyword, afterKeyword);
    }*/
	/*private static List<String> divide(final CharSequence input) {
        final var state = Main.foldEarly(new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped)));
        return state.right().advance().stream().toList();
    }*/
	/*private static Tuple<Boolean, DivideState> foldEarly(final DivideState initial,
                                                         final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                         final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        Tuple<Boolean, DivideState> tuple = new Tuple<>(true, initial);
        while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }
        return tuple;
    }*/
	/*private static Tuple<Boolean, DivideState> foldEarlyElement(final DivideState state,
                                                                final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                                final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        final var maybePopped = mapper.apply(state);
        if (maybePopped.isEmpty()) return new Tuple<>(false, state);
        final var popped = maybePopped.get();
        return folder.apply(popped);
    }*/
	/*private static DivideState foldDecorated(final Tuple<DivideState, Character> popped) {
        final var state = popped.left();
        final var c = popped.right();
        return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> Main.foldStatement(state, c));
    }*/
	/*private static Optional<DivideState> foldDoubleQuotes(final DivideState state, final char c) {
        if ('\"' != c) return Optional.empty();
        return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right());
    }*/
	/*private static Tuple<Boolean, DivideState> foldInDoubleQuotes(final Tuple<DivideState, Character> popped) {
        final var nextAppended = popped.left();
        final var next = popped.right();

        if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));
        if ('\"' == next) return new Tuple<>(false, nextAppended);
        return new Tuple<>(true, nextAppended);
    }*/
	/*private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if ('\'' != c) return Optional.empty();
        return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption);
    }*/
	/*private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right()) return tuple.left().popAndAppendToOption();
        return Optional.of(tuple.left());
    }*/
	/*private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.exit().advance();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }*/
}/**/
