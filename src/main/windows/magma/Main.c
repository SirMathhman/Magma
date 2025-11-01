struct Main {};
/*public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "main", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "main", "windows", "magma", "Main.c"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*//*

	private static String compile(String input) {
		return compileStatements(input, Main::compileRootSegment);
	}*//*

	private static String compileStatements(String input, Function<String, String> mapper) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuffer();
		var depth = 0;
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
				depth--;
			} else if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
			}
		}*//*
		segments.add(buffer.toString());*//*

		return segments.stream().map(mapper).collect(Collectors.joining());*//**/struct ");
		if (i >= 0) {};
/*final var substring = stripped.substring(i + "class ".length()).strip();*//*
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);*//*
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					final var name = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();
					return "struct " + name + " {};" + System.lineSeparator() +
								 compileStatements(content, Main::compileClassSegment);
				}
			}*//*
		}

		return wrap(stripped);*//*private static String compileClassSegment(String input) {
		return wrap(input);
	}*//*private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*//*}*/