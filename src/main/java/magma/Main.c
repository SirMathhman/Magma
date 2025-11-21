/*public*/struct Main {};
/*
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			Files.writeString(target, compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		var depth = 0;

		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else {
				if (c == '{') {
					depth++;
				}
				if (c == '}') {
					depth--;
				}
			}
		}
		segments.add(buffer.toString());

		return segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		final var i = stripped.indexOf("class ");
		if (i >= 0) {
			final var modifiers = stripped.substring(0, i).strip();
			final var afterKeyword = stripped.substring(i + "class ".length()).strip();
			final var i1 = afterKeyword.indexOf("{");
			if (i1 >= 0) {
				final var name = afterKeyword.substring(0, i1).strip();
				final var content = afterKeyword.substring(i1 + 1);
				return wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() + wrap(content);
			}
		}

		return wrap(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
}*/