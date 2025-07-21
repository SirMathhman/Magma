/*public */struct Main {};
/*public static class DivideState {
		private final Collection<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();
		private int depth = 0;

		private DivideState advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private Stream<String> stream() {
			return segments.stream();
		}

		private DivideState append(char c) {
			buffer.append(c);
			return this;
		}

		public boolean isLevel() {
			return depth == 0;
		}

		public DivideState enter() {
			depth++;
			return this;
		}

		public DivideState exit() {
			depth--;
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			final var output = compile(input);
			Files.writeString(target, output);
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return divide(input).map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileRootSegmentValue(strip) + System.lineSeparator();
	}

	private static String compileRootSegmentValue(String input) {
		return compileClass(input).orElseGet(() -> generatePlaceholder(input));
	}

	private static Optional<String> compileClass(String input) {
		final var classIndex = input.indexOf("class ");
		if (classIndex < 0) {return Optional.empty();}
		final var beforeKeyword = input.substring(0, classIndex);
		final var afterKeyword = input.substring(classIndex + "class ".length());

		final var contentStart = afterKeyword.indexOf("{");
		if (contentStart < 0) {return Optional.empty();}
		final var name = afterKeyword.substring(0, contentStart).strip();
		final var afterName = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterName.endsWith("}")) {return Optional.empty();}
		final var withoutEnd = afterName.substring(0, afterName.length() - "}".length());

		return Optional.of(generatePlaceholder(beforeKeyword) + "struct " + name + " {};" + System.lineSeparator() +
											 generatePlaceholder(withoutEnd));

	}

	private static Stream<String> divide(String input) {
		var current = new DivideState();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static DivideState fold(DivideState state, char c) {
		final var appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String generatePlaceholder(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
*/
