package magma;

import magma.node.CDefinition;
import magma.node.JavaConstructor;
import magma.node.JavaMethodHeader;
import magma.node.JavaParameter;
import magma.node.Placeholder;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Main {
	private static final List<String> RESERVED_KEYWORDS = List.of("new", "private");
	private static final SequencedCollection<List<String>> typeParams = new ArrayList<>();

	private Main() {}

	public static void main(final String[] args) {
		final var source = Paths.get(".", "src", "java", "magma", "Main.java");
		final var target = Paths.get(".", "src", "windows", "magma", "Main.c");

		Main.readString(source).match(input -> {
			final var output = Main.compile(input);
			return Main.writeString(target, output);
		}, Some::new).ifPresent(Throwable::printStackTrace);
	}

	@Actual
	private static Option<IOException> writeString(final Path target, final CharSequence output) {
		try {
			Files.writeString(target, output);
			return new None<>();
		} catch (final IOException e) {
			return new Some<>(e);
		}
	}

	@Actual
	private static Result<String, IOException> readString(final Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static String compile(final CharSequence input) {
		return Main.compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
		return Main.compileAll(input, mapper, Main::foldStatement, "");
	}

	private static String compileAll(final CharSequence input,
																	 final Function<String, String> mapper,
																	 final BiFunction<DivideState, Character, DivideState> folder,
																	 final CharSequence delimiter) {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}

	private static List<String> divide(final CharSequence input,
																		 final BiFunction<DivideState, Character, DivideState> folder) {
		var current = new DivideState(input);
		while (true) {
			final var popped = current.pop().toTuple(new Tuple<>(current, '\0'));
			if (!popped.left()) break;

			final var tuple = popped.right();
			current = Main.foldDecorated(folder, tuple.left(), tuple.right());
		}

		return current.advance().stream().toList();
	}

	private static DivideState foldDecorated(final BiFunction<DivideState, Character, DivideState> folder,
																					 final DivideState state,
																					 final char next) {
		return Main.foldSingleQuotes(state, next)
							 .or(() -> Main.foldDoubleQuotes(state, next))
							 .orElseGet(() -> folder.apply(state, next));
	}

	private static Option<DivideState> foldDoubleQuotes(final DivideState state, final char next) {
		if ('\"' != next) return new None<>();
		var current = state.append('\"');

		while (true) {
			final var maybeTuple = current.popAndAppendToTuple().toTuple(new Tuple<>(current, '\0'));

			if (!maybeTuple.left()) break;

			final var tuple = maybeTuple.right();
			current = tuple.left();

			if ('\\' == tuple.right()) current = current.popAndAppendToOption().orElse(current);
			if ('\"' == tuple.right()) break;
		}

		return new Some<>(current);
	}

	private static Option<DivideState> foldSingleQuotes(final DivideState state, final char next) {
		if ('\'' != next) return new None<>();

		return state.append('\'')
								.popAndAppendToTuple()
								.flatMap(tuple -> Main.foldEscapeChar(tuple.left(), tuple.right()))
								.flatMap(DivideState::popAndAppendToOption);
	}

	private static Option<DivideState> foldEscapeChar(final DivideState state, final Character next) {
		if ('\\' == next) return state.popAndAppendToOption();
		return new Some<>(state);
	}

	private static DivideState foldStatement(final DivideState current, final char c) {
		final var appended = current.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('}' == c && appended.isShallow()) return appended.advance().exit();
		if ('{' == c || '(' == c) return appended.enter();
		if ('}' == c || ')' == c) return appended.exit();
		return appended;
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("package ") || strip.startsWith("import ")) return "";
		final var modifiers = Main.compileStructure("class", strip, 0);
		return modifiers.orElseGet(() -> Placeholder.wrap(strip));
	}

	private static Option<String> compileStructure(final String type, final String input, final int depth) {
		final var index = input.indexOf(type + " ");
		if (0 > index) return new None<>();
		final var withName = input.substring(index + (type + " ").length());

		final var contentStart = withName.indexOf('{');
		if (0 > contentStart) return new None<>();
		final var beforeContent = withName.substring(0, contentStart).strip();
		final var withEnd = withName.substring(contentStart + "{".length()).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return new None<>();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		if (!beforeContent.isEmpty() && '>' == beforeContent.charAt(beforeContent.length() - 1)) {
			final var withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
			final var i = withoutEnd.indexOf('<');
			if (0 <= i) {
				final var name = withoutEnd.substring(0, i);
				final var generics = withoutEnd.substring(i + 1);
				final var typeParams = Main.divide(generics, Main::foldValue).stream().map(String::strip).toList();

				Main.typeParams.add(typeParams);
				final var generated = Main.assembleStructure(depth, content, name, typeParams);
				Main.typeParams.removeLast();

				return generated;
			}
		}

		return Main.assembleStructure(depth, content, beforeContent, Collections.emptyList());
	}

	private static Option<String> assembleStructure(final int depth,
																									final CharSequence content,
																									final CharSequence name,
																									final Collection<String> typeParams) {
		final var outputContent =
				Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth + 1, name));
		final String typeParamsString;
		if (typeParams.isEmpty()) typeParamsString = "";
		else
			typeParamsString = "<" + String.join(", ", typeParams) + "> ";
		return new Some<>("struct " + name + typeParamsString + " {" + outputContent + Main.createIndent(depth) + "}");
	}

	private static String compileClassSegment(final String input, final int depth, final CharSequence structName) {
		final var strip = input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth, structName);
	}

	private static String compileClassSegmentValue(final String input, final int depth, final CharSequence structName) {
		return Main.compileStructure("class", input, depth)
							 .or(() -> Main.compileStructure("interface", input, depth))
							 .or(() -> Main.compileStructure("record", input, depth))
							 .or(() -> Main.compileMethod(input, depth, structName))
							 .or(() -> Main.compileField(input, depth, structName))
							 .orElseGet(() -> Placeholder.wrap(input));
	}

	private static Option<String> compileField(final String input, final int depth, final CharSequence structName) {
		final var strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1)) return new None<>();
		final var withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth, structName)
							 .or(() -> Main.parseDefinition(withoutEnd).map(CDefinition::generate))
							 .map(result -> result + ";");
	}

	private static Option<String> compileInitialization(final String input,
																											final int depth,
																											final CharSequence structName) {
		final var valueSeparator = input.indexOf('=');
		if (0 > valueSeparator) return new None<>();

		final var definition = input.substring(0, valueSeparator);
		final var value = input.substring(valueSeparator + 1);

		final var destination = Main.parseDefinition(definition)
																.map(CDefinition::generate)
																.orElseGet(() -> Main.compileValueOrPlaceholder(definition, depth, structName));

		return Main.compileValue(value, depth, structName).map(result -> destination + " = " + result);
	}

	private static String compileValueOrPlaceholder(final String input, final int depth, final CharSequence structName) {
		return Main.compileValue(input, depth, structName).orElseGet(() -> Placeholder.wrap(input));
	}

	private static Option<String> compileValue(final String input, final int depth, final CharSequence structName) {
		final var strip = input.strip();
		return Main.compileLambda(strip, depth, structName)
							 .or(() -> Main.compileNumber(strip))
							 .or(() -> Main.compileInvokable(strip, depth, structName))
							 .or(() -> Main.compileAccess(strip, ".", depth, structName))
							 .or(() -> Main.compileAccess(strip, "::", depth, structName))
							 .or(() -> Main.compileString(strip))
							 .or(() -> Main.compileChar(strip))
							 .or(() -> Main.compileOperator(strip, "==", depth, structName))
							 .or(() -> Main.compileOperator(strip, "!=", depth, structName))
							 .or(() -> Main.compileOperator(strip, "+", depth, structName))
							 .or(() -> Main.compileOperator(strip, "-", depth, structName))
							 .or(() -> Main.compileOperator(strip, "<", depth, structName))
							 .or(() -> Main.compileOperator(strip, "&&", depth, structName))
							 .or(() -> Main.compileOperator(strip, "||", depth, structName))
							 .or(() -> Main.compileOperator(strip, ">=", depth, structName))
							 .or(() -> Main.compileOperator(strip, ">", depth, structName))
							 .or(() -> Main.compileIdentifier(strip))
							 .or(() -> Main.compileNot(depth, strip, structName));
	}

	private static Option<String> compileChar(final String input) {
		if (!input.isEmpty() && '\'' == input.charAt(0) && '\'' == input.charAt(input.length() - 1))
			return new Some<>(input);
		else return new None<>();
	}

	private static Option<String> compileNot(final int depth, final String strip, final CharSequence structName) {
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return new Some<>("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth, structName));
		return new None<>();
	}

	private static Option<String> compileLambda(final String input, final int depth, final CharSequence structName) {
		final var index = input.indexOf("->");
		if (0 > index) return new None<>();
		final var name = input.substring(0, index).strip();
		final var after = input.substring(index + "->".length()).strip();
		final List<JavaParameter> params;
		if (name.contentEquals("()")) params = Collections.emptyList();
		else if (Main.isIdentifier(name))
			params = Collections.singletonList(new CDefinition("auto", name));
		else return new None<>();

		final JavaMethodHeader definition = new CDefinition("auto", "?");
		if (after.isEmpty() || '{' != after.charAt(0) || '}' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth, structName)
								 .flatMap(value -> Main.assembleFunction(depth, params, definition,
																												 Main.createIndent(depth + 1) + "return " + value, structName));
		final var content = after.substring(1, after.length() - 1);
		return Main.assembleFunction(depth, params, definition, Main.compileFunctionSegments(depth, content, structName),
																 structName);
	}

	private static Option<String> compileString(final String input) {
		if (Main.isString(input)) return new Some<>(input);
		return new None<>();
	}

	private static boolean isString(final CharSequence input) {
		return !input.isEmpty() && '\"' == input.charAt(0) && '\"' == input.charAt(input.length() - 1);
	}

	private static Option<String> compileOperator(final CharSequence input,
																								final CharSequence operator,
																								final int depth,
																								final CharSequence structName) {
		final var divisions = Main.divide(input, (state, next) -> Main.foldOperator(operator, state, next));
		if (2 > divisions.size()) return new None<>();

		final var left = divisions.getFirst();
		final var right = divisions.getLast();

		return Main.compileValue(left, depth, structName)
							 .flatMap(leftResult -> Main.compileValue(right, depth, structName)
																					.map(rightResult -> leftResult + " " + operator + " " + rightResult));
	}

	private static DivideState foldOperator(final CharSequence operator, final DivideState state, final Character next) {
		return Main.tryAdvanceAtOperator(operator, state, next).orElseGet(() -> Main.getState(state, next));
	}

	private static DivideState getState(final DivideState state, final Character next) {
		final var appended = state.append(next);
		if ('(' == next) return appended.enter();
		if (')' == next) return appended.exit();
		return appended;
	}

	private static Option<DivideState> tryAdvanceAtOperator(final CharSequence operator,
																													final DivideState state,
																													final Character next) {
		if (!state.isLevel() || next != operator.charAt(0)) return new None<>();

		if (1 == operator.length()) return new Some<>(state.advance());
		if (2 != operator.length()) return new None<>();

		if (state.hasNextChar(operator.charAt(1))) return new Some<>(state.pop().map(Tuple::left).orElse(state).advance());
		return new None<>();
	}

	private static Option<String> compileIdentifier(final String input) {
		if (Main.isIdentifier(input)) return new Some<>(input);
		else return new None<>();
	}

	private static boolean isIdentifier(final String input) {
		if (input.isEmpty() || Main.RESERVED_KEYWORDS.contains(input)) return false;

		return IntStream.range(0, input.length()).allMatch(index -> Main.isIdentifierChar(input, index));
	}

	private static boolean isIdentifierChar(final CharSequence input, final int index) {
		final var next = input.charAt(index);
		if (0 == index) return Character.isLetter(next);
		return Character.isLetterOrDigit(next);
	}

	private static Option<String> compileAccess(final String input,
																							final String delimiter,
																							final int depth,
																							final CharSequence structName) {
		final var index = input.lastIndexOf(delimiter);
		if (0 > index) return new None<>();

		final var before = input.substring(0, index);
		final var property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property)) return new None<>();

		return Main.compileValue(before, depth, structName).map(result -> result + "." + property);
	}

	private static Option<String> compileNumber(final String input) {
		if (Main.isNumber(input)) return new Some<>(input);
		else return new None<>();
	}

	private static boolean isNumber(final CharSequence input) {
		final var length = input.length();
		return IntStream.range(0, length).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private static Option<String> compileInvokable(final String input, final int depth, final CharSequence structName) {
		if (input.isEmpty() || ')' != input.charAt(input.length() - 1)) return new None<>();
		final var withoutEnd = input.substring(0, input.length() - 1);

		final var divisions = Main.divide(withoutEnd, Main::foldInvocationStart);
		if (2 > divisions.size()) return new None<>();

		final var withParamStart = String.join("", divisions.subList(0, divisions.size() - 1));
		final var arguments = divisions.getLast();

		if (withParamStart.isEmpty() || '(' != withParamStart.charAt(withParamStart.length() - 1)) return new None<>();
		final var caller = withParamStart.substring(0, withParamStart.length() - 1);

		final String outputArguments;
		if (arguments.isEmpty()) outputArguments = "";
		else outputArguments =
				Main.compileValues(arguments, input1 -> Main.compileValueOrPlaceholder(input1, depth, structName));

		return Main.compileConstructor(caller)
							 .or(() -> Main.compileValue(caller, depth, structName))
							 .map(result -> result + "(" + outputArguments + ")");
	}

	private static DivideState foldInvocationStart(final DivideState state, final Character next) {
		final var appended = state.append(next);
		if ('(' == next) {
			final var enter = appended.enter();
			if (enter.isShallow()) return enter.advance();
			else return enter;
		}
		if (')' == next) return appended.exit();
		return appended;
	}

	private static Option<String> compileConstructor(final String input) {
		if (input.startsWith("new ")) {
			final var slice = input.substring("new ".length());
			final var output = Main.compileTypeOrPlaceholder(slice);
			return new Some<>(output);
		}

		return new None<>();
	}

	private static Option<String> compileMethod(final String input, final int depth, final CharSequence structName) {
		final var paramStart = input.indexOf('(');
		if (0 > paramStart) return new None<>();
		final var definitionString = input.substring(0, paramStart).strip();
		final var withParams = input.substring(paramStart + 1);

		final var paramEnd = withParams.indexOf(')');
		if (0 > paramEnd) return new None<>();
		final var paramsString = withParams.substring(0, paramEnd);
		final var withBraces = withParams.substring(paramEnd + 1).strip();

		return Main.parseDefinition(definitionString)
							 .<JavaMethodHeader>map(value -> value)
							 .or(() -> Main.parseConstructor(structName, definitionString).map(value -> value))
							 .flatMap(definable -> Main.getStringOptional(depth, structName, definable, paramsString, withBraces));
	}

	private static Option<String> getStringOptional(final int depth,
																									final CharSequence structName,
																									final JavaMethodHeader definable,
																									final CharSequence paramsString,
																									final String withBraces) {
		if (definable instanceof final CDefinition definition)
			Main.typeParams.add(definition.maybeTypeParameter().stream().toList());

		final var params = Main.getList(paramsString);

		if (definable instanceof CDefinition) Main.typeParams.removeLast();

		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1)) {
			final String definition1 = definable.generate();
			return new Some<>(Main.generateFunction(params, definition1, ";"));
		}

		final var content = withBraces.substring(1, withBraces.length() - 1);
		return Main.assembleFunction(depth, params, definable, Main.compileFunctionSegments(depth, content, structName),
																 structName);
	}

	private static List<JavaParameter> getList(final CharSequence paramsString) {
		return Main.divide(paramsString, Main::foldValue)
							 .stream()
							 .map(String::strip)
							 .filter(segment -> !segment.isEmpty())
							 .map(Main::parseParameter)
							 .toList();
	}

	private static JavaParameter parseParameter(final String input) {
		return Main.parseDefinition(input).<JavaParameter>map(value -> value).orElseGet(() -> new Placeholder(input));
	}

	private static Option<JavaConstructor> parseConstructor(final CharSequence structName,
																													final String definitionString) {
		final var i = definitionString.lastIndexOf(' ');
		if (0 <= i) {
			final var substring = definitionString.substring(i + 1).strip();
			if (substring.contentEquals(structName)) return new Some<>(new JavaConstructor());
		}

		return new None<>();
	}

	private static Option<String> assembleFunction(final int depth,
																								 final Collection<JavaParameter> oldParams,
																								 final JavaMethodHeader oldHeader,
																								 final String content,
																								 final CharSequence structName) {
		final SequencedCollection<JavaParameter> newParams = new ArrayList<>(oldParams);

		switch (oldHeader) {
			case JavaConstructor() -> {
				final JavaParameter newHeader = new CDefinition("struct " + structName, "new_" + structName);
				final String newContent =
						Main.createIndent(depth + 1) + "struct " + structName + " this;" + content + Main.createIndent(depth + 1) +
						"return this;";
				return new Some<>(
						Main.generateFunction(newParams, newHeader.generate(), " {" + newContent + Main.createIndent(depth) + "}"));
			}
			case CDefinition(
					final Option<String> maybeTypeParameter, final String type, final String name
			) -> {
				final CDefinition newHeader;
				final String newContent;
				newHeader = new CDefinition(maybeTypeParameter, type, name + "_" + structName);
				newParams.addFirst(new CDefinition("void*", "__self__"));
				newContent =
						Main.createIndent(depth + 1) + "struct " + structName + " this = *(struct " + structName + "*) __self__;" +
						content;
				return new Some<>(
						Main.generateFunction(newParams, newHeader.generate(), " {" + newContent + Main.createIndent(depth) + "}"));
			}
			case null, default -> {
				return new None<>();
			}
		}
	}

	private static String generateFunction(final Collection<JavaParameter> params,
																				 final String definition,
																				 final String content) {
		final var joinedParams = params.stream().map(JavaParameter::generate).collect(Collectors.joining(", "));
		return definition + "(" + joinedParams + ")" + content;
	}

	private static String compileFunctionSegments(final int depth,
																								final CharSequence content,
																								final CharSequence structName) {
		return Main.compileStatements(content, input1 -> Main.compileFunctionSegment(input1, depth + 1, structName));
	}

	private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
		return Main.compileAll(input, mapper, Main::foldValue, ", ");
	}

	private static String createIndent(final int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static String compileFunctionSegment(final String input, final int depth, final CharSequence structName) {
		final var strip = input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth, structName);
	}

	private static String compileFunctionSegmentValue(final String input,
																										final int depth,
																										final CharSequence structName) {
		return Main.compileConditional(input, depth, "while", structName)
							 .or(() -> Main.compileConditional(input, depth, "if", structName))
							 .or(() -> Main.compileElse(input, depth, structName))
							 .or(() -> Main.compileFunctionStatement(input, depth, structName))
							 .orElseGet(() -> Placeholder.wrap(input));
	}

	private static Option<String> compileElse(final String input, final int depth, final CharSequence structName) {
		if (input.startsWith("else")) {
			final var substring = input.substring("else".length());
			return new Some<>("else " + Main.compileFunctionStatement(substring, depth, structName));
		} else return new None<>();
	}

	private static Option<String> compileFunctionStatement(final String input,
																												 final int depth,
																												 final CharSequence structName) {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return new None<>();

		final var withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth, structName).map(result -> result + ";");
	}

	private static Option<String> compileBreak(final CharSequence input) {
		if ("break".contentEquals(input)) return new Some<>("break");
		else return new None<>();
	}

	private static Option<String> compileConditional(final String input,
																									 final int depth,
																									 final String type,
																									 final CharSequence structName) {
		if (!input.startsWith(type)) return new None<>();
		final var withoutStart = input.substring(type.length()).strip();

		if (withoutStart.isEmpty() || '(' != withoutStart.charAt(0)) return new None<>();
		final var withCondition = withoutStart.substring(1);

		final var divisions = Main.divide(withCondition, Main::foldConditionEnd);
		if (2 > divisions.size()) return new None<>();

		final var withEnd = String.join("", divisions.subList(0, divisions.size() - 1));
		final var maybeWithBraces = divisions.getLast();

		if (withEnd.isEmpty() || ')' != withEnd.charAt(withEnd.length() - 1)) return new None<>();
		final var condition = withEnd.substring(0, withEnd.length() - 1);

		final var before = type + " (" + Main.compileValueOrPlaceholder(condition, depth, structName) + ")";
		return new Some<>(before + Main.compileWithBraces(depth, maybeWithBraces, structName)
																	 .orElseGet(
																			 () -> Main.compileFunctionSegment(maybeWithBraces, depth + 1, structName)));
	}

	private static DivideState foldConditionEnd(final DivideState state, final Character next) {
		final var appended = state.append(next);
		if ('(' == next) return appended.enter();
		if (')' == next) if (appended.isLevel()) return appended.advance();
		else return appended.exit();
		return appended;
	}

	private static Option<String> compileWithBraces(final int depth, final String input, final CharSequence structName) {
		final var withBraces = input.strip();

		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1))
			return new None<>();
		final var content = withBraces.substring(1, withBraces.length() - 1);

		return new Some<>("{ " + Main.compileFunctionSegments(depth, content, structName) + Main.createIndent(depth) + "}");
	}

	private static Option<String> compileFunctionStatementValue(final String input,
																															final int depth,
																															final CharSequence structName) {
		if (input.startsWith("return ")) {
			final var value = input.substring("return ".length());
			return new Some<>("return " + Main.compileValueOrPlaceholder(value, depth, structName));
		}

		return Main.compileInvokable(input, depth, structName)
							 .or(() -> Main.compileInitialization(input, depth, structName))
							 .or(() -> Main.parseDefinition(input).map(CDefinition::generate))
							 .or(() -> Main.compilePostFix(input, depth, structName))
							 .or(() -> Main.compileBreak(input));
	}

	private static Option<String> compilePostFix(final String input, final int depth, final CharSequence structName) {
		if (!input.endsWith("++")) return new None<>();
		final var slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth, structName).map(result -> result + "++");
	}

	private static DivideState foldValue(final DivideState state, final char next) {
		if (',' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('-' == next && state.hasNextChar('>')) return appended.popAndAppendToOption().orElse(appended);

		if ('(' == next || '<' == next) return appended.enter();
		if (')' == next || '>' == next) return appended.exit();
		return appended;
	}

	private static Option<CDefinition> parseDefinition(final String input) {
		final var strip = input.strip();
		final var index = strip.lastIndexOf(' ');
		if (0 > index) return new None<>();
		final var beforeName = strip.substring(0, index);
		final var name = strip.substring(index + " ".length());

		final var divisions = Main.divide(beforeName, Main::foldTypeSeparator);
		if (2 > divisions.size())
			return Main.compileType(beforeName).map(type -> new CDefinition(new None<>(), type, name));

		final var joined = String.join(" ", divisions.subList(0, divisions.size() - 1)).strip();

		final var typeString = divisions.getLast();

		if (!joined.isEmpty() && '>' == joined.charAt(joined.length() - 1)) {
			final var withoutEnd = joined.substring(0, joined.length() - 1);
			final var typeParamStart = withoutEnd.lastIndexOf('<');
			if (0 <= typeParamStart) {
				final var typeParameterString = withoutEnd.substring(typeParamStart + 1).strip();
				Main.typeParams.add(List.of(typeParameterString));

				final var definition = Main.assembleDefinition(new Some<>(typeParameterString), typeString, name);
				Main.typeParams.removeLast();
				return definition;
			}
		}

		return Main.assembleDefinition(new None<>(), typeString, name);
	}

	private static Option<CDefinition> assembleDefinition(final Option<String> maybeTypeParameter,
																												final String typeString,
																												final String name) {
		final var maybeType = Main.compileType(typeString);
		if (maybeType.isEmpty()) return new None<>();
		final var type = maybeType.orElseGet(() -> Placeholder.wrap(typeString));
		final var generated = new CDefinition(maybeTypeParameter, type, name);
		return new Some<>(generated);
	}

	private static DivideState foldTypeSeparator(final DivideState state, final Character next) {
		if (' ' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('<' == next) return appended.enter();
		if ('>' == next) return appended.exit();
		return appended;
	}

	private static String compileTypeOrPlaceholder(final String input) {
		return Main.compileType(input).orElseGet(() -> Placeholder.wrap(input));
	}

	private static Option<String> compileType(final String input) {
		final var strip = input.strip();

		if ("int".contentEquals(strip) || "boolean".contentEquals(strip)) return new Some<>("int");
		if ("var".contentEquals(strip)) return new Some<>("auto");
		if ("void".contentEquals(strip)) return new Some<>("void");
		if ("char".contentEquals(strip) || "Character".contentEquals(strip)) return new Some<>("char");
		if ("String".contentEquals(strip)) return new Some<>("struct String");

		if (Main.typeParams.stream().anyMatch(frame -> frame.contains(strip))) return new Some<>("typeparam " + strip);
		return Main.compileGenericType(strip)
							 .or(() -> Main.compileArrayType(strip))
							 .or(() -> Main.compileStructureType(strip));
	}

	private static Option<String> compileStructureType(final String input) {
		if (Main.isIdentifier(input)) return new Some<>("struct " + input);
		return new None<>();
	}

	private static Option<String> compileGenericType(final String strip) {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1)) return new None<>();
		final var withoutEnd = strip.substring(0, strip.length() - 1);

		final var index = withoutEnd.indexOf('<');
		if (0 > index) return new None<>();
		final var base = withoutEnd.substring(0, index);
		final var inputArguments = withoutEnd.substring(index + "<".length());

		final var outputArgs = Main.beforeTypeArguments(inputArguments);
		if (base.contentEquals("Function")) return new Some<>(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ")");
		if ("BiFunction".contentEquals(base))
			return new Some<>(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ", " + outputArgs.get(1) + ")");

		final var outputArgsString = String.join(", ", outputArgs);
		return new Some<>("template " + base + "<" + outputArgsString + ">");
	}

	private static List<String> beforeTypeArguments(final CharSequence input) {
		if (input.isEmpty()) return Collections.emptyList();
		return Main.divide(input, Main::foldValue).stream().map(Main::compileTypeOrPlaceholder).toList();
	}

	private static Option<String> compileArrayType(final String input) {
		if (!input.endsWith("[]")) return new None<>();
		final var withoutEnd = input.substring(0, input.length() - "[]".length());
		final var slice = Main.compileTypeOrPlaceholder(withoutEnd);

		return new Some<>("[" + slice + "]*");
	}
}