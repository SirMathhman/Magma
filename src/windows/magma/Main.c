struct Main {
	struct Result<T, X>  {
		<R> typeparam R match(typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr);
	}
	struct Actual {
	}
	struct Definable {
		struct String generate();
	}
	struct MethodHeader extends Definable {
	}
	struct State {
		struct StringBuilder buffer = struct StringBuilder();
		template Collection<struct String> segments = template ArrayList<>();
		struct CharSequence input;
		int depth = 0;
		int index = 0;
		struct State new(struct CharSequence input) {
			struct State this;
			this.input = input;
			return this;
		}
		int hasNextChar(char c) {
			auto peek = this.peek();
			return peek.isPresent() && peek.get().equals(c);
		}
		template Stream<struct String> stream() {
			return this.segments.stream();
		}
		struct State append(char c) {
			this.buffer.append(c);
			return this;
		}
		struct State enter() {
			this.depth = this.depth + 1;
			return this;
		}
		int isLevel() {
			return 0 == this.depth;
		}
		struct State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}
		struct State exit() {
			this.depth = this.depth - 1;
			return this;
		}
		int isShallow() {
			return 1 == this.depth;
		}
		template Optional<template Tuple<struct State, char>> pop() {
			if (this.index >= this.input.length())
				return Optional.empty();
			auto next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(template Tuple<>(this, next));
		}
		template Optional<template Tuple<struct State, char>> popAndAppendToTuple() {
			return this.pop().map(auto ?(auto tuple) {
				return template Tuple<>(tuple.left.append(tuple.right), tuple.right)
			});
		}
		template Optional<struct State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(auto ?(auto tuple) {
				return tuple.left
			});
		}
		template Optional<char> peek() {
			if (this.index < this.input.length())
				return Optional.of(this.input.charAt(this.index));
			return Optional.empty();
		}
	}
	struct Tuple<A, B>(A left, B right) {
	}
	struct Ok<T, X>(T value) implements Result<T, X>  {
		<R> typeparam R match(typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			return whenOk.apply(this.value);
		}
	}
	struct Err<T, X>(X error) implements Result<T, X>  {
		<R> typeparam R match(typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			return whenErr.apply(this.error);
		}
	}
	struct Definition(Optional<String> maybeTypeParameter, String type, String name) implements MethodHeader {
		/*private Definition(final String type, final String name) {
			this(Optional.empty(), type, name);
		}*/
		struct String generate() {
			return this.maybeTypeParameter.map(auto ?(auto value) {
				return "<" + value + "> "
			}).orElse("") + this.name;
		}
	}
	struct Placeholder(String value) implements Definable {
		struct String generate() {
			return Main.wrap(this.value);
		}
	}
	struct Constructor(CharSequence structName) implements MethodHeader {
		struct String generate() {
			return "struct " + this.structName + " new";
		}
	}
	template List<struct String> RESERVED_KEYWORDS = List.of("new", "private");
	template SequencedCollection<template List<struct String>> typeParams = template ArrayList<>();
	struct Main new() {
		struct Main this;
		return this;
	}
	void main([struct String]* args) {
		auto source = Paths.get(".", "src", "java", "magma", "Main.java");
		auto target = Paths.get(".", "src", "windows", "magma", "Main.c");
		Main.readString(source).match(auto ?(auto input) {
			auto output = Main.compile(input);
			return Main.writeString(target, output);
		}, Optional.of).ifPresent(Throwable.printStackTrace);
	}
	template Optional<struct IOException> writeString(struct Path target, struct CharSequence output) {
		/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
		/*catch (final IOException e) {
			return Optional.of(e);
		}*/
	}
	template Result<struct String, struct IOException> readString(struct Path source) {
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	struct String compile(struct CharSequence input) {
		return Main.compileStatements(input, Main.compileRootSegment);
	}
	struct String compileStatements(struct CharSequence input, struct String (*)(struct String) mapper) {
		return Main.compileAll(input, mapper, Main.foldStatement, "");
	}
	struct String compileAll(struct CharSequence input, struct String (*)(struct String) mapper, struct State (*)(struct State, char) folder, struct CharSequence delimiter) {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	template List<struct String> divide(struct CharSequence input, struct State (*)(struct State, char) folder) {
		auto current = struct State(input);
		while (true){ 
			auto popped = current.pop();
			if (popped.isEmpty())
				break;
			auto tuple = popped.get();
			current = Main.foldDecorated(folder, tuple.left, tuple.right);
		}
		return current.advance().stream().toList();
	}
	struct State foldDecorated(struct State (*)(struct State, char) folder, struct State state, char next) {
		return Main.foldSingleQuotes(state, next).or(auto ?() {
			return Main.foldDoubleQuotes(state, next)
		}).orElseGet(auto ?() {
			return folder.apply(state, next)
		});
	}
	template Optional<struct State> foldDoubleQuotes(struct State state, char next) {
		if ('\"' != next)
			return Optional.empty();
		auto current = state.append('\"');
		while (true){ 
			auto maybeTuple = current.popAndAppendToTuple();
			if (maybeTuple.isEmpty())
				break;
			auto tuple = maybeTuple.get();
			current = tuple.left;
			if ('\\' == tuple.right)
				current = current.popAndAppendToOption().orElse(current);
			if ('\"' == tuple.right)
				break;
		}
		return Optional.of(current);
	}
	template Optional<struct State> foldSingleQuotes(struct State state, char next) {
		if ('\'' != next)
			return Optional.empty();
		return state.append('\'').popAndAppendToTuple().flatMap(auto ?(auto tuple) {
			return Main.foldEscapeChar(tuple.left, tuple.right)
		}).flatMap(State.popAndAppendToOption);
	}
	template Optional<struct State> foldEscapeChar(struct State state, char next) {
		if ('\\' == next)
			return state.popAndAppendToOption();
		return Optional.of(state);
	}
	struct State foldStatement(struct State current, char c) {
		auto appended = current.append(c);
		if (';' == c && appended.isLevel())
			return appended.advance();
		if ('}' == c && appended.isShallow())
			return appended.advance().exit();
		if ('{' == c)
			return appended.enter();
		if ('}' == c)
			return appended.exit();
		return appended;
	}
	struct String compileRootSegment(struct String input) {
		auto strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("import "))
			return "";
		auto modifiers = Main.compileStructure("class", strip, 0);
		return modifiers.orElseGet(auto ?() {
			return Main.wrap(strip)
		});
	}
	template Optional<struct String> compileStructure(struct String type, struct String input, int depth) {
		auto index = input.indexOf(type + " ");
		if (0 > index)
			return Optional.empty();
		auto withName = input.substring(index + (type + " ").length());
		auto contentStart = withName.indexOf('{');
		if (0 > contentStart)
			return Optional.empty();
		auto beforeContent = withName.substring(0, contentStart).strip();
		auto withEnd = withName.substring(contentStart + "{".length()).strip();
		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1))
			return Optional.empty();
		auto content = withEnd.substring(0, withEnd.length() - 1);
		if (!beforeContent.isEmpty() && '>' == beforeContent.charAt(beforeContent.length() - 1)){ 
			auto withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
			auto i = withoutEnd.indexOf('<');
			if (/*0 <= i*/){ 
				auto name = withoutEnd.substring(0, i);
				auto generics = withoutEnd.substring(i + 1);
				auto typeParams = Main.divide(generics, Main.foldValue).stream().map(String.strip).toList();
				Main.typeParams.add(typeParams);
				auto generated = Main.assembleStructure(depth, content, name, typeParams);
				Main.typeParams.removeLast();
				return generated;
			}
		}
		return Main.assembleStructure(depth, content, beforeContent, Collections.emptyList());
	}
	template Optional<struct String> assembleStructure(int depth, struct CharSequence content, struct CharSequence name, template Collection<struct String> typeParams) {
		auto outputContent = Main.compileStatements(content, auto ?(auto input1) {
			return Main.compileClassSegment(input1, depth + 1, name)
		});
		struct String typeParamsString;
		if (typeParams.isEmpty())
			typeParamsString = "";
		else Optional[typeParamsString = "<" + String.join(", ", typeParams) + "> ";]
		return Optional.of("struct " + name + typeParamsString + " {" + outputContent + Main.createIndent(depth) + "}");
	}
	struct String compileClassSegment(struct String input, int depth, struct CharSequence structName) {
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth, structName);
	}
	struct String compileClassSegmentValue(struct String input, int depth, struct CharSequence structName) {
		return Main.compileStructure("class", input, depth).or(auto ?() {
			return Main.compileStructure("interface", input, depth)
		}).or(auto ?() {
			return Main.compileStructure("record", input, depth)
		}).or(auto ?() {
			return Main.compileMethod(input, depth, structName)
		}).or(auto ?() {
			return Main.compileField(input, depth)
		}).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileField(struct String input, int depth) {
		auto strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth).or(auto ?() {
			return Main.parseDefinition(withoutEnd).map(Definition.generate)
		}).map(auto ?(auto result) {
			return result + ";"
		});
	}
	template Optional<struct String> compileInitialization(struct String input, int depth) {
		auto valueSeparator = input.indexOf('=');
		if (0 > valueSeparator)
			return Optional.empty();
		auto definition = input.substring(0, valueSeparator);
		auto value = input.substring(valueSeparator + 1);
		auto destination = Main.parseDefinition(definition).map(Definition.generate).orElseGet(auto ?() {
			return Main.compileValueOrPlaceholder(definition, depth)
		});
		return Main.compileValue(value, depth).map(auto ?(auto result) {
			return destination + result
		});
	}
	struct String compileValueOrPlaceholder(struct String input, int depth) {
		return Main.compileValue(input, depth).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileValue(struct String input, int depth) {
		auto strip = input.strip();
		return Main.compileLambda(strip, depth).or(auto ?() {
			return Main.compileNumber(strip)
		}).or(auto ?() {
			return Main.compileInvokable(strip, depth)
		}).or(auto ?() {
			return Main.compileAccess(strip, ".", depth)
		}).or(auto ?() {
			return Main.compileAccess(strip, "::", depth)
		}).or(auto ?() {
			return Main.compileString(strip)
		}).or(auto ?() {
			return Main.compileChar(strip)
		}).or(auto ?() {
			return Main.compileOperator(strip, "==", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "!=", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "+", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "-", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "<", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "&&", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, "||", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, ">=", depth)
		}).or(auto ?() {
			return Main.compileOperator(strip, ">", depth)
		}).or(auto ?() {
			return Main.compileIdentifier(strip)
		}).or(auto ?() {
			return Main.compileNot(depth, strip)
		});
	}
	template Optional<struct String> compileChar(struct String input) {
		if (!input.isEmpty() && '\'' == input.charAt(input.length() - 1))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileNot(int depth, struct String strip) {
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return Optional.of("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth));
		return Optional.empty();
	}
	template Optional<struct String> compileLambda(struct String input, int depth) {
		auto index = input.indexOf("->");
		if (0 > index)
			return Optional.empty();
		auto name = input.substring(0, index).strip();
		auto after = input.substring(index + "->".length()).strip();
		struct String params;
		if (name.contentEquals("()"))
			params = "";
		else Optional[/* if (Main.isIdentifier(name)) params */ = "auto " + name;]
		else Optional[struct return Optional.empty();]
		struct MethodHeader definition = struct Definition("auto", "?");
		if (after.isEmpty() || '{' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth).map(auto ?(auto value) {
				return Main.assembleFunction(depth, params, definition, Main.createIndent(depth + 1) + value)
			});
		auto content = after.substring(1, after.length() - 1);
		return Optional.of(Main.assembleFunction(depth, params, definition, Main.compileFunctionSegments(depth, content)));
	}
	template Optional<struct String> compileString(struct String input) {
		if (Main.isString(input))
			return Optional.of(input);
		return Optional.empty();
	}
	int isString(struct CharSequence input) {
		return !input.isEmpty() && '\"' == input.charAt(input.length() - 1);
	}
	template Optional<struct String> compileOperator(struct CharSequence input, struct CharSequence operator, int depth) {
		auto divisions = Main.divide(input, (state, next) -  > Main.foldOperator(operator, state, next));
		if (2 > divisions.size())
			return Optional.empty();
		auto left = divisions.getFirst();
		auto right = divisions.getLast();
		return Main.compileValue(left, depth).flatMap(auto ?(auto leftResult) {
			return Main.compileValue(right, depth).map(auto ?(auto rightResult) {
			return leftResult + rightResult
		})
		});
	}
	struct State foldOperator(struct CharSequence operator, struct State state, char next) {
		auto state1 = Main.tryAdvanceAtOperator(operator, state, next);
		if (state1.isPresent())
			return state1.get();
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Optional<struct State> tryAdvanceAtOperator(struct CharSequence operator, struct State state, char next) {
		if (!state.isLevel() || next != operator.charAt(0))
			return Optional.empty();
		if (1 == operator.length())
			return Optional.of(state.advance());
		if (2 != operator.length())
			return Optional.empty();
		if (state.hasNextChar(operator.charAt(1)))
			return Optional.of(state.pop().map(auto ?(auto tuple) {
				return tuple.left
			}).orElse(state).advance());
		return Optional.empty();
	}
	template Optional<struct String> compileIdentifier(struct String input) {
		if (Main.isIdentifier(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isIdentifier(struct String input) {
		if (/*input.isEmpty() || Main.RESERVED_KEYWORDS.contains(input)*/)
			return false;
		return IntStream.range(0, input.length()).allMatch(auto ?(auto index) {
			return Main.isIdentifierChar(input, index)
		});
	}
	int isIdentifierChar(struct CharSequence input, int index) {
		auto next = input.charAt(index);
		if (0 == index)
			return Character.isLetter(next);
		return Character.isLetterOrDigit(next);
	}
	template Optional<struct String> compileAccess(struct String input, struct String delimiter, int depth) {
		auto index = input.lastIndexOf(delimiter);
		if (0 > index)
			return Optional.empty();
		auto before = input.substring(0, index);
		auto property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property))
			return Optional.empty();
		return Main.compileValue(before, depth).map(auto ?(auto result) {
			return result + property
		});
	}
	template Optional<struct String> compileNumber(struct String input) {
		if (Main.isNumber(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isNumber(struct CharSequence input) {
		auto length = input.length();
		return IntStream.range(0, length).mapToObj(input.charAt).allMatch(Character.isDigit);
	}
	template Optional<struct String> compileInvokable(struct String input, int depth) {
		if (input.isEmpty() || ')' != input.charAt(input.length() - 1))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - 1);
		auto divisions = Main.divide(withoutEnd, Main.foldInvocationStart);
		if (2 > divisions.size())
			return Optional.empty();
		auto withParamStart = String.join("", divisions.subList(0, divisions.size() - 1));
		auto arguments = divisions.getLast();
		if (withParamStart.isEmpty() || '(' != withParamStart.charAt(withParamStart.length() - 1))
			return Optional.empty();
		auto caller = withParamStart.substring(0, withParamStart.length() - 1);
		struct String outputArguments;
		if (arguments.isEmpty())
			outputArguments = "";
		else Optional[outputArguments = Main.compileValues(arguments, auto ?(auto input1) {
			return Main.compileValueOrPlaceholder(input1, depth)
		});]
		return Main.compileConstructor(caller).or(auto ?() {
			return Main.compileValue(caller, depth)
		}).map(auto ?(auto result) {
			return result + ")"
		});
	}
	struct State foldInvocationStart(struct State state, char next) {
		auto appended = state.append(next);
		if ('(' == next){ 
			auto enter = appended.enter();
			if (enter.isShallow())
				return enter.advance();
			else Optional[struct return enter;]
		}
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Optional<struct String> compileConstructor(struct String input) {
		if (input.startsWith("new ")){ 
			auto slice = input.substring("new ".length());
			auto output = Main.compileTypeOrPlaceholder(slice);
			return Optional.of(output);
		}
		return Optional.empty();
	}
	template Optional<struct String> compileMethod(struct String input, int depth, struct CharSequence structName) {
		auto paramStart = input.indexOf('(');
		if (0 > paramStart)
			return Optional.empty();
		auto definitionString = input.substring(0, paramStart).strip();
		auto withParams = input.substring(paramStart + 1);
		auto paramEnd = withParams.indexOf(')');
		if (0 > paramEnd)
			return Optional.empty();
		auto params = withParams.substring(0, paramEnd);
		auto withBraces = withParams.substring(paramEnd + 1).strip();
		/*final var maybeDefinition = Main.parseDefinition(definitionString)
																		.<MethodHeader>map(value -> value)
																		.or(() -> Main.parseConstructor(structName, definitionString));*/
		if (maybeDefinition.isEmpty())
			return Optional.empty();
		auto definable = maybeDefinition.get();
		if (/*definable instanceof final Definition definition*/)
			Main.typeParams.add(definition.maybeTypeParameter.stream().toList());
		struct String newParams = Main.compileValues(params, auto ?(auto paramString) {
			if (paramString.isBlank())
				return "";
			return Main.parseParameter(paramString).generate();
		});
		if (/*definable instanceof Definition*/)
			Main.typeParams.removeLast();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1)){ 
			struct String definition1 = definable.generate();
			return Optional.of(Main.getString(newParams, definition1, ";"));
		}
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of(Main.assembleFunction(depth, newParams, definable, Main.compileFunctionSegments(depth, content)));
	}
	template Optional<struct Constructor> parseConstructor(struct CharSequence structName, struct String definitionString) {
		auto i = definitionString.lastIndexOf(' ');
		if (/*0 <= i*/){ 
			auto substring = definitionString.substring(i + 1).strip();
			if (substring.contentEquals(structName))
				return Optional.of(struct Constructor(structName));
		}
		return Optional.empty();
	}
	struct String assembleFunction(int depth, struct String params, struct MethodHeader definition, struct String content) {
		struct String content1;
		if (/*definition instanceof Constructor(CharSequence structName)*/)
			content1 = Main.createIndent(depth + 1) + "return this;";
		else Optional[content1 = content;]
		return Main.getString(params, definition.generate(), " {" + content1 + Main.createIndent(depth) + "}");
	}
	struct String getString(struct String params, struct String definition, struct String content) {
		return definition + content;
	}
	struct String compileFunctionSegments(int depth, struct CharSequence content) {
		return Main.compileStatements(content, auto ?(auto input1) {
			return Main.compileFunctionSegment(input1, depth + 1)
		});
	}
	struct String compileValues(struct CharSequence input, struct String (*)(struct String) mapper) {
		return Main.compileAll(input, mapper, Main.foldValue, ", ");
	}
	struct String createIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}
	struct String compileFunctionSegment(struct String input, int depth) {
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth);
	}
	struct String compileFunctionSegmentValue(struct String input, int depth) {
		return Main.compileConditional(input, depth, "while").or(auto ?() {
			return Main.compileConditional(input, depth, "if")
		}).or(auto ?() {
			return Main.compileElse(input, depth)
		}).or(auto ?() {
			return Main.compileFunctionStatement(input, depth)
		}).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileElse(struct String input, int depth) {
		if (input.startsWith("else")){ 
			auto substring = input.substring("else".length());
			return Optional.of("else " + Main.compileFunctionStatement(substring, depth));
		}
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileFunctionStatement(struct String input, int depth) {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth).map(auto ?(auto result) {
			return result + ";"
		});
	}
	template Optional<struct String> compileBreak(struct CharSequence input) {
		if ("break".contentEquals(input))
			return Optional.of("break");
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileConditional(struct String input, int depth, struct String type) {
		if (!input.startsWith(type))
			return Optional.empty();
		auto withoutStart = input.substring(type.length()).strip();
		if (withoutStart.isEmpty() || '(' != withoutStart.charAt(0))
			return Optional.empty();
		auto withCondition = withoutStart.substring(1);
		auto divisions = Main.divide(withCondition, Main.foldConditionEnd);
		if (2 > divisions.size())
			return Optional.empty();
		auto withEnd = String.join("", divisions.subList(0, divisions.size() - 1));
		auto maybeWithBraces = divisions.getLast();
		if (withEnd.isEmpty() || ')' != withEnd.charAt(withEnd.length() - 1))
			return Optional.empty();
		auto condition = withEnd.substring(0, withEnd.length() - 1);
		auto before = type + ")";
		return Optional.of(before + Main.compileWithBraces(depth, maybeWithBraces).orElseGet(auto ?() {
			return Main.compileFunctionSegment(maybeWithBraces, depth + 1)
		}));
	}
	struct State foldConditionEnd(struct State state, char next) {
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			if (appended.isLevel())
				return appended.advance();
		else Optional[struct return appended.exit();]
		return appended;
	}
	template Optional<struct String> compileWithBraces(int depth, struct String input) {
		auto withBraces = input.strip();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of("{ " + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) + "}");
	}
	template Optional<struct String> compileFunctionStatementValue(struct String input, int depth) {
		if (input.startsWith("return ")){ 
			auto value = input.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth));
		}
		return Main.compileInvokable(input, depth).or(auto ?() {
			return Main.compileInitialization(input, depth)
		}).or(auto ?() {
			return Main.parseDefinition(input).map(Definition.generate)
		}).or(auto ?() {
			return Main.compilePostFix(input, depth)
		}).or(auto ?() {
			return Main.compileBreak(input)
		});
	}
	template Optional<struct String> compilePostFix(struct String input, int depth) {
		if (!input.endsWith("++"))
			return Optional.empty();
		auto slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth).map(auto ?(auto result) {
			return result + "++"
		});
	}
	struct State foldValue(struct State state, char next) {
		if (',' == next && state.isLevel())
			return state.advance();
		auto appended = state.append(next);
		if ('-' == next){ 
			auto peeked = state.peek();
			if (peeked.isPresent() && '>' == peeked.get())
				return appended.popAndAppendToOption().orElse(appended);
		}
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			return appended.exit();
		return appended;
	}
	struct Definable parseParameter(struct String input) {
		return /*Main.parseDefinition(input).<Definable>map(value -> value).orElseGet(() -> new Placeholder(input))*/;
	}
	template Optional<struct Definition> parseDefinition(struct String input) {
		auto strip = input.strip();
		auto index = strip.lastIndexOf(' ');
		if (0 > index)
			return Optional.empty();
		auto beforeName = strip.substring(0, index);
		auto name = strip.substring(index + " ".length());
		auto divisions = Main.divide(beforeName, Main.foldTypeSeparator);
		if (2 > divisions.size()){ 
			auto maybeType = Main.compileType(beforeName);
			if (maybeType.isEmpty())
				return Optional.empty();
			auto type = maybeType.get();
			return Optional.of(struct Definition(Optional.empty(), type, name));
		}
		auto joined = String.join(" ", divisions.subList(0, divisions.size() - 1)).strip();
		auto typeString = divisions.getLast();
		if (!joined.isEmpty() && '>' == joined.charAt(joined.length() - 1)){ 
			auto withoutEnd = joined.substring(0, joined.length() - 1);
			auto typeParamStart = withoutEnd.lastIndexOf('<');
			if (/*0 <= typeParamStart*/){ 
				auto typeParameterString = withoutEnd.substring(typeParamStart + 1).strip();
				Main.typeParams.add(List.of(typeParameterString));
				auto definition = Main.assembleDefinition(Optional.of(typeParameterString), typeString, name);
				Main.typeParams.removeLast();
				return definition;
			}
		}
		return Main.assembleDefinition(Optional.empty(), typeString, name);
	}
	template Optional<struct Definition> assembleDefinition(template Optional<struct String> maybeTypeParameter, struct String typeString, struct String name) {
		auto maybeType = Main.compileType(typeString);
		if (maybeType.isEmpty())
			return Optional.empty();
		auto type = maybeType.orElseGet(auto ?() {
			return Main.wrap(typeString)
		});
		auto generated = struct Definition(maybeTypeParameter, type, name);
		return Optional.of(generated);
	}
	struct State foldTypeSeparator(struct State state, char next) {
		if (' ' == next && state.isLevel())
			return state.advance();
		auto appended = state.append(next);
		if ('<' == next)
			return appended.enter();
		if ('>' == next)
			return appended.exit();
		return appended;
	}
	struct String compileTypeOrPlaceholder(struct String input) {
		return Main.compileType(input).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileType(struct String input) {
		auto strip = input.strip();
		if ("int".contentEquals(strip) || "boolean".contentEquals(strip))
			return Optional.of("int");
		if ("var".contentEquals(strip))
			return Optional.of("auto");
		if ("void".contentEquals(strip))
			return Optional.of("void");
		if ("char".contentEquals(strip) || "Character".contentEquals(strip))
			return Optional.of("char");
		if ("String".contentEquals(strip))
			return Optional.of("struct String");
		if (Main.typeParams.stream().anyMatch(auto ?(auto frame) {
			return frame.contains(strip)
		}))
			return Optional.of("typeparam " + strip);
		return Main.compileGenericType(strip).or(auto ?() {
			return Main.compileArrayType(strip)
		}).or(auto ?() {
			return Main.compileStructureType(strip)
		});
	}
	template Optional<struct String> compileStructureType(struct String input) {
		if (Main.isIdentifier(input))
			return Optional.of("struct " + input);
		return Optional.empty();
	}
	template Optional<struct String> compileGenericType(struct String strip) {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		auto index = withoutEnd.indexOf('<');
		if (0 > index)
			return Optional.empty();
		auto base = withoutEnd.substring(0, index);
		auto inputArguments = withoutEnd.substring(index + "<".length());
		auto outputArgs = Main.beforeTypeArguments(inputArguments);
		if (base.contentEquals("Function"))
			return Optional.of(outputArgs.getLast() + ")");
		if ("BiFunction".contentEquals(base))
			return Optional.of(outputArgs.getLast() + ")");
		auto outputArgsString = String.join(", ", outputArgs);
		return Optional.of("template " + base + "<" + outputArgsString + ">");
	}
	template List<struct String> beforeTypeArguments(struct CharSequence input) {
		if (input.isEmpty())
			return Collections.emptyList();
		return Main.divide(input, Main.foldValue).stream().map(Main.compileTypeOrPlaceholder).toList();
	}
	template Optional<struct String> compileArrayType(struct String input) {
		if (!input.endsWith("[]"))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - "[]".length());
		auto slice = Main.compileTypeOrPlaceholder(withoutEnd);
		return Optional.of("[" + slice + "]*");
	}
	struct String wrap(struct String input) {
		return "/*" + input + "*/";
	}
}