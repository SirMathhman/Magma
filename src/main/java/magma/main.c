struct Main {};
/*public static void main(String[] args) */{/*
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	*/}
/*private static String compile(String input) */{/*
		return compileAll(input, Main::compileRootSegment);
	*/}
/*private static String compileAll(String input, Function<String, String> mapper) */{/*
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			} else {
				if (c == '{') depth++;
				if (c == '}') depth--;
			}
		*/}
/*segments.add(buffer.toString());*/
/*return segments.stream().map(mapper).collect(Collectors.joining());*/
/**/

/*private static String compileRootSegment(String input) */ {};
/*final String stripped = input.strip();*/
/*if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";*/
/*return compileRootSegmentValue(stripped) + System.lineSeparator();*/
/**/

/*private static String compileRootSegmentValue(String input) */ {};
/*if (input.endsWith("*/

/*")) */ {};
/*final String slice = input.substring(0, input.length() - "*/

/*".length());*/
/*final int contentStart = slice.indexOf("*/ {};
/*");*/
/*if (contentStart >= 0) */{/*
				final String beforeBraces = slice.substring(0, contentStart);
				final String afterBraces = slice.substring(contentStart + "{".length());
				return compileClasHeader(beforeBraces) + " {};" + System.lineSeparator() +
							 compileAll(afterBraces, Main::compileClassSegment);
			}
		*/}
/*return wrap(input);*/
/**/

/*private static String compileClassSegment(String input) */ {};
/*return compileClassSegmentValue(input.strip()) + System.lineSeparator();*/
/**/

/*private static String compileClassSegmentValue(String input) */ {};
/*if (input.endsWith("*/

/*")) */ {};
/*final String slice = input.substring(0, input.length() - "*/

/*".length());*/
/*final int i = slice.indexOf("*/ {};
/*");*/
/*if (i >= 0) */{/*
				final String beforeContent = slice.substring(0, i);
				final String content = slice.substring(i + "{".length());
				return wrap(beforeContent) + "{" + wrap(content) + "}";
			}
		*/}
/*return wrap(input);*/
/**/

/*private static String compileClasHeader(String input) */ {};
/*final int index = input.indexOf("class ");*/
/*if (index >= 0) */{/*
			final String slice = input.substring(index + "class ".length());
			return "struct " + slice.strip();
		*/}
/*return wrap(input);*/
/**/

/*private static String wrap(String input) */ {};
/*return "start" + input.replace("start", "start").replace("end", "end") + "end";*/
/**/

/*}*/
