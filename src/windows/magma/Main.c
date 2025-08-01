/*public final */struct Main {};
/*private static */struct State {};
/*private final Collection<String> segments = new ArrayList<>();*/
/*private StringBuilder buffer = new StringBuilder();*/
/*private int depth = 0;*/
/*private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}*/
/*private State append(final char c) {
			this.buffer.append(c);
			return this;
		}*/
/*private boolean isLevel() {
			return 0 == this.depth;
		}*/
/*private State exit() {
			this.depth = this.depth - 1;
			return this;
		}*/
/*private State enter() {
			this.depth = this.depth + 1;
			return this;
		}*/
/*private Stream<String> stream() {
			return this.segments.stream();
		}*/
/*final boolean isShallow() {
			return 1 == this.depth;
		}*/
/**/

/*private Main() {}*/
/*public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");

			final var joined = Main.compile(input);
			Files.writeString(target, joined);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*/
/*private static String compile(final CharSequence input) {
		return Main.compileStatements(input, Main::compileRootSegment);
	}*/
/*private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
		final var segments = Main.divide(input);
		return segments.stream().map(mapper).collect(Collectors.joining());
	}*/
/*private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return Main.compileRootSegmentValue(strip) + System.lineSeparator();
	}*/
/*private static String compileRootSegmentValue(final String input) {
		return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input));
	}*/
/*private static Optional<String> compileClass(final String input) {
		final var classIndex = input.indexOf("*/struct ");
		if (0 > classIndex) return Optional.empty();
		final var before = input.substring(0, classIndex);
		final var after = input.substring(classIndex + "class ".length());

		final var i = after.indexOf(' {};
/*');*/
/*if (0 > i) return Optional.empty();*/
/*final var name = after.substring(0, i).strip();*/
/*final var withEnd = after.substring(i + 1).strip();*/
/*if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);
		return Optional.of(Main.generatePlaceholder(before) + "struct " + name + " {};" + System.lineSeparator() +
											 Main.compileStatements(content, Main::compileClassSegment));*/

/*private static String compileClassSegment(final String input) {
		return Main.compileClassSegmentValue(input.strip()) + System.lineSeparator();
	}*/
/*private static String compileClassSegmentValue(final String input) {
		return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input));
	}*/
/*private static List<String> divide(final CharSequence input) {
		final var length = input.length();
		var current = new State();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream().toList();
	}*/
/*private static State fold(final State state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}*/
/*' == c && current.isShallow()) return current.advance().exit();*/
/*if ('{' == c) return current.enter();
		if ('}*/
/*' == c) return current.exit();*/
/*return current;*/
/**/

/*private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*/
/*}*/
