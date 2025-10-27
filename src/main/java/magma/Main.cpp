struct Main {};
/*public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.cpp");
			Files.writeString(target, compile(input));

			new ProcessBuilder("clang", target.toAbsolutePath().toString(), "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
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

		final var joined = segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());

		return joined + System.lineSeparator() + "int main(){" + System.lineSeparator() + "\treturn 0;" +
					 System.lineSeparator() + "}";
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		final var classIndex = stripped.indexOf("class");
		if (classIndex >= 0) {
			final var afterKeyword = stripped.substring(classIndex + "class".length());
			final var contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				final var name = afterKeyword.substring(0, contentStart).strip();
				final var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
				if (withEnd.endsWith("}")) {
					final var content = withEnd.substring(0, withEnd.length() - 1);
					return "struct " + name + " {};" + System.lineSeparator() + wrap(content);
				}
			}
		}

		return wrap(input);
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}
*/
int main(){
	return 0;
}