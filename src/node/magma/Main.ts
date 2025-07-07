export class Main {
	/*public static final String LINE_SEPARATOR = System.lineSeparator();*/
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
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);
        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);
        final var csq = Main.compileStatements(input, Main::compileRootSegment);
        Files.writeString(target, csq);
    }*/
	/*private static String compileStatements(final String input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(mapper.apply(segment));
        final var csq = output.toString();
        return csq;
    }*/
	/*private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
        return Main.compileClass(strip).orElseGet(() -> Main.generatePlaceholder(strip));
    }*/
	/*private static Optional<String> compileClass(final String strip) {
        return Main.compileSuffix(strip, "}*/
	/*");*/
	/**/
}/*private static Optional<String> compileSuffix(final String input, final String suffix) */{
	/*if (!input.endsWith(suffix)) return Optional.empty();*/
	/*final var withoutEnd = input.substring(0, input.length() - suffix.length());*/
	/*return Main.compileInfix(withoutEnd, "{", (beforeContent1, content1) -> Optional.of(
                Main.compileClassHeader(beforeContent1) + "{" +
                Main.compileStatements(content1, Main::compileClassSegments) + Main.LINE_SEPARATOR + "}"));
    }*/
	/*private static Optional<String> compileInfix(final String withoutEnd,
                                                 final String infix,
                                                 final BiFunction<String, String, Optional<String>> mapper) {
        final var contentStart = withoutEnd.indexOf(infix);
        if (0 > contentStart) return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var content = withoutEnd.substring(contentStart + infix.length());
        return mapper.apply(beforeContent, content);
    }*/
	/*private static String compileClassHeader(final String input) {
        return Main.compileInfix(input, "class ", (modifiers, s2) -> {
            final var stripped = modifiers.strip();
            final String newModifiers;
            if ("public".contentEquals(stripped)) newModifiers = "export ";
            else newModifiers = "";
            return Optional.of(newModifiers + "class " + s2);
        }).orElseGet(() -> Main.generatePlaceholder(input));
    }*/
	/*private static String compileClassSegments(final String input) {
        return Main.LINE_SEPARATOR + "\t" + Main.generatePlaceholder(input.strip());
    }*/
	/*private static List<String> divide(final CharSequence input) {
        var current = (DivideState) new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance().stream().toList();
    }*/
	/*private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}*/
	/*' == c && appended.isShallow()) return appended.advance().exit();*/
	/*if ('{' == c) return appended.enter();
        if ('}*/
	/*' == c) return appended.exit();*/
	/*return appended;*/
	/**/
}/*private static String generatePlaceholder(final String input) */{
	/*final var replaced = input.replace("start", "start").replace("end", "end");*/
	/*return "start" + replaced + "end";*/
	/**/
}/*}*/