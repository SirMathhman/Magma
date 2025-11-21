/*public*/struct Main {};
/*public static*/ void main(char** args){/*
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			Files.writeString(target, compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	*/}
/*private static*/ char* compile(char* input){/*
		return compileStatements(input, Main::compileRootSegment);
	*/}
/*private static*/ char* compileStatements(/*String input, Function<String,*/ /*String>*/ mapper){/*
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		var depth = 0;

		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				continue;
			}
			if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
				continue;
			}
			if (c == '{') {
				depth++;
			}
			if (c == '}') {
				depth--;
			}
		*/}
/*segments.add(buffer.toString());*//*return segments.stream().map(mapper).collect(Collectors.joining());*//*}*//*private static String compileRootSegment(String input) {
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
				if (isIdentifier(name)) {
					return wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() +
								 compileStatements(content, Main::compileClassSegment);
				}
			}
		}

		return wrap(stripped);
	}

	private static boolean isIdentifier(String input) {
		final var stripped = input.strip();
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input) {
		final var stripped = input.strip();
		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declaration = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parameters = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var substring = withBraces.substring(1, withBraces.length() - 1);

					final var compiledParameters = compileDeclaration(parameters);
					return compileDeclaration(declaration) + "(" + compiledParameters + "){" + wrap(substring) + "}" +
								 System.lineSeparator();
				}
			}
		}

		return wrap(stripped);
	}

	private static String compileDeclaration(String input) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator);
			final var name = stripped.substring(nameSeparator + 1).strip();
			final var typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final var substring = beforeName.substring(0, typeSeparator);
				final var substring1 = beforeName.substring(typeSeparator + 1);
				return wrap(substring) + " " + compileType(substring1) + " " + name;
			} else {
				return compileType(beforeName) + " " + name;
			}
		}

		return wrap(stripped);
	}

	private static String compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return "void";
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice) + "*";
		}

		if (stripped.equals("String")) {
			return "char*";
		}

		return wrap(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
}*//**/