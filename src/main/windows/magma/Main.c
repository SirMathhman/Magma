struct Main {};
/*public static void*/ main(/*String[] args*/)/* {
		try {
			final var input = Files.readString(Paths.get(".", "src", "main", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "main", "windows", "magma", "Main.c"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*/
/*private static String*/ compile(/*String input*/)/* {
		return compileStatements(input, Main::compileRootSegment);
	}*/
/*private static String*/ compileStatements(/*String input, Function<String, String> mapper*/)/* {
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
		}*/
/*segments.add*/(/*buffer.toString(*/)/*);*/
/*return*/ segments.stream(/**/)/*.map(mapper).collect(Collectors.joining());*/
/**//*private static String compileRootSegment(String input) {
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
				final var substring3 = substring1.substring(i1 + 1);
				return compileDefinition(substring) + "(" + wrap(substring2) + ")" + wrap(substring3) + System.lineSeparator();
			}
		}

		return wrap(input);
	}*//*private static String compileDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if(i >= 0) {
			final var substring = stripped.substring(0, i);
			final var name = stripped.substring(i + 1);
			return wrap(substring) + " " + name;
		}

		return wrap(stripped);
	}*//*private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*//*}*/