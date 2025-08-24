/*public */struct Main {};
/*private static class State {
		private final Collection<String> segments = new ArrayList<>();
		public int depth = 0;
		private StringBuilder buffer = new StringBuilder();

		private Stream<String> stream() {
			return segments.stream();
		}

		private State advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private State append(char c) {
			buffer.append(c);
			return this;
		}

		public boolean isLevel() {
			return depth == 0;
		}

		public State enter() {
			depth++;
			return this;
		}

		public State exit() {
			depth--;
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "main.c"), compile(input) + "int main(){\r\n\treturn 0;\r\n}");
			new ProcessBuilder("clang", "main.c", "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(CharSequence input) {
		return divide(input).map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileClass(strip).orElseGet(() -> wrap(strip));
	}

	private static Optional<String> compileClass(String strip) {
		final var contentStart = strip.indexOf("{");
		if (contentStart < 0) return Optional.empty();
		final var beforeBraces = strip.substring(0, contentStart);
		final var classIndex = beforeBraces.indexOf("class ");

		if (classIndex < 0) return Optional.empty();
		final var modifiers = beforeBraces.substring(0, classIndex);
		final var name = beforeBraces.substring(classIndex + "class ".length()).strip();

		final var withEnd = strip.substring(contentStart + "{".length()).strip();
		if (!withEnd.endsWith("}")) return Optional.empty();
		final var substring = withEnd.substring(0, withEnd.length() - "}".length());

		return Optional.of(wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() + wrap(substring));
	}

	private static Stream<String> divide(CharSequence input) {
		var current = new State();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static State fold(State current, char c) {
		final var appended = current.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}
*/int main(){
	return 0;
}