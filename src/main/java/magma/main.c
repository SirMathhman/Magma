struct Main {};
/*
	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String compile(String input) {
		final ArrayList<String> segments = new ArrayList<String>();
		final StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer.setLength(0);
			} else {
				if (c == '{') depth++;
				if (c == '}') depth--;
			}
		}

		segments.add(buffer.toString());
		return segments.stream()
									 .map(String::strip)
									 .filter(segment -> !segment.startsWith("package ") && !segment.startsWith("import "))
									 .map(Main::compileRootSegment)
									 .collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		if (input.endsWith("}")) {
			final String withoutEnd = input.substring(0, input.length() - "}".length());
			final int index = withoutEnd.indexOf("{");
			if (index >= 0) {
				final String substring = withoutEnd.substring(0, index);
				final String substring1 = withoutEnd.substring(index + "{".length());
				return compileStructureHeader(substring) + "{};" + System.lineSeparator() + wrap(substring1);
			}
		}

		return wrap(input);
	}

	private static String compileStructureHeader(String input) {
		final int index = input.indexOf("class ");
		if (index >= 0) {
			final String name = input.substring(index + "class ".length());
			return "struct " + name;
		}

		return wrap(input);
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}
*/