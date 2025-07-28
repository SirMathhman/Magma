/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.Stream;*/
/*public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");*/
/*try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());*/
/*Main.runWithSources(sources, sourceDirectory);*/
/*} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*/
/*}
	}

	private static void runWithSources(final Iterable<Path> sources, final Path sourceDirectory) throws IOException {
		for (final var source : sources) Main.runWithSource(source, sourceDirectory);*/
/*}

	private static void runWithSource(final Path source, final Path sourceDirectory) throws IOException {
		final var relativeParent = sourceDirectory.relativize(source.getParent());*/
/*final var fileName = source.getFileName().toString();*/
/*final var separator = fileName.lastIndexOf('.');*/
/*final var name = fileName.substring(0, separator);*/
/*final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);*/
/*if (!Files.exists(targetParent)) Files.createDirectories(targetParent);*/
/*final var target = targetParent.resolve(name + ".ts");*/
/*final var input = Files.readString(source);*/
/*final var output = Main.divide(input).stream().map(Main::compileRootSegment).collect(Collectors.joining());*/
/*Files.writeString(target, output);*/
/*}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();*/
/*if (strip.startsWith("package ")) return "";*/
/*return Main.generatePlaceholder(strip) + System.lineSeparator();*/
/*}

	private static State divide(final CharSequence input) {
		final var length = input.length();*/
/*var current = new State();*/
/*for (var i = 0;*/
/*i < length;*/
/*i++) {
			final var c = input.charAt(i);*/
/*current = Main.fold(current, c);*/
/*}

		return current.advance();*/
/*}

	private static State fold(final State state, final char c) {
		final var appended = state.append(c);*/
/*if (';*/
/*' == c) return appended.advance();*/
/*return appended;*/
/*}

	private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*/
/*}
}*/
