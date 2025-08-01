/*private static */struct DivideState {/*private final Collection<String> segments = new ArrayList<>();*/
/*private StringBuilder buffer = new StringBuilder();*/
/*private int depth = 0;*/
/*private DivideState advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}*/
/*private DivideState append(final char c) {
			this.buffer.append(c);
			return this;
		}*/
/*private boolean isLevel() {
			return 0 == this.depth;
		}*/
/*private DivideState exit() {
			this.depth = this.depth - 1;
			return this;
		}*/
/*private DivideState enter() {
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
};/*private static Optional<Tuple<ParseState, String>> compileClass(final ParseState state, final String input) {
		final var classIndex = input.indexOf("*/struct ");
		if (0 > classIndex) return Optional.empty();
		final var before = input.substring(0, classIndex);
		final var after = input.substring(classIndex + "class ".length());

		final var i = after.indexOf(' {/*');*/
/*if (0 > i) return Optional.empty();*/
/*final var name = after.substring(0, i).strip();*/
/*final var withEnd = after.substring(i + 1).strip();*/
/*if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		final var tuple = Main.compileStatements(state, content, Main::compileClassSegment);
		final var generated = Main.generatePlaceholder(before) + "struct " + name + " {" + tuple.right + "};";
		return Optional.of(new Tuple<>(tuple.left.addStructure(generated), ""));*/
};/*public final */struct Main {
/*private record ParseState(List<String> structures) {
		private ParseState() {
			this(new ArrayList<>());
		}

		ParseState addStructure(final String generated) {
			this.structures.add(generated);
			return this;
		}
	}*/
/*private record Tuple<Left, Right>(Left left, Right right) {}*/
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
		final var tuple = Main.compileStatements(new ParseState(), input, Main::compileRootSegment);
		final var joinedStructures = String.join("", tuple.left.structures);
		return joinedStructures + tuple.right;
	}*/
/*private static Tuple<ParseState, String> compileStatements(final ParseState state,
																														 final CharSequence input,
																														 final BiFunction<ParseState, String, Tuple<ParseState, String>> mapper) {
		final var segments = Main.divide(input);

		var current = state;
		final var buffer = new StringBuilder();
		for (final var segment : segments) {
			final var tuple = mapper.apply(current, segment);
			current = tuple.left;
			buffer.append(tuple.right);
		}

		return new Tuple<>(current, buffer.toString());
	}*/
/*private static Tuple<ParseState, String> compileRootSegment(final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return new Tuple<>(state, "");

		final var tuple = Main.compileRootSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right + System.lineSeparator());
	}*/
/*private static Tuple<ParseState, String> compileRootSegmentValue(final ParseState state, final String input) {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/

/*private static Tuple<ParseState, String> compileClassSegment(final ParseState state, final String input) {
		final var tuple = Main.compileClassSegmentValue(state, input.strip());
		return new Tuple<>(tuple.left, tuple.right + System.lineSeparator());
	}*/
/*private static Tuple<ParseState, String> compileClassSegmentValue(final ParseState state, final String input) {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/
/*private static List<String> divide(final CharSequence input) {
		final var length = input.length();
		var current = new DivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream().toList();
	}*/
/*private static DivideState fold(final DivideState state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}*/
/*' == c && current.isShallow()) return current.advance().exit();*/
/*if ('{' == c) return current.enter();
		if ('}*/
/*' == c) return current.exit();*/
/*return current;*/
/**/
};
/*private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*/
/*}*/
