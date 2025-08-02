/*final */struct Main {
	/*private interface Result<T, X> {
		<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
	}*/
	/*private @interface Actual {}*/
	/*private static final */struct State {
		/*private final*/ struct StringBuilder buffer = struct StringBuilder();
		/*private final*/ template Collection<char*> segments = template ArrayList<>();
		/*private final CharSequence input;*/
		/*private*/ int depth = 0;
		/*private*/ int index = 0;
		struct private State(/*final*/ struct CharSequence input) {
			this.input = input;
		}
		/*private*/ template Stream<char*> stream() {
			return this.segments.stream();
		}
		/*private*/ struct State append(/*final*/ char c) {
			this.buffer.append(c);
			return this;
		}
		/*private*/ struct State enter() {
			this.depth = this.depth + 1;
			return this;
		}
		/*private*/ int isLevel() {
			return 0 == this.depth;
		}
		/*private*/ struct State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}
		/*private*/ struct State exit() {
			this.depth = this.depth - 1;
			return this;
		}
		/*private*/ int isShallow() {
			return 1 == this.depth;
		}
		template Optional<template Tuple<struct State, char>> pop() {
			/*if*/ struct (this.index > = this.input.length(/*)) return Optional.empty(*/);
			/*final*/ auto next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(template Tuple<>(this, next));
		}
	}
	/*private record*/ struct Tuple<A, B>(struct A left, struct B right) {
	}
	/*private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
			return whenOk.apply(this.value);
		}
	}*/
	/*private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
			return whenErr.apply(this.error);
		}
	}*/
	struct private Main() {
	}
	/*public static*/ void main(/*final*/ [char*]* args) {
		/*final*/ auto source = Paths.get(".", "src", "java", "magma", "Main.java");
		/*final*/ auto target = Paths.get(".", "src", "windows", "magma", "Main.c");
		Main.readString(source).match(input - /*> {
			final var output = Main.compile(input);
			return Main.writeString(target*/, /* output);
		}, Optional::of*/).ifPresent(Throwable.printStackTrace);
	}
	/*@Actual
	private static*/ template Optional<struct IOException> writeString(/*final*/ struct Path target, /*final*/ struct CharSequence output) {
		/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
		/*catch (final IOException e) {
			return Optional.of(e);
		}*/
	}
	/*@Actual
	private static*/ template Result<char*, struct IOException> readString(/*final*/ struct Path source) {
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	/*private static*/ char* compile(/*final*/ struct CharSequence input) {
		return Main.compileStatements(input, Main.compileRootSegment);
	}
	/*private static*/ char* compileStatements(/*final*/ struct CharSequence input, /*final*/ char* (*)(char*) mapper) {
		return Main.compileAll(input, mapper, Main.foldStatement, "");
	}
	/*private static*/ char* compileAll(/*final*/ struct CharSequence input, /*final*/ char* (*)(char*) mapper, /*final*/ struct State (*)(struct State, char) folder, /*final*/ struct CharSequence delimiter) {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	/*private static*/ template List<char*> divide(/*final*/ struct CharSequence input, /*final*/ struct State (*)(struct State, char) folder) {
		auto current = struct State(input);
		while (true){ 
			/*final*/ auto popped = current.pop();
			if (popped.isEmpty())
				/*break;*/
			/*final*/ auto tuple = popped.get();
			current = folder.apply(tuple.left, tuple.right);
		}
		return current.advance().stream().toList();
	}
	/*private static State foldStatement(final State current, final char c) {
		final var appended = current.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if*/ struct ('}' = = /* c && appended.isShallow()) return appended.advance().exit()*/;
	/*if ('{' == c || '(' == c) return appended.enter();
		if ('}' == c ||*/ struct ')' = = /* c) return appended.exit()*/;
	/*return appended;*/
}/*private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		final var modifiers = Main.compileClass(strip, 0);
		return modifiers.orElseGet(() -> Main.wrap(strip));
	}*//*private static Optional<String> compileClass(final String input, final int depth) {
		final var index = input.indexOf("*/struct ");
		if (0 > index) return Optional.empty();
		final var modifiers = input.substring(0, index);
		final var withName = input.substring(index + "class ".length());

		final var contentStart = withName.indexOf(' {
	/*');
		if (0*/ struct > contentStart) return Optional.empty();
		final var name = withName.substring(0, contentStart).strip();
		final var withEnd = withName.substring(contentStart + "{".length()).strip();
	/*if (withEnd.isEmpty() || '}*/
	/*' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		return Optional.of(Main.wrap(modifiers) + "struct " + name + " {" +
											 Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth + 1)) +
											 Main.createIndent(depth) + "}*/
	/*");
	}

	private static String compileClassSegment(final String input, final int depth) {
		final var strip = input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth);
	}

	private static String compileClassSegmentValue(final String input, final int depth) {
		return Main.compileClass(input, depth)
							 .or(()*/ struct -> Main.compileField(input, depth))
							 .or(() -> Main.compileMethod(input, depth))
							 .orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileField(final String input, final int depth) {
		final var strip = /* input.strip();
		if (strip.isEmpty() || '*/;
	/*' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final*/ auto input1 = strip.substring(0, /* strip.length(*/) - /*1);
		return Main.compileInitialization(input1, depth).map(result */ - /*> result*/ + ";
	/*");
	}

	private static*/ template Optional<char*> compileInitialization(/*final*/ char* input, /*final*/ int depth) {
		/*final var valueSeparator*/ struct = input.lastIndexOf(' = /*')*/;
		if (/*0 > valueSeparator*/)
			return Optional.empty();
		/*final*/ auto definition = input.substring(0, valueSeparator);
		/*final*/ auto value = input.substring(valueSeparator + 1);
		/*final*/ auto destination = Main.compileDefinition(definition).orElseGet(() - /*> Main.compileValueOrPlaceholder(definition*/, /* depth)*/);
		return Optional.of(destination + " = " + Main.compileValueOrPlaceholder(value, depth));
		/*}

	private static String compileValueOrPlaceholder(final String input, final int depth) {
		return Main.compileValue(input, depth).orElseGet(() -> Main.wrap(input));*/
		/*}

	private static Optional*/ < /*String> compileValue(final String input, final int depth) {
		final var strip = input*/.strip();
		return Main.compileInvokable(strip, depth).or(() - /*> Main*/.compileNumber(strip)).or(() - /*> Main.compileAccess(strip*/, ".", /* depth)*/).or(() - /*> Main.compileAccess(strip*/, "::", /* depth)*/).or(() - /*> Main.compileLambda(strip*/, /* depth)*/).or(() - /*> Main.compileOperator(strip*/, " == ", /* depth)*/).or(() - /*> Main.compileOperator(strip*/, " + ", /* depth)*/).or(() - /*> Main.compileOperator(strip*/, " - ", /* depth)*/).or(() - /*> Main.compileOperator(strip*/, " < /*", depth)*/).or(() - /*> Main*/.compileIdentifier(strip)).or(() - /*> Main*/.compileString(strip));
		/*}

	private static Optional*/ < /*String> compileLambda(final String input, final int depth) {
		final var index = input*/.indexOf(" - /*>"*/);
		if (/*0 > index*/)
			return Optional.empty();
		/*final*/ auto name = input.substring(0, index).strip();
		/*final var after = input.substring(index */ + " - /*>"*/.length(/*)*/).strip();
		/*if (after.isEmpty() || '{' != after.charAt(0) || '*/
	}
	/*' != after.charAt(after.length() - 1)) return Optional.empty();

		final var content = after.substring(1, after.length() - 1);
		return Optional.of(Main.assembleFunction(depth, "auto " + name, "auto ?", content));
	}

	private static Optional<String> compileString(final String input) {
		return !input.isEmpty() && '\"' == input.charAt(0) && '\"' == input.charAt(input.length() - 1) ? Optional.of(input)
																																																	 : Optional.empty();
	}

	private static Optional<String> compileOperator(final String input, final String operator, final int depth) {
		final var index = input.indexOf(operator);
		if (0*/ struct > index) return Optional.empty();
		final var left = input.substring(0, index);
		final var right = input.substring(index + operator.length());

		return Optional.of(Main.compileValueOrPlaceholder(left, depth) + " " + operator + " " +
											 Main.compileValueOrPlaceholder(right, depth));
	}

	private static Optional<String> compileIdentifier(final String input) {
		if (Main.isIdentifier(input)) return Optional.of(input);
		else return Optional.empty();
	}

	private static boolean isIdentifier(final CharSequence input) {
		final var length = input.length();
		for (int i = 0;
	/*i < length;*/
	/*i++) {
			if (Character.isLetter(input.charAt(i))) continue;*/
	/*return false;*/
	/*}
		return true;
	}

	private static Optional<String> compileAccess(final String input, final String delimiter, final int depth) {
		final var index = input.lastIndexOf(delimiter);
		if (0*/ struct > index) return Optional.empty();

		final var before = input.substring(0, index);
		final var property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property)) return Optional.empty();

		return Main.compileValue(before, depth).map(result -> result + "." + property);
	}

	private static Optional<String> compileNumber(final String input) {
		if (Main.isNumber(input)) return Optional.of(input);
		else return Optional.empty();
	}

	private static boolean isNumber(final CharSequence input) {
		final var length = input.length();
		for (var i = 0;
	/*i < length;*/
	/*i++) {
			final*/ auto c = input.charAt(i);
	/*if (Character.isDigit(c)) continue;*/
	/*return false;*/
	/*}

		return true;
	}

	private static*/ template Optional<char*> compileInvokable(/*final*/ char* input, /*final*/ int depth) {
		/*if (input.isEmpty() || ')' != input.charAt(input.length() */ - /*1)) return Optional.empty();
		final var withoutEnd = input.substring(0, input.length() */ - /*1);

		final var divisions = Main.divide(withoutEnd, Main::foldInvocationStart);
		if (2 > divisions.size()) return Optional.empty();

		final var withParamStart = String.join("", divisions.subList(0, divisions.size() */ - /*1));
		final var arguments = divisions.getLast();

		if (withParamStart.isEmpty() || '(' != withParamStart.charAt(withParamStart.length() */ - /* 1)) return Optional*/.empty();
		/*final*/ auto caller = withParamStart.substring(0, withParamStart.length() - 1);
		/*final*/ auto outputArguments = /*arguments.isEmpty() ? "" : Main.compileValues(arguments,
																																							input1 */ - /*> Main.compileValueOrPlaceholder(
																																									input1, depth))*/;
		return Main.compileConstructor(caller).or(() - /*> Main.compileValue(caller*/, /* depth)*/).map(result - /*> result*/ + "(" + outputArguments + ")");
		/*}

	private static State foldInvocationStart(final State state, final Character next) {
		final*/ auto appended = state.append(next);
		/*if ('(' == next) {
			final*/ auto enter = /* appended.enter();
			if (enter.isShallow()) return enter.advance();
			else return enter*/;
	}
	/*if (')' == next) return appended.exit();
		return appended;
	}

	private static Optional<String> compileConstructor(final String input) {
		if (input.startsWith("new ")) {
			final*/ auto slice = input.substring("new ".length());
	/*final*/ auto output = Main.compileType(slice);
	/*return Optional.of(output);*/
	/*}

		return Optional.empty();
	}

	private static Optional<String> compileMethod(final String input, final int depth) {
		final*/ auto paramStart = input.indexOf(/*'('*/);
	/*if (0 > paramStart) return Optional.empty();*/
	/*final*/ auto definition = input.substring(0, paramStart);
	/*final*/ auto withParams = input.substring(paramStart + 1);
	/*final var paramEnd = withParams.indexOf(')');
		if (0 > paramEnd) return Optional.empty();
		final var params = withParams.substring(0, paramEnd);
		final var withBraces = withParams.substring(paramEnd + 1).strip();

		final var newParams = params.isEmpty() ? "" : Main.compileValues(params, Main::compileDefinitionOrPlaceholder);
		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}*/
	/*' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();

		final var content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of(
				Main.assembleFunction(depth, newParams, Main.compileDefinitionOrPlaceholder(definition), content));
	}

	private static String assembleFunction(final int depth,
																				 final String params,
																				 final String definition,
																				 final CharSequence content) {
		return definition + "(" + params + ") {" + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) +
					 "}";
	}

	private static String compileFunctionSegments(final int depth, final CharSequence content) {
		return Main.compileStatements(content, input1 -> Main.compileFunctionSegment(input1, depth + 1));
	}

	private static String compileValues(final CharSequence input, final Function<String,*/ struct String> mapper) {
		return Main.compileAll(input, mapper, Main::foldValue, ", ");
	}

	private static String createIndent(final int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static String compileFunctionSegment(final String input, final int depth) {
		final var strip = /*input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) */ + /* Main.compileFunctionSegmentValue(strip, depth);
	}

	private static String compileFunctionSegmentValue(final String input, final int depth) {
		if (!input.isEmpty() && '*/;
	/*' == input.charAt(input.length() - 1)) {
			final*/ auto withoutEnd = input.substring(0, input.length() - 1);
	/*final*/ auto maybe = Main.compileFunctionStatementValue(withoutEnd, depth);
	/*if (maybe.isPresent()) return maybe.get() + ";*/
	/*";*/
	/*}

		return Main.compileConditional(input, depth, "while")
							 .or(()*/ struct -> Main.compileConditional(input, depth, "if"))
							 .orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileConditional(final String input, final int depth, final String type) {
		if (!input.startsWith(type)) return Optional.empty();
		final var withoutStart = input.substring(type.length()).strip();

		if (withoutStart.isEmpty() || '(' ! = withoutStart.charAt(/*0)) return Optional.empty(*/);
	/*final*/ auto withCondition = withoutStart.substring(1);
	/*final*/ auto divisions = Main.divide(withCondition, Main.foldConditionEnd);
	/*if (2 > divisions.size()) return Optional.empty();*/
	/*final*/ auto withEnd = String.join("", divisions.subList(0, divisions.size() - 1));
	/*final*/ auto maybeWithBraces = divisions.getLast();
	/*if (withEnd.isEmpty() || ')' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var condition = withEnd.substring(0, withEnd.length() - 1);

		final var before = type + " (" + Main.compileValueOrPlaceholder(condition, depth) + ")";
		return Optional.of(before + Main.compileWithBraces(depth, maybeWithBraces)
																		.orElseGet(()*/ struct -> Main.compileFunctionSegment(maybeWithBraces, depth + 1)));
	}

	private static State foldConditionEnd(final State state, final Character next) {
		final var appended = state.append(next);
		if ('(' = = /* next) return appended.enter()*/;
	/*if (')' == next) if (appended.isLevel()) return appended.advance();
		else return appended.exit();
		return appended;
	}

	private static Optional<String> compileWithBraces(final int depth, final String input) {
		final var withBraces = input.strip();

		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}*/
	/*' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		final var content = withBraces.substring(1, withBraces.length() - 1);

		return Optional.of("{ " + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) + "}*/
	/*");
	}

	private static Optional<String> compileFunctionStatementValue(final String input, final int depth) {
		if (input.startsWith("return ")) {
			final*/ auto value = input.substring("return ".length());
	/*return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth));*/
	/*}

		return Main.compileInvokable(input, depth)
							 .or(()*/ struct -> Main.compileInitialization(input, depth))
							 .or(() -> Main.compilePostFix(input, depth));
	}

	private static Optional<String> compilePostFix(final String input, final int depth) {
		if (!input.endsWith("++")) return Optional.empty();
		final var slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth).map(result -> result + "++");
	}

	private static State foldValue(final State state, final char next) {
		if (',' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('(' == next || '<' = = /* next) return appended.enter()*/;
	/*if (')' == next || '>' == next) return appended.exit();
		return appended;
	}

	private static String compileDefinitionOrPlaceholder(final String input) {
		return Main.compileDefinition(input).orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileDefinition(final String input) {
		final var strip = input.strip();
		final var index = strip.lastIndexOf(' ');
		if (0 > index) return Optional.empty();
		final var beforeName = strip.substring(0, index);
		final var name = strip.substring(index + " ".length());

		final var divisions = Main.divide(beforeName, Main::foldTypeSeparator);
		if (2 > divisions.size()) return Optional.of(Main.compileType(beforeName) + " " + name);

		final var beforeType = String.join(" ", divisions.subList(0, divisions.size() - 1));
		final var type = divisions.getLast();

		return Optional.of(Main.wrap(beforeType) + " " + Main.compileType(type) + " " + name);
	}

	private static State foldTypeSeparator(final State state, final Character next) {
		if (' ' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('<' == next) return appended.enter();
		if ('>' == next) return appended.exit();
		return appended;
	}

	private static String compileType(final String input) {
		final var strip = input.strip();
		if ("int".contentEquals(strip) || "boolean".contentEquals(strip)) return "int";
		if ("var".contentEquals(strip)) return "auto";
		if ("void".contentEquals(strip)) return "void";
		if ("char".contentEquals(strip) || "Character".contentEquals(strip)) return "char";
		if ("String".contentEquals(strip)) return "char*";

		return Main.compileGenericType(strip).or(() -> Main.compileArrayType(strip)).orElseGet(() -> "struct " + strip);
	}

	private static Optional<String> compileGenericType(final String strip) {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var withoutEnd = strip.substring(0, strip.length() - 1);

		final var index = withoutEnd.indexOf('<');
		if (0 > index) return Optional.empty();
		final var base = withoutEnd.substring(0, index);
		final var inputArguments = withoutEnd.substring(index + "<".length());

		final var outputArgs = Main.beforeTypeArguments(inputArguments);
		if (base.contentEquals("Function"))
			return Optional.of(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ")");
		if ("BiFunction".contentEquals(base))
			return Optional.of(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ", " + outputArgs.get(1) + ")");

		final var outputArgsString = String.join(", ", outputArgs);
		return Optional.of("template " + base + "<" + outputArgsString + ">");
	}

	private static List<String> beforeTypeArguments(final CharSequence input) {
		if (input.isEmpty()) return Collections.emptyList();
		return Main.divide(input, Main::foldValue).stream().map(Main::compileType).toList();
	}

	private static Optional<String> compileArrayType(final String input) {
		if (!input.endsWith("[]")) return Optional.empty();
		final var withoutEnd = input.substring(0, input.length() - "[]".length());
		final var slice = Main.compileType(withoutEnd);

		return Optional.of("[" + slice + "]*");
	}

	private static String wrap(final String input) {
		return "/*" + input + "*/";
	}*/
}/**/