// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Main {};
struct State {};
/*private final*/ ArrayList<char*> segments;
/*private final*/ char* input;
/*private*/ /*StringBuilder*/ buffer;
/*private*/ int depth;
/*private*/ int index;
/*public State(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
		}*/
/*private Stream<String> stream() {
			return this.segments.stream();
		}*/
/*private State enter() {
			this.depth = this.depth + 1;
			return this;
		}*/
/*private State exit() {
			this.depth = this.depth - 1;
			return this;
		}*/
/*private boolean isShallow() {
			return this.depth == 1;
		}*/
/*private boolean isLevel() {
			return this.depth == 0;
		}*/
/*private State append(char c) {
			this.buffer.append(c);
			return this;
		}*/
/*private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}*/
/*public Optional<Tuple<State, Character>> pop() {
			if (this.index >= this.input.length()) return Optional.empty();
			final char next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<State, Character>(this, next));
		}*/
/*public Optional<Tuple<State, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<State, Character>(tuple.left.append(tuple.right), tuple.right));
		}*/
/*public Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}*/
/**/

/*public record Tuple<A, B>(A left, B right) {}*/
/*public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);

			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
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
			current = foldEscaped(tuple.left, tuple.right);
		}

		return current.advance().stream();
	}*/
/*private static State foldEscaped(State state, char next) {
		return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next)).orElseGet(() -> fold(state, next));
	}*/
/*private static Optional<State> foldSingleQuotes(State state, char next) {
		if (next != '\'') return Optional.empty();

		final State appended = state.append(next);
		return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(State::popAndAppendToOption);
	}*/
/*private static Optional<State> foldEscaped(Tuple<State, Character> tuple) {
		if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
		else return Optional.of(tuple.left);
	}*/
/*private static Optional<State> foldDoubleQuotes(State state, char next) {
		if (next != '\"') return Optional.empty();

		State appended = state.append(next);
		while (true) {
			final Optional<Tuple<State, Character>> maybeNext = appended.popAndAppendToTuple();
			if (maybeNext.isPresent()) {
				final Tuple<State, Character> tuple = maybeNext.get();
				appended = tuple.left;

				final char c = tuple.right;
				if (c == '\\') appended = appended.popAndAppendToOption().orElse(appended);
				if (c == '\"') break;
			} else break;
		}

		return Optional.of(appended);
	}*/
/*private static State fold(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}*/
/*private static String compileRootSegment(String input) {
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		return compileClass(stripped).orElseGet(() -> wrap(stripped));
	}*/
struct ");
		if (i < 0) return Optional.empty();
		final String afterKeyword = stripped.substring(i + "class ".length());
		final int contentStart = afterKeyword.indexOf(" {};
/*");

		if (contentStart < 0) return Optional.empty();
		final String beforeContent = afterKeyword.substring(0, contentStart).strip();
		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterContent.endsWith("}*/
/*")) return Optional.empty();
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		return Optional.of("struct " + beforeContent + " {};" + System.lineSeparator() +
											*/ /*compileStatements(content,*/ Main::compileClassSegment));

/*private static String compileClassSegment(String input) {
		final String stripped = input.strip();
		return compileClassSegmentValue(stripped) + System.lineSeparator();
	}*/
/*private static String compileClassSegmentValue(String input) {
		return compileClass(input).or(() -> compileField(input)).orElseGet(() -> wrap(input));
	}*/
/*private static Optional<String> compileField(String input) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			return Optional.of(compileDefinition(substring) + ";");
		} else return Optional.empty();
	}*/
/*private static String compileDefinition(String input) {
		final int index = input.lastIndexOf(" ");
		if (index >= 0) {
			final String beforeName = input.substring(0, index).strip();
			final String name = input.substring(index + " ".length()).strip();
			final int typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final String beforeType = beforeName.substring(0, typeSeparator);
				final String type = beforeName.substring(typeSeparator + " ".length());
				return wrap(beforeType) + " " + compileType(type) + " " + name;
			}
		}

		return wrap(input);
	}*/
/*private static String compileType(String input) {
		final String stripped = input.strip();
		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String arguments = withoutEnd.substring(argumentStart + "<".length());
				return base + "<" + compileType(arguments) + ">";
			}
		}

		if (stripped.equals("String")) return "char*";
		if (stripped.equals("int")) return "int";

		return wrap(stripped);
	}*/
/*private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*/
/**/
/**/int main(){
	main_Main();
	return 0;
}