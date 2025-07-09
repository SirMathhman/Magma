/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.stream.Collectors;*/
/*public class Main {
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
        final var separator = fileName.lastIndexOf(.');
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
        if (!stripped.isEmpty() && }' == stripped.charAt(stripped.length() - 1)) {
            final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());
            return Main.generatePlaceholder(withoutEnd) + "}";
        }

        return Main.generatePlaceholder(stripped) + System.lineSeparator();*/
/*}

    private static List<String> divide(final CharSequence input) {
        Tuple<Boolean, DivideState> state = new Tuple<>(true, new MutableDivideState(input));*/
/*while (state.left()) state = Main.foldAsState(state);*/
/*return state.right().advance().stream().toList();*/
/*}

    private static Tuple<Boolean, DivideState> foldAsState(final Tuple<Boolean, DivideState> state) {
        final var maybePopped = state.right().pop();*/
/*if (maybePopped.isEmpty()) return new Tuple<>(false, state.right());*/
/*final var popped = maybePopped.get();*/
/*return new Tuple<>(true, Main.foldDecorated(popped));*/
/*}

    private static DivideState foldDecorated(final Tuple<DivideState, Character> popped) {
        final var state = popped.left();*/
/*final var c = popped.right();*/
/*return Main.foldSingleQuotes(state, c).orElseGet(() -> Main.foldStatement(state, c));*/
/*}

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if (\'' != c) return Optional.empty();*/
/*return state.popAndAppendToTuple().flatMap(Main::foldEscape).flatMap(DivideState::popAndAppendToOption);*/
/*}

    private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if (\\' == tuple.right()) return tuple.left().popAndAppendToOption();*/
/*return Optional.of(tuple.left());*/
/*}

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);*/
/*if (;' == c && appended.isLevel()) return appended.advance();*/
/*if ({' == c) return appended.enter();*/
/*if (}' == c) return appended.exit();*/
/*return appended;*/
/*}

    private static String generatePlaceholder(final String input) {
        return "start" + input.replace("start", "start").replace("end", "end") + "end";*/
/*}
*/}