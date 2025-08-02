/*final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "windows", "magma", "Main.c"), Main.compile(input));
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(final CharSequence input) {
		final Collection<String> segments = new ArrayList<>();
		final var buffer = new StringBuilder();
		final var length = input.length();
		var depth = 0;
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (';' == c && 0 == depth) {
				segments.add(buffer.toString());
				buffer.setLength(0);
			}
			if ('{' == c) depth++;
			if ('}' == c) depth--;
		}
		segments.add(buffer.toString());

		return segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return Main.wrap(strip);
	}

	private static String wrap(final String input) {
		return "/*" + input + "*/";
	}
}*/