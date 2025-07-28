/*import magma.rule.InfixRule;*/
/*import magma.rule.PlaceholderRule;*/
/*import magma.rule.StringRule;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.BiFunction;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.Stream;*/
/*public final*/class Main {/*
	public static final Path TARGET_DIRECTORY = Paths.get(".", "src", "node");

	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			final var targets = Main.runWithSources(sources, sourceDirectory);
			try (final Stream<Path> stream1 = Files.walk(Main.TARGET_DIRECTORY)) {
				final var notGenerated = stream1.filter(Files::isRegularFile)
																				.filter(target -> !targets.contains(target))
																				.collect(Collectors.toSet());
				for (final var path : notGenerated) Files.deleteIfExists(path);
			}
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
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
		return Main.compileRootSegmentValue(strip) + System.lineSeparator();
	}

	private static String compileRootSegmentValue(final String input) {
		return Main.compileClass(input).orElseGet(() -> PlaceholderRule.wrap(input));
	}

	private static Optional<String> compileClass(final String input) {
		return Main.compileInfix(input, "class ", (s, s2) -> Main.compileInfix(s2, "{", (name1, withEnd1) -> Main.generate(
				new MapNode().withString("modifiers", s).withString("name", name1).withString("with-end", withEnd1))));

	}

	private static Optional<String> generate(final MapNode mapNode) {
		final var name = new InfixRule(new StringRule("name"), " {", new PlaceholderRule(new StringRule("with-end")));
		final var modifiers = new PlaceholderRule(new StringRule("modifiers"));

		return new InfixRule(modifiers, "class ", name).generate(mapNode);
	}

	private static Optional<String> compileInfix(final String input,
																							 final String infix,
																							 final BiFunction<String, String, Optional<String>> mapper) {
		final var index = input.indexOf(infix);
		if (0 > index) return Optional.empty();
		final var left = input.substring(0, index).strip();
		final var right = input.substring(index + infix.length());
		return mapper.apply(left, right);
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
