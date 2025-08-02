struct Main {
	struct Result<T, X> {
		struct R (*)(struct X) whenErr);
	}
	struct Actual {
	}
	struct State {
		struct StringBuilder buffer = struct StringBuilder();
		template Collection<char*> segments = template ArrayList<>();
		struct CharSequence input;
		int depth = 0;
		int index = 0;
		struct private State(struct CharSequence input) {
			this.input = input;
		}
		template Stream<char*> stream() {
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
	struct Tuple<A, B>(struct A left, struct B right) {
	}
	struct Ok<T, X>(struct T value);
	struct Err<T, X>(struct X error);
	struct private Main() {
	}
	void main([char*]* args) {
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
	template Result<char*, struct IOException> readString(struct Path source) {
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	char* compile(struct CharSequence input) {
		return Main.compileStatements(input, Main.compileRootSegment);
	}
	char* compileStatements(struct CharSequence input, char* (*)(char*) mapper) {
		return Main.compileAll(input, mapper, Main.foldStatement, "");
	}
	char* compileAll(struct CharSequence input, char* (*)(char*) mapper, struct State (*)(struct State, char) folder, struct CharSequence delimiter) {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	template List<char*> divide(struct CharSequence input, struct State (*)(struct State, char) folder) {
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
	char* compileRootSegment(char* input) {
		auto strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("import "))
			return "";
		auto modifiers = Main.compileClass("class", strip, 0);
		return modifiers.orElseGet(auto ?() {
			return Main.wrap(strip)
		});
	}
	template Optional<char*> compileClass(char* type, char* input, int depth) {
		auto index = input.indexOf(type + " ");
		if (0 > index)
			return Optional.empty();
		auto withName = input.substring(index + (type + " ").length());
		auto contentStart = withName.indexOf('{');
		if (0 > contentStart)
			return Optional.empty();
		auto name = withName.substring(0, contentStart).strip();
		auto withEnd = withName.substring(contentStart + "{".length()).strip();
		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1))
			return Optional.empty();
		auto content = withEnd.substring(0, withEnd.length() - 1);
		return Optional.of("struct " + name + " {" +
											 Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth + 1)) +
											 Main.createIndent(depth) + "}");
	}
	char* compileClassSegment(char* input, int depth) {
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth);
	}
	char* compileClassSegmentValue(char* input, int depth) {
		return Main.compileClass("class", input, depth).or(auto ?() {
			return Main.compileClass("interface", input, depth)
		}).or(auto ?() {
			return Main.compileField(input, depth)
		}).or(auto ?() {
			return Main.compileMethod(input, depth)
		}).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<char*> compileField(char* input, int depth) {
		auto strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth).or(auto ?() {
			return Main.compileDefinition(withoutEnd)
		}).map(auto ?(auto result) {
			return result + ";"
		});
	}
	template Optional<char*> compileInitialization(char* input, int depth) {
		auto valueSeparator = input.indexOf('=');
		if (0 > valueSeparator)
			return Optional.empty();
		auto definition = input.substring(0, valueSeparator);
		auto value = input.substring(valueSeparator + 1);
		auto destination = Main.compileDefinition(definition).orElseGet(auto ?() {
			return Main.compileValueOrPlaceholder(definition, depth)
		});
		return Main.compileValue(value, depth).map(auto ?(auto s) {
			return destination + s
		});
	}
	char* compileValueOrPlaceholder(char* input, int depth) {
		return Main.compileValue(input, depth).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<char*> compileValue(char* input, int depth) {
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
	template Optional<char*> compileChar(char* input) {
		if (!input.isEmpty() && '\'' == input.charAt(input.length() - 1))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	template Optional<char*> compileNot(int depth, char* strip) {
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return Optional.of("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth));
		return Optional.empty();
	}
	template Optional<char*> compileLambda(char* input, int depth) {
		auto index = input.indexOf("->");
		if (0 > index)
			return Optional.empty();
		auto name = input.substring(0, index).strip();
		auto after = input.substring(index + "->".length()).strip();
		char* params;
		if (name.contentEquals("()"))
			params = "";
		else Optional[struct (Main.isIdentifier(name)) params = "auto " + name;]
		else Optional[struct return Optional.empty();]
		if (after.isEmpty() || '{' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth).map(auto ?(auto value) {
				return Main.assembleFunction(depth, params, "auto ?", Main.createIndent(depth + 1) + value)
			});
		auto content = after.substring(1, after.length() - 1);
		return Optional.of(Main.assembleFunction(depth, params, "auto ?", Main.compileFunctionSegments(depth, content)));
	}
	template Optional<char*> compileString(char* input) {
		if (Main.isString(input))
			return Optional.of(input);
		return Optional.empty();
	}
	int isString(struct CharSequence input) {
		return !input.isEmpty() && '\"' == input.charAt(input.length() - 1);
	}
	template Optional<char*> compileOperator(struct CharSequence input, char* operator, int depth) {
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
	struct State foldOperator(char* operator, struct State state, char next) {
		if (state.isLevel() && next == operator.charAt(0)){ 
			if (1 == operator.length())
				return state.advance();
			if (2 == operator.length()){ 
				auto peeked = state.peek();
				if (peeked.isPresent() && peeked.get().equals(operator.charAt(1)))
					return state.pop().map(auto ?(auto tuple) {
						return tuple.left
					}).orElse(state).advance();
			}
		}
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Optional<char*> compileIdentifier(char* input) {
		if (Main.isIdentifier(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isIdentifier(struct CharSequence input) {
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
	template Optional<char*> compileAccess(char* input, char* delimiter, int depth) {
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
	template Optional<char*> compileNumber(char* input) {
		if (Main.isNumber(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isNumber(struct CharSequence input) {
		auto length = input.length();
		return IntStream.range(0, length).mapToObj(input.charAt).allMatch(Character.isDigit);
	}
	template Optional<char*> compileInvokable(char* input, int depth) {
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
		char* outputArguments;
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
	template Optional<char*> compileConstructor(char* input) {
		if (input.startsWith("new ")){ 
			auto slice = input.substring("new ".length());
			auto output = Main.compileType(slice);
			return Optional.of(output);
		}
		return Optional.empty();
	}
	template Optional<char*> compileMethod(char* input, int depth) {
		auto paramStart = input.indexOf('(');
		if (0 > paramStart)
			return Optional.empty();
		auto definition = input.substring(0, paramStart);
		auto withParams = input.substring(paramStart + 1);
		auto paramEnd = withParams.indexOf(')');
		if (0 > paramEnd)
			return Optional.empty();
		auto params = withParams.substring(0, paramEnd);
		auto withBraces = withParams.substring(paramEnd + 1).strip();
		char* newParams;
		if (params.isEmpty())
			newParams = "";
		else Optional[newParams = Main.compileValues(params, Main.compileDefinitionOrPlaceholder);]
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1)){ 
			char* definition1 = Main.compileDefinitionOrPlaceholder(definition);
			return Optional.of(Main.getString(newParams, definition1, ";"));
		}
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of(Main.assembleFunction(depth, newParams, Main.compileDefinitionOrPlaceholder(definition), Main.compileFunctionSegments(depth, content)));
	}
	char* assembleFunction(int depth, char* params, char* definition, char* content) {
		return Main.getString(params, definition, " {" + content + Main.createIndent(depth) + "}");
	}
	char* getString(char* params, char* definition, char* content) {
		return definition + content;
	}
	char* compileFunctionSegments(int depth, struct CharSequence content) {
		return Main.compileStatements(content, auto ?(auto input1) {
			return Main.compileFunctionSegment(input1, depth + 1)
		});
	}
	char* compileValues(struct CharSequence input, char* (*)(char*) mapper) {
		return Main.compileAll(input, mapper, Main.foldValue, ", ");
	}
	char* createIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}
	char* compileFunctionSegment(char* input, int depth) {
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth);
	}
	char* compileFunctionSegmentValue(char* input, int depth) {
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
	template Optional<char*> compileElse(char* input, int depth) {
		if (input.startsWith("else")){ 
			auto substring = input.substring("else".length());
			return Optional.of("else " + Main.compileFunctionStatement(substring, depth));
		}
		else Optional[struct return Optional.empty();]
	}
	template Optional<char*> compileFunctionStatement(char* input, int depth) {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth).map(auto ?(auto result) {
			return result + ";"
		});
	}
	template Optional<char*> compileBreak(struct CharSequence input) {
		if ("break".contentEquals(input))
			return Optional.of("break");
		else Optional[struct return Optional.empty();]
	}
	template Optional<char*> compileConditional(char* input, int depth, char* type) {
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
	template Optional<char*> compileWithBraces(int depth, char* input) {
		auto withBraces = input.strip();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of("{ " + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) + "}");
	}
	template Optional<char*> compileFunctionStatementValue(char* input, int depth) {
		if (input.startsWith("return ")){ 
			auto value = input.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth));
		}
		return Main.compileInvokable(input, depth).or(auto ?() {
			return Main.compileInitialization(input, depth)
		}).or(auto ?() {
			return Main.compileDefinition(input)
		}).or(auto ?() {
			return Main.compilePostFix(input, depth)
		}).or(auto ?() {
			return Main.compileBreak(input)
		});
	}
	template Optional<char*> compilePostFix(char* input, int depth) {
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
	char* compileDefinitionOrPlaceholder(char* input) {
		return Main.compileDefinition(input).orElseGet(auto ?() {
			return Main.wrap(input)
		});
	}
	template Optional<char*> compileDefinition(char* input) {
		auto strip = input.strip();
		auto index = strip.lastIndexOf(' ');
		if (0 > index)
			return Optional.empty();
		auto beforeName = strip.substring(0, index);
		auto name = strip.substring(index + " ".length());
		auto divisions = Main.divide(beforeName, Main.foldTypeSeparator);
		if (2 > divisions.size())
			return Optional.of(Main.compileType(beforeName) + name);
		auto type = divisions.getLast();
		return Optional.of(Main.compileType(type) + name);
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
	char* compileType(char* input) {
		auto strip = input.strip();
		if ("int".contentEquals(strip) || "boolean".contentEquals(strip))
			return "int";
		if ("var".contentEquals(strip))
			return "auto";
		if ("void".contentEquals(strip))
			return "void";
		if ("char".contentEquals(strip) || "Character".contentEquals(strip))
			return "char";
		if ("String".contentEquals(strip))
			return "char*";
		return Main.compileGenericType(strip).or(auto ?() {
			return Main.compileArrayType(strip)
		}).orElseGet(auto ?() {
			return "struct " + strip
		});
	}
	template Optional<char*> compileGenericType(char* strip) {
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
	template List<char*> beforeTypeArguments(struct CharSequence input) {
		if (input.isEmpty())
			return Collections.emptyList();
		return Main.divide(input, Main.foldValue).stream().map(Main.compileType).toList();
	}
	template Optional<char*> compileArrayType(char* input) {
		if (!input.endsWith("[]"))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - "[]".length());
		auto slice = Main.compileType(withoutEnd);
		return Optional.of("[" + slice + "]*");
	}
	char* wrap(char* input) {
		return "/*" + input + "*/";
	}
}