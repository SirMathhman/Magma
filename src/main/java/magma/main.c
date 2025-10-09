struct Main {};
/*public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}*/
/*private static String compile(String input) {
		return compileStatements(input, Main::compileRootSegment);
	}*/
/*private static String compileStatements(String input, Function<String, String> mapper) {
		final ArrayList<String> segments = new ArrayList<String>();
		final StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer.setLength(0);
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				depth--;
			} else {
				if (c == '{') depth++;
				if (c == '}') depth--;
			}
		}*/
/*segments.add(buffer.toString());*/
/*return segments.stream().map(mapper).collect(Collectors.joining());*/
/**/
/*private static String compileRootSegment(String input) */{};
/*final String strip = input.strip();*/
/*if (strip.startsWith("package ") || strip.startsWith("import ")) return "";*/
/*if (strip.endsWith("*/
/*")) */{};
/*final String withoutEnd = strip.substring(0, strip.length() - "*/
/*".length());*//*final int index = withoutEnd.indexOf("*/{};
/*");*/
/*if (index >= 0) {
				final String substring = withoutEnd.substring(0, index);
				final String body = withoutEnd.substring(index + "{".length());
				return compileStructureHeader(substring) + "{};" + System.lineSeparator() +
							 compileStatements(body, Main::compileClassSegment);
			}
		}*/
/*return wrap(strip);*/
/**/
/*private static String compileClassSegment(String input) */{};
/*return wrap(input.strip()) + System.lineSeparator();*/
/**/
/*private static String compileStructureHeader(String input) */{};
/*final int index = input.indexOf("class ");*/
/*if (index >= 0) {
			final String name = input.substring(index + "class ".length());
			return "struct " + name;
		}*/
/*return wrap(input);*/
/**/
/*private static String wrap(String input) */{};
/*return "start" + input.replace("start", "start").replace("end", "end") + "end";*/
/**/
/*}*/