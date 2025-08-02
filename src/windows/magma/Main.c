struct Main {
	struct Result<T, X>  {
		<R> typeparam R match(typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr);
	}
	struct Actual {
	}
	struct Parameter {
		struct String generate();
	}
	struct MethodHeader extends Parameter {
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
		int hasNextChar(void* __self__, char c) {
			struct State this = *(struct State*) __self__;
			auto peek = this.peek();
			return peek.isPresent() && peek.get().equals(c);
		}
		template Stream<struct String> stream(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.segments.stream();
		}
		struct State append(void* __self__, char c) {
			struct State this = *(struct State*) __self__;
			this.buffer.append(c);
			return this;
		}
		struct State enter(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.depth = this.depth + 1;
			return this;
		}
		int isLevel(void* __self__) {
			struct State this = *(struct State*) __self__;
			return 0 == this.depth;
		}
		struct State advance(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}
		struct State exit(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.depth = this.depth - 1;
			return this;
		}
		int isShallow(void* __self__) {
			struct State this = *(struct State*) __self__;
			return 1 == this.depth;
		}
		template Optional<template Tuple<struct State, char>> pop(void* __self__) {
			struct State this = *(struct State*) __self__;
			if (this.index >= this.input.length())
				return Optional.empty();
			auto next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(template Tuple<>(this, next));
		}
		template Optional<template Tuple<struct State, char>> popAndAppendToTuple(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.pop().map(auto ?(void* __self__, auto tuple) {
				struct ? this = *(struct ?*) __self__;
				return template Tuple<>(tuple.left.append(tuple.right), tuple.right)
			});
		}
		template Optional<struct State> popAndAppendToOption(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.popAndAppendToTuple().map(auto ?(void* __self__, auto tuple) {
				struct ? this = *(struct ?*) __self__;
				return tuple.left
			});
		}
		template Optional<char> peek(void* __self__) {
			struct State this = *(struct State*) __self__;
			if (this.index < this.input.length())
				return Optional.of(this.input.charAt(this.index));
			return Optional.empty();
		}
	}
	struct Tuple<A, B>(A left, B right) {
	}
	struct Ok<T, X>(T value) implements Result<T, X>  {
		<R> typeparam R match(void* __self__, typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			struct Ok this = *(struct Ok*) __self__;
			return whenOk.apply(this.value);
		}
	}
	struct Err<T, X>(X error) implements Result<T, X>  {
		<R> typeparam R match(void* __self__, typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			struct Err this = *(struct Err*) __self__;
			return whenErr.apply(this.error);
		}
	}
	struct Definition(Optional<String> maybeTypeParameter, String type, String name) implements MethodHeader {
		/*private Definition(final String type, final String name) {
			this(Optional.empty(), type, name);
		}*/
		struct String generate(void* __self__) {
			struct Definition(Optional<String> maybeTypeParameter, String type, String name) implements MethodHeader this = *(struct Definition(Optional<String> maybeTypeParameter, String type, String name) implements MethodHeader*) __self__;
			return this.maybeTypeParameter.map(auto ?(void* __self__, auto value) {
				struct ? this = *(struct ?*) __self__;
				return "<" + value + "> "
			}).orElse("") + this.name;
		}
	}
	struct Placeholder(String value) implements Parameter {
		struct String generate(void* __self__) {
			struct Placeholder(String value) implements Parameter this = *(struct Placeholder(String value) implements Parameter*) __self__;
			return Main.wrap(this.value);
		}
	}
	struct Constructor(CharSequence structName) implements MethodHeader {
		struct String generate(void* __self__) {
			struct Constructor(CharSequence structName) implements MethodHeader this = *(struct Constructor(CharSequence structName) implements MethodHeader*) __self__;
			return "struct " + this.structName + " new";
		}
	}
	template List<struct String> RESERVED_KEYWORDS = List.of("new", "private");
	template SequencedCollection<template List<struct String>> typeParams = template ArrayList<>();
	struct Main new() {
		struct Main this;
		return this;
	}
	void main(void* __self__, [struct String]* args) {
		struct Main this = *(struct Main*) __self__;
		auto source = Paths.get(".", "src", "java", "magma", "Main.java");
		auto target = Paths.get(".", "src", "windows", "magma", "Main.c");
		Main.readString(source).match(auto ?(void* __self__, auto input) {
			struct ? this = *(struct ?*) __self__;
			auto output = Main.compile(input);
			return Main.writeString(target, output);
		}, Optional.of).ifPresent(Throwable.printStackTrace);
	}
	template Optional<struct IOException> writeString(void* __self__, struct Path target, struct CharSequence output) {
		struct Main this = *(struct Main*) __self__;
		/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
		/*catch (final IOException e) {
			return Optional.of(e);
		}*/
	}
	template Result<struct String, struct IOException> readString(void* __self__, struct Path source) {
		struct Main this = *(struct Main*) __self__;
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	struct String compile(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStatements(input, Main.compileRootSegment);
	}
	struct String compileStatements(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileAll(input, mapper, Main.foldStatement, "");
	}
	struct String compileAll(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper, struct State (*)(struct State, char) folder, struct CharSequence delimiter) {
		struct Main this = *(struct Main*) __self__;
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	template List<struct String> divide(void* __self__, struct CharSequence input, struct State (*)(struct State, char) folder) {
		struct Main this = *(struct Main*) __self__;
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
	struct State foldDecorated(void* __self__, struct State (*)(struct State, char) folder, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		return Main.foldSingleQuotes(state, next).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.foldDoubleQuotes(state, next)
		}).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return folder.apply(state, next)
		});
	}
	template Optional<struct State> foldDoubleQuotes(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct State> foldSingleQuotes(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\'' != next)
			return Optional.empty();
		return state.append('\'').popAndAppendToTuple().flatMap(auto ?(void* __self__, auto tuple) {
			struct ? this = *(struct ?*) __self__;
			return Main.foldEscapeChar(tuple.left, tuple.right)
		}).flatMap(State.popAndAppendToOption);
	}
	template Optional<struct State> foldEscapeChar(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\\' == next)
			return state.popAndAppendToOption();
		return Optional.of(state);
	}
	struct State foldStatement(void* __self__, struct State current, char c) {
		struct Main this = *(struct Main*) __self__;
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
	struct String compileRootSegment(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("import "))
			return "";
		auto modifiers = Main.compileStructure("class", strip, 0);
		return modifiers.orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(strip)
		});
	}
	template Optional<struct String> compileStructure(void* __self__, struct String type, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct String> assembleStructure(void* __self__, int depth, struct CharSequence content, struct CharSequence name, template Collection<struct String> typeParams) {
		struct Main this = *(struct Main*) __self__;
		auto outputContent = Main.compileStatements(content, auto ?(void* __self__, auto input1) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileClassSegment(input1, depth + 1, name)
		});
		struct String typeParamsString;
		if (typeParams.isEmpty())
			typeParamsString = "";
		else Optional[typeParamsString = "<" + String.join(", ", typeParams) + "> ";]
		return Optional.of("struct " + name + typeParamsString + " {" + outputContent + Main.createIndent(depth) + "}");
	}
	struct String compileClassSegment(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth, structName);
	}
	struct String compileClassSegmentValue(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStructure("class", input, depth).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileStructure("interface", input, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileStructure("record", input, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileMethod(input, depth, structName)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileField(input, depth)
		}).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileField(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.parseDefinition(withoutEnd).map(Definition.generate)
		}).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return result + ";"
		});
	}
	template Optional<struct String> compileInitialization(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto valueSeparator = input.indexOf('=');
		if (0 > valueSeparator)
			return Optional.empty();
		auto definition = input.substring(0, valueSeparator);
		auto value = input.substring(valueSeparator + 1);
		auto destination = Main.parseDefinition(definition).map(Definition.generate).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileValueOrPlaceholder(definition, depth)
		});
		return Main.compileValue(value, depth).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return destination + result
		});
	}
	struct String compileValueOrPlaceholder(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileValue(input, depth).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileValue(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		return Main.compileLambda(strip, depth).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileNumber(strip)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileInvokable(strip, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileAccess(strip, ".", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileAccess(strip, "::", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileString(strip)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileChar(strip)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "==", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "!=", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "+", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "-", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "<", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "&&", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, "||", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, ">=", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileOperator(strip, ">", depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileIdentifier(strip)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileNot(depth, strip)
		});
	}
	template Optional<struct String> compileChar(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.isEmpty() && '\'' == input.charAt(input.length() - 1))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileNot(void* __self__, int depth, struct String strip) {
		struct Main this = *(struct Main*) __self__;
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return Optional.of("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth));
		return Optional.empty();
	}
	template Optional<struct String> compileLambda(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.indexOf("->");
		if (0 > index)
			return Optional.empty();
		auto name = input.substring(0, index).strip();
		auto after = input.substring(index + "->".length()).strip();
		template List<struct Parameter> params;
		if (name.contentEquals("()"))
			params = Collections.emptyList();
		else Optional[struct if (Main.isIdentifier(name))
			params = Collections.singletonList(struct Definition("auto", name));]
		else Optional[struct return Optional.empty();]
		struct Parameter definition = struct Definition("auto", "?");
		if (after.isEmpty() || '{' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth).map(auto ?(void* __self__, auto value) {
				struct ? this = *(struct ?*) __self__;
				return Main.assembleFunction(depth, params, definition, Main.createIndent(depth + 1) + value, "?")
			});
		auto content = after.substring(1, after.length() - 1);
		return Optional.of(Main.assembleFunction(depth, params, definition, Main.compileFunctionSegments(depth, content), "?"));
	}
	template Optional<struct String> compileString(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isString(input))
			return Optional.of(input);
		return Optional.empty();
	}
	int isString(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		return !input.isEmpty() && '\"' == input.charAt(input.length() - 1);
	}
	template Optional<struct String> compileOperator(void* __self__, struct CharSequence input, struct CharSequence operator, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto divisions = Main.divide(input, (state, next) -  > Main.foldOperator(operator, state, next));
		if (2 > divisions.size())
			return Optional.empty();
		auto left = divisions.getFirst();
		auto right = divisions.getLast();
		return Main.compileValue(left, depth).flatMap(auto ?(void* __self__, auto leftResult) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileValue(right, depth).map(auto ?(void* __self__, auto rightResult) {
			struct ? this = *(struct ?*) __self__;
			return leftResult + rightResult
		})
		});
	}
	struct State foldOperator(void* __self__, struct CharSequence operator, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct State> tryAdvanceAtOperator(void* __self__, struct CharSequence operator, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if (!state.isLevel() || next != operator.charAt(0))
			return Optional.empty();
		if (1 == operator.length())
			return Optional.of(state.advance());
		if (2 != operator.length())
			return Optional.empty();
		if (state.hasNextChar(operator.charAt(1)))
			return Optional.of(state.pop().map(auto ?(void* __self__, auto tuple) {
				struct ? this = *(struct ?*) __self__;
				return tuple.left
			}).orElse(state).advance());
		return Optional.empty();
	}
	template Optional<struct String> compileIdentifier(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isIdentifier(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (/*input.isEmpty() || Main.RESERVED_KEYWORDS.contains(input)*/)
			return false;
		return IntStream.range(0, input.length()).allMatch(auto ?(void* __self__, auto index) {
			struct ? this = *(struct ?*) __self__;
			return Main.isIdentifierChar(input, index)
		});
	}
	int isIdentifierChar(void* __self__, struct CharSequence input, int index) {
		struct Main this = *(struct Main*) __self__;
		auto next = input.charAt(index);
		if (0 == index)
			return Character.isLetter(next);
		return Character.isLetterOrDigit(next);
	}
	template Optional<struct String> compileAccess(void* __self__, struct String input, struct String delimiter, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.lastIndexOf(delimiter);
		if (0 > index)
			return Optional.empty();
		auto before = input.substring(0, index);
		auto property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property))
			return Optional.empty();
		return Main.compileValue(before, depth).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return result + property
		});
	}
	template Optional<struct String> compileNumber(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isNumber(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isNumber(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		auto length = input.length();
		return IntStream.range(0, length).mapToObj(input.charAt).allMatch(Character.isDigit);
	}
	template Optional<struct String> compileInvokable(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
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
		else Optional[outputArguments = Main.compileValues(arguments, auto ?(void* __self__, auto input1) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileValueOrPlaceholder(input1, depth)
		});]
		return Main.compileConstructor(caller).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileValue(caller, depth)
		}).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return result + ")"
		});
	}
	struct State foldInvocationStart(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct String> compileConstructor(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("new ")){ 
			auto slice = input.substring("new ".length());
			auto output = Main.compileTypeOrPlaceholder(slice);
			return Optional.of(output);
		}
		return Optional.empty();
	}
	template Optional<struct String> compileMethod(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto paramStart = input.indexOf('(');
		if (0 > paramStart)
			return Optional.empty();
		auto definitionString = input.substring(0, paramStart).strip();
		auto withParams = input.substring(paramStart + 1);
		auto paramEnd = withParams.indexOf(')');
		if (0 > paramEnd)
			return Optional.empty();
		auto paramsString = withParams.substring(0, paramEnd);
		auto withBraces = withParams.substring(paramEnd + 1).strip();
		/*final var maybeDefinition = Main.parseDefinition(definitionString)
																		.<MethodHeader>map(value -> value)
																		.or(() -> Main.parseConstructor(structName, definitionString));*/
		if (maybeDefinition.isEmpty())
			return Optional.empty();
		auto definable = maybeDefinition.get();
		if (/*definable instanceof final Definition definition*/)
			Main.typeParams.add(definition.maybeTypeParameter.stream().toList());
		auto params = Main.divide(paramsString, Main.foldValue).stream().map(String.strip).filter(auto ?(void* __self__, auto segment) {
			struct ? this = *(struct ?*) __self__;
			return !segment.isEmpty()
		}).map(Main.parseParameter).toList();
		if (/*definable instanceof Definition*/)
			Main.typeParams.removeLast();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1)){ 
			struct String definition1 = definable.generate();
			return Optional.of(Main.generateFunction(params, definition1, ";"));
		}
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of(Main.assembleFunction(depth, params, definable, Main.compileFunctionSegments(depth, content), structName));
	}
	struct Parameter parseParameter(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return /*Main.parseDefinition(input).<Parameter>map(value -> value).orElseGet(() -> new Placeholder(input))*/;
	}
	template Optional<struct Constructor> parseConstructor(void* __self__, struct CharSequence structName, struct String definitionString) {
		struct Main this = *(struct Main*) __self__;
		auto i = definitionString.lastIndexOf(' ');
		if (/*0 <= i*/){ 
			auto substring = definitionString.substring(i + 1).strip();
			if (substring.contentEquals(structName))
				return Optional.of(struct Constructor(structName));
		}
		return Optional.empty();
	}
	struct String assembleFunction(void* __self__, int depth, template Collection<struct Parameter> oldParams, struct Parameter definition, struct String content, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		template SequencedCollection<struct Parameter> newParams = template ArrayList<>(oldParams);
		struct String content1;
		if (/*definition instanceof Constructor(final CharSequence structName0)*/)
			content1 = Main.createIndent(depth + 1) + "return this;";
		else Optional.empty
		return Main.generateFunction(newParams, definition.generate(), " {" + content1 + Main.createIndent(depth) + "}");
	}
	struct String generateFunction(void* __self__, template Collection<struct Parameter> params, struct String definition, struct String content) {
		struct Main this = *(struct Main*) __self__;
		auto joinedParams = params.stream().map(Parameter.generate).collect(Collectors.joining(", "));
		return definition + content;
	}
	struct String compileFunctionSegments(void* __self__, int depth, struct CharSequence content) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStatements(content, auto ?(void* __self__, auto input1) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileFunctionSegment(input1, depth + 1)
		});
	}
	struct String compileValues(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileAll(input, mapper, Main.foldValue, ", ");
	}
	struct String createIndent(void* __self__, int depth) {
		struct Main this = *(struct Main*) __self__;
		return System.lineSeparator() + "\t".repeat(depth);
	}
	struct String compileFunctionSegment(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth);
	}
	struct String compileFunctionSegmentValue(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileConditional(input, depth, "while").or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileConditional(input, depth, "if")
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileElse(input, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileFunctionStatement(input, depth)
		}).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileElse(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("else")){ 
			auto substring = input.substring("else".length());
			return Optional.of("else " + Main.compileFunctionStatement(substring, depth));
		}
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileFunctionStatement(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return result + ";"
		});
	}
	template Optional<struct String> compileBreak(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if ("break".contentEquals(input))
			return Optional.of("break");
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileConditional(void* __self__, struct String input, int depth, struct String type) {
		struct Main this = *(struct Main*) __self__;
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
		return Optional.of(before + Main.compileWithBraces(depth, maybeWithBraces).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileFunctionSegment(maybeWithBraces, depth + 1)
		}));
	}
	struct State foldConditionEnd(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			if (appended.isLevel())
				return appended.advance();
		else Optional[struct return appended.exit();]
		return appended;
	}
	template Optional<struct String> compileWithBraces(void* __self__, int depth, struct String input) {
		struct Main this = *(struct Main*) __self__;
		auto withBraces = input.strip();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of("{ " + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) + "}");
	}
	template Optional<struct String> compileFunctionStatementValue(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("return ")){ 
			auto value = input.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth));
		}
		return Main.compileInvokable(input, depth).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileInitialization(input, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.parseDefinition(input).map(Definition.generate)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compilePostFix(input, depth)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileBreak(input)
		});
	}
	template Optional<struct String> compilePostFix(void* __self__, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("++"))
			return Optional.empty();
		auto slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth).map(auto ?(void* __self__, auto result) {
			struct ? this = *(struct ?*) __self__;
			return result + "++"
		});
	}
	struct State foldValue(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct Definition> parseDefinition(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
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
	template Optional<struct Definition> assembleDefinition(void* __self__, template Optional<struct String> maybeTypeParameter, struct String typeString, struct String name) {
		struct Main this = *(struct Main*) __self__;
		auto maybeType = Main.compileType(typeString);
		if (maybeType.isEmpty())
			return Optional.empty();
		auto type = maybeType.orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(typeString)
		});
		auto generated = struct Definition(maybeTypeParameter, type, name);
		return Optional.of(generated);
	}
	struct State foldTypeSeparator(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if (' ' == next && state.isLevel())
			return state.advance();
		auto appended = state.append(next);
		if ('<' == next)
			return appended.enter();
		if ('>' == next)
			return appended.exit();
		return appended;
	}
	struct String compileTypeOrPlaceholder(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileType(input).orElseGet(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileType(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
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
		if (Main.typeParams.stream().anyMatch(auto ?(void* __self__, auto frame) {
			struct ? this = *(struct ?*) __self__;
			return frame.contains(strip)
		}))
			return Optional.of("typeparam " + strip);
		return Main.compileGenericType(strip).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileArrayType(strip)
		}).or(auto ?(void* __self__) {
			struct ? this = *(struct ?*) __self__;
			return Main.compileStructureType(strip)
		});
	}
	template Optional<struct String> compileStructureType(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return Optional.of("struct " + input);
		return Optional.empty();
	}
	template Optional<struct String> compileGenericType(void* __self__, struct String strip) {
		struct Main this = *(struct Main*) __self__;
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
	template List<struct String> beforeTypeArguments(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty())
			return Collections.emptyList();
		return Main.divide(input, Main.foldValue).stream().map(Main.compileTypeOrPlaceholder).toList();
	}
	template Optional<struct String> compileArrayType(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("[]"))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - "[]".length());
		auto slice = Main.compileTypeOrPlaceholder(withoutEnd);
		return Optional.of("[" + slice + "]*");
	}
	struct String wrap(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return "/*" + input + "*/";
	}
}