/*import magma.rule.InfixRule;*/
/*import magma.rule.OrRule;*/
/*import magma.rule.PlaceholderRule;*/
/*import magma.rule.Rule;*/
/*import magma.rule.StringRule;*/
/*import magma.rule.SuffixRule;*/
/*import magma.rule.TypeRule;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.List;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.Stream;*/
/*public final*/class Main {/*
	private static final Path TARGET_DIRECTORY = Paths.get(".", "src", "node");

	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			final var targets = Main.runWithSources(sources, sourceDirectory);
			Main.pruneTargets(targets);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static void pruneTargets(final Collection<Path> targets) throws IOException {
		try (final Stream<Path> stream1 = Files.walk(Main.TARGET_DIRECTORY)) {
			final var extra =
					stream1.filter(Files::isRegularFile).filter(target -> !targets.contains(target)).collect(Collectors.toSet());
			Main.deleteExtras(extra);
		}
	}

	private static void deleteExtras(final Iterable<Path> extras) throws IOException {
		for (final var path : extras) Files.deleteIfExists(path);
	}

	private static List<Path> runWithSources(final Iterable<Path> sources, final Path sourceDirectory)
			throws IOException {
		final List<Path> targets = new ArrayList<>();
		for (final var source : sources) targets.add(Main.runWithSource(source, sourceDirectory));
		return targets;
	}

	private static Path runWithSource(final Path source, final Path sourceDirectory) throws IOException {
		final var relativeParent = sourceDirectory.relativize(source.getParent());
		final var fileName = source.getFileName().toString();
		final var separator = fileName.lastIndexOf('.');
		final var name = fileName.substring(0, separator);

		final var targetParent = Main.TARGET_DIRECTORY.resolve(relativeParent);
		if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

		final var target = targetParent.resolve(name + ".ts");

		final var input = Files.readString(source);
		final var output = Main.divide(input).stream().map(Main::compileRootSegment).collect(Collectors.joining());

		Files.writeString(target, output);
		return target;
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ")) return "";
		return Main.createJavaRootSegmentValueRule()
							 .lex(strip)
							 .flatMap(new SuffixRule(Main.createTSRootSegmentValueRule(), System.lineSeparator())::generate)
							 .orElse("");
	}

	private static Rule createJavaRootSegmentValueRule() {
		return new OrRule(List.of(Main.createJavaClassRule(), Main.createPlaceholderRule()));
	}

	private static Rule createTSRootSegmentValueRule() {
		return new OrRule(List.of(Main.createTSClassRule(), Main.createPlaceholderRule()));
	}

	private static Rule createPlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("value")));
	}

	private static Rule createJavaClassRule() {
		final var modifiers1 = new StringRule("modifiers");
		final var name = new InfixRule(new StringRule("name"), "{", new StringRule("with-end"));
		return new TypeRule("class", new InfixRule(modifiers1, "class ", name));
	}

	private static Rule createTSClassRule() {
		final var name = new InfixRule(new StringRule("name"), " {", new PlaceholderRule(new StringRule("with-end")));
		final var modifiers = new PlaceholderRule(new StringRule("modifiers"));
		return new TypeRule("class", new InfixRule(modifiers, "class ", name));
	}

	private static State divide(final CharSequence input) {
		final var length = input.length();
		var current = new State();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance();
	}

	private static State fold(final State state, final char c) {
		final var appended = state.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('{' == c) return appended.enter();
		if ('}' == c) return appended.exit();
		return appended;
	}

}*/
