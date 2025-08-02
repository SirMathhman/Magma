/*final */struct Main {
	/*private interface Result<T, X> {
		<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
	}*/
	/*private static final */struct State {
		/*private final*/ struct StringBuilder buffer = struct StringBuilder();
		/*private final*/ template Collection<char*> segments = template ArrayList<>();
		/*private*/ int depth = 0;
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
	/*private Main*/() {
	}
	/*public static*/ void main(/*final*/ [char*]* args) {
		/*final var source = Paths*/.get(/*"."*/, /*"src"*/, /*"java"*/, /*"magma"*/, /*"Main.java"*/);
		/*final var target = Paths*/.get(/*"."*/, /*"src"*/, /*"windows"*/, /*"magma"*/, /*"Main.c"*/);
		/*Main.readString(source).match(input -> {
			final var output = Main.compile(input);
			return Main.writeString(target, output);
		}*/
		/*, Optional::of)*/.ifPresent(/*Throwable::printStackTrace*/);
	}
	/*private static*/ template Optional<struct IOException> writeString(/*final*/ struct Path target, /*final*/ struct CharSequence output) {
		/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
		/*catch (final IOException e) {
			return Optional.of(e);
		}*/
	}
	/*private static Result<String,*/ struct IOException> readString(/*final*/ struct Path source) {
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	/*private static*/ char* compile(/*final*/ struct CharSequence input) {
		return Main.compileStatements(input, /*Main::compileRootSegment*/);
	}
	/*private static*/ char* compileStatements(/*final*/ struct CharSequence input, /* final Function<String*/, /* String> mapper*/) {
		return Main.compileAll(input, mapper, /*Main::foldStatement*/, /*""*/);
	}
	/*private static*/ char* compileAll(/*final*/ struct CharSequence input, /*
																	 final Function<String*/, /* String> mapper*/, /*
																	 final BiFunction<State*/, /* Character*/, /* State> folder*/, /*final*/ char* delimiter) {
		/*final var length = input*/.length();
		/*var current = new State*/();
		/*for*/ struct (var i = 0;
		/*i < length;*/
		/*i++) {
			final var next = input.charAt(i);
			current = folder.apply(current, next);
		}*/
		return current.advance(/*)*/.stream(/*).map(mapper).collect(Collectors.joining(delimiter*/));
	}
	/*private static*/ struct State foldStatement(/*final*/ struct State current, /*final*/ char c) {
		/*final var appended = current*/.append(c);
		/*if (';*/
		/*'*/ == /*c && appended*/.isLevel(/*)) return appended.advance(*/);
		/*if ('*/
	}
	/*' =*/ = /*c && appended*/.isShallow(/*)) return appended.advance().exit(*/);
	/*if ('{' == c) return appended.enter();
		if ('}*/
	/*' =*/ = /*c) return appended*/.exit();
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
	/*');*/
	/*if (0 > contentStart) return Optional.empty();*/
	/*final*/ struct var name = withName.substring(0, /*contentStart).strip(*/);
	/*final var withEnd = withName.substring(contentStart + "{".length()).strip();

		if (withEnd.isEmpty() || '}*/
	/*' !*/ = withEnd.charAt(withEnd.length() - /*1)) return Optional.empty(*/);
	/*final*/ struct var content = withEnd.substring(0, withEnd.length() - 1);
	/*return Optional.of(Main.wrap(modifiers) + "struct " + name + " {" +
											 Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth + 1)) +
											 Main.createIndent(depth) + "}*/
	/*");*/
	/*}

	private static String compileClassSegment(final String input, final int depth) {
		final*/ struct var strip = input.strip();
	/*if (strip.isEmpty()) return "";*/
	/*return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth);*/
	/*}

	private static String compileClassSegmentValue(final String input, final int depth) {
		return Main.compileClass(input, depth)
							 .or(() -> Main.compileField(input))
							 .or(() -> Main.compileMethod(input, depth))
							 .orElseGet(() -> Main.wrap(input));*/
	/*}

	private static Optional<String> compileField(final String input) {
		final*/ struct var strip = input.strip();
	/*if (strip.isEmpty() || ';*/
	/*' !*/ = strip.charAt(strip.length() - /*1)) return Optional.empty(*/);
	/*final*/ struct var input1 = strip.substring(0, strip.length() - 1);
	/*return Main.compileInitialization(input1).map(result -> result + ";*/
	/*");*/
	/*}

	private static Optional<String> compileInitialization(final String input) {
		final var valueSeparator*/ struct = input.lastIndexOf(' = /*')*/;
	/*if (0 > valueSeparator) return Optional.empty();*/
	/*final*/ struct var definition = input.substring(0, valueSeparator);
	/*final*/ struct var value = input.substring(valueSeparator + 1);
	/*final*/ struct var destination = Main.compileDefinition(/*definition)*/.orElseGet(() - /*> Main.compileValue(definition*/));
	/*return Optional.of(destination*/ struct + " = /*"*/ + Main.compileValue(/*value)*/);
	/*}

	private static String compileValue(final String input) {
		final*/ struct var strip = input.strip();
	/*return Main.compileInvokable(strip)
							 .or(() -> Main.compileNumber(strip))
							 .or(() ->*/ struct Main.compileOperator(strip, "= = /*"))*/.or(() - /*> Main.compileOperator(strip*/, /*"*/ + /*"))*/.or(/*(*/) - /*> Main.compileOperator(strip*/, /*"*/ - /*"))*/.or(() - /*> Main*/.compileAccess(/*strip))
							 .or((*/) - /*> Main*/.compileIdentifier(/*strip))
							 .orElseGet((*/) - /*> Main.wrap(strip*/));
	/*}

	private static Optional<String> compileOperator(final String input, final String operator) {
		final*/ struct var index = input.indexOf(operator);
	/*if (0 > index) return Optional.empty();*/
	/*final*/ struct var left = input.substring(0, index);
	/*final*/ struct var right = input.substring(index + operator.length());
	/*return Optional.of(Main.compileValue(left) + " " + operator + " " + Main.compileValue(right));*/
	/*}

	private static Optional<String> compileIdentifier(final String input) {
		if (Main.isIdentifier(input)) return Optional.of(input);*/
	/*else return Optional.empty();*/
	/*}

	private static boolean isIdentifier(final CharSequence input) {
		final*/ struct var length = input.length();
	/*for*/ struct (int i = 0;
	/*i < length;*/
	/*i++) {
			if (Character.isLetter(input.charAt(i))) continue;
			return false;
		}*/
	/*return true;*/
	/*}

	private static Optional<String> compileAccess(final String input) {
		final*/ struct var index = input.lastIndexOf(/*'.'*/);
	/*if (0 > index) return Optional.empty();*/
	/*final*/ struct var before = input.substring(0, index);
	/*final*/ struct var property = input.substring(index + /*1).strip(*/);
	/*if (!Main.isIdentifier(property)) return Optional.empty();*/
	/*return Optional.of(Main.compileValue(before) + "." + property);*/
	/*}

	private static Optional<String> compileNumber(final String input) {
		if (Main.isNumber(input)) return Optional.of(input);*/
	/*else return Optional.empty();*/
	/*}

	private static boolean isNumber(final CharSequence input) {
		final*/ struct var length = input.length();
	/*for*/ struct (var i = 0;
	/*i < length;*/
	/*i++) {
			final var c = input.charAt(i);
			if (Character.isDigit(c)) continue;
			return false;
		}*/
	/*return true;*/
	/*}

	private static Optional<String> compileInvokable(final String input) {
		if (input.isEmpty() ||*/ struct ')' ! = input.charAt(input.length() - /*1)) return Optional.empty(*/);
	/*final*/ struct var withoutEnd = input.substring(0, input.length() - 1);
	/*final*/ struct var argStart = withoutEnd.indexOf(/*'('*/);
	/*if (0 > argStart) return Optional.empty();*/
	/*final*/ struct var inputCaller = withoutEnd.substring(0, argStart);
	/*final*/ struct var arguments = withoutEnd.substring(argStart + /*"*/(/*".length(*/));
	/*final*/ struct var outputArguments = arguments.isEmpty(/*) ? "" : Main.compileValues(arguments*/, /*Main::compileValue*/);
	/*final*/ struct var outputCaller = Main.compileConstructor(/*inputCaller)*/.orElseGet(() - /*> Main.compileValue(inputCaller*/));
	/*return Optional.of(outputCaller + "(" + outputArguments + ")");*/
	/*}

	private static*/ template Optional<char*> compileConstructor(/*final*/ char* input) {
		if(/*input.startsWith("new ")) {
			final var slice = input.substring("new ".length());
			final var output = Main.compileType(slice);
			return Optional.of(output*/);
	}
	/*return Optional.empty();*/
	/*}

	private static Optional<String> compileMethod(final String input, final int depth) {
		final*/ struct var paramStart = input.indexOf(/*'('*/);
	/*if (0 > paramStart) return Optional.empty();*/
	/*final*/ struct var definition = input.substring(0, paramStart);
	/*final*/ struct var withParams = input.substring(paramStart + 1);
	/*final*/ struct var paramEnd = withParams.indexOf(/*')'*/);
	/*if (0 > paramEnd) return Optional.empty();*/
	/*final*/ struct var params = withParams.substring(0, paramEnd);
	/*final*/ struct var withBraces = withParams.substring(paramEnd + /*1).strip(*/);
	/*final*/ struct var newParams = params.isEmpty(/*) ? "" : Main.compileValues(params*/, /*Main::compileDefinitionOrPlaceholder*/);
	/*if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}*/
	/*' !*/ = withBraces.charAt(withBraces.length() - /*1))
			return Optional.empty(*/);
	/*final*/ struct var content = withBraces.substring(1, withBraces.length() - 1);
	/*return Optional.of(Main.compileDefinitionOrPlaceholder(definition) + "(" + newParams + ") {" +
											 Main.compileStatements(content, input1 -> Main.compileFunctionSegment(input1, depth + 1)) +
											 Main.createIndent(depth) + "}*/
	/*");*/
	/*}

	private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
		return Main.compileAll(input, mapper, Main::foldValue, ", ");*/
	/*}

	private static String createIndent(final int depth) {
		return System.lineSeparator() + "\t".repeat(depth);*/
	/*}

	private static String compileFunctionSegment(final String input, final int depth) {
		final*/ struct var strip = input.strip();
	/*if (strip.isEmpty()) return "";*/
	/*return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip);*/
	/*}

	private static String compileFunctionSegmentValue(final String input) {
		if (!input.isEmpty() && ';*/
	/*' == input.charAt(input.length() - 1)) {
			final var withoutEnd = input.substring(0, input.length() - 1);
			final var maybe = Main.compileFunctionStatementValue(withoutEnd);
			if (maybe.isPresent()) return maybe.get() + ";";
		}*/
	/*return Main.wrap(input);*/
	/*}

	private static*/ template Optional<char*> compileFunctionStatementValue(/*final*/ char* input) {
		if(input.startsWith(/*"return ")) {
			final var value = input.substring("return ".length());
			return Optional.of("return "*/ + /*Main.compileValue(value*/));
	}
	/*return Main.compileInvokable(input).or(() -> Main.compileInitialization(input));*/
	/*}

	private static State foldValue(final State state, final char next) {
		if*/ struct (',' = = /*next) return state*/.advance();
	/*return state.append(next);*/
	/*}

	private static String compileDefinitionOrPlaceholder(final String input) {
		return Main.compileDefinition(input).orElseGet(() -> Main.wrap(input));*/
	/*}

	private static Optional<String> compileDefinition(final String input) {
		final*/ struct var strip = input.strip();
	/*final*/ struct var index = strip.lastIndexOf(/*' '*/);
	/*if (0 > index) return Optional.empty();*/
	/*final*/ struct var beforeName = strip.substring(0, index);
	/*final*/ struct var name = strip.substring(index + /*" "*/.length());
	/*final*/ struct var i = beforeName.lastIndexOf(/*' '*/);
	/*if (0 > i) return Optional.empty();*/
	/*final*/ struct var beforeType = beforeName.substring(0, i);
	/*final*/ struct var type = beforeName.substring(i + /*" "*/.length());
	/*return Optional.of(Main.wrap(beforeType) + " " + Main.compileType(type) + " " + name);*/
	/*}

	private static String compileType(final String input) {
		final*/ struct var strip = input.strip();
	/*if ("int".contentEquals(strip) || "boolean".contentEquals(strip)) return "int";*/
	/*if ("void".contentEquals(strip)) return "void";*/
	/*if ("char".contentEquals(strip)) return "char";*/
	/*if ("String".contentEquals(strip)) return "char*";*/
	/*if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
			final var withoutEnd = strip.substring(0, strip.length() - 1);
			final var index = withoutEnd.indexOf('<');
			if (0 <= index) {
				final var base = withoutEnd.substring(0, index);
				final var arguments = withoutEnd.substring(index + "<".length());
				final var outputArguments = arguments.isEmpty() ? "" : Main.compileType(arguments);
				return "template " + base + "<" + outputArguments + ">";
			}
		}*/
	/*return Main.compileArrayType(strip).orElseGet(() -> "struct " + input);*/
	/*}

	private static Optional<String> compileArrayType(final String input) {
		if (!input.endsWith("[]")) return Optional.empty();*/
	/*final*/ struct var withoutEnd = input.substring(0, input.length(/*)*/ - /*"[]".length(*/));
	/*final*/ struct var slice = Main.compileType(withoutEnd);
	/*return Optional.of("[" + slice + "]*");*/
	/*}

	private static String wrap(final String input) {
		return "/*" + input + "*/";*/
	/*}*/
}/**/