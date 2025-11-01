struct Main {};
/*public static*/ void main(char** args) {/*
		try {
			final var input = Files.readString(Paths.get(".", "src", "main", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "main", "windows", "magma", "Main.c"), compile(input));
		}*//* catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}*//*
	*/}
/*private static*/ char* compile(char* input) {/*
		return compileStatements(input, Main::compileRootSegment);*//*
	*/}
/*private static*/ char* compileStatements(/*String input, Function<String,*/ /*String>*/ mapper) {/*
		final var segments = new ArrayList<String>();*//*
		var buffer = new StringBuffer();*//*
		var depth = 0;*//*
		for (var i = 0;*//* i < input.length();*//* i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
			} else if (c == '}*//*' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
				depth--;
			}*//* else if (c == '{') {
				depth++;
			} else if (c == '}*//*') {
				depth--;
			}*//*
		*/}
/*
		segments.add(buffer.toString());*//*

		return segments.stream().map(mapper).collect(Collectors.joining());*//**//*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		final var i = stripped.indexOf("class ");
		if (i >= 0) {
			final var substring = stripped.substring(i + "class ".length()).strip();
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					final var name = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();
					if (isIdentifier(name)) {
						return "struct " + name + " {};" + System.lineSeparator() +
									 compileStatements(content, Main::compileClassSegment);
					}
				}
			}
		}

		return wrap(stripped);
	}*//*private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}*//*private static String compileClassSegment(String input) {
		final var i = input.indexOf("(");
		if (i >= 0) {
			final var substring = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var substring2 = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);
					final var outputContent = compileStatements(content, Main::compileMethodSegment);

					return compileDefinition(substring) + "(" + compileDefinition(substring2) + ") {" + outputContent + "}" +
								 System.lineSeparator();
				}
			}
		}

		return wrap(input);
	}*//*private static String compileMethodSegment(String input) {
		return wrap(input);
	}*//*private static String compileDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1);
			final var i1 = substring.lastIndexOf(" ");
			if (i1 >= 0) {
				final var substring1 = substring.substring(0, i1);
				final var substring2 = substring.substring(i1 + 1);
				return wrap(substring1) + " " + compileType(substring2) + " " + name;
			} else {
				return compileType(substring) + " " + name;
			}
		}

		return wrap(stripped);
	}*//*private static String compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return "void";
		}

		if (stripped.endsWith("[]")) {
			return compileType(stripped.substring(0, stripped.length() - 2)) + "*";
		}

		if (stripped.equals("String")) {
			return "char*";
		}

		return wrap(stripped);
	}*//*private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*//*}*/