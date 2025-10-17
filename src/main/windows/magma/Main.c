// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Main {};
/*private static class State {
		private final ArrayList<String> segments;
		private final String input;
		private StringBuilder buffer;
		private int depth;
		private int index;

		public State(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(char c) {
			this.buffer.append(c);
			return this;
		}

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		public Optional<Tuple<State, Character>> pop() {
			if (this.index >= this.input.length()) return Optional.empty();
			final char next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<State, Character>(this, next));
		}
	}*/
/*public record Tuple<A, B>(A left, B right) {}*/
/*public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);

			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.c");
			final Path targetParent = target.getParent();

			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
			Files.writeString(target, "// File generated from '" + source + "'. This is not source code!\n" + compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*/
/*private static String compile(String input) {
		final String joined = compileStatements(input, Main::compileRootSegment);
		return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() +
					 "\treturn 0;" + System.lineSeparator() + "}";
	}*/
/*private static String compileStatements(String input, Function<String, String> mapper) {
		return divide(input).map(mapper).collect(Collectors.joining());
	}*/
/*private static Stream<String> divide(String input) {
		State current = new State(input);
		while (true) {
			final Optional<Tuple<State, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<State, Character> tuple = maybeNext.get();
			current = fold(tuple.left, tuple.right);
		}

		return current.advance().stream();
	}*/
/*private static State fold(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}*/
/*' && appended.isShallow()) return appended.advance().exit();*/
/*if (c == '{') return appended.enter();
		if (c == '}*/
/*') return appended.exit();*/
/*return appended;*/
/**/
struct ");
		if (i >= 0) {};
/*final String afterKeyword = stripped.substring(i + "class ".length());*/
/*final int contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				final String beforeContent = afterKeyword.substring(0, contentStart).strip();
				final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
				if (afterContent.endsWith("}")) {
					final String content = afterContent.substring(0, afterContent.length() - "}".length());
					return "struct " + beforeContent + " {};" + System.lineSeparator() +
								 compileStatements(content, Main::compileClassSegment);
				}
			}*/
/*}

		return wrap(stripped);*/
/*private static String compileClassSegment(String input) {
		return wrap(input.strip()) + System.lineSeparator();
	}*//*private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*//*}*/int main(){
	main_Main();
	return 0;
}