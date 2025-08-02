struct Main {
	struct Result<T, X>  {
		<R> typeparam R match(typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr);
	}
	struct Actual {
	}
	struct JavaParameter {
		struct String generate();
	}
	struct JavaMethodHeader extends JavaParameter {
	}
	struct State {
		struct StringBuilder buffer = struct StringBuilder();
		template Collection<struct String> segments = template ArrayList<>();
		struct CharSequence input;
		int depth = 0;
		int index = 0;
		struct State new_State(struct CharSequence input) {
			struct State this;
			this.input = input;
			return this;
		}
		int hasNextChar_State(void* __self__, char c) {
			struct State this = *(struct State*) __self__;
			auto peek = this.peek();
			return peek.isPresent() && peek.get().equals(c);
		}
		template Stream<struct String> stream_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.segments.stream();
		}
		struct State append_State(void* __self__, char c) {
			struct State this = *(struct State*) __self__;
			this.buffer.append(c);
			return this;
		}
		struct State enter_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.depth = this.depth + 1;
			return this;
		}
		int isLevel_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			return 0 == this.depth;
		}
		struct State advance_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}
		struct State exit_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			this.depth = this.depth - 1;
			return this;
		}
		int isShallow_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			return 1 == this.depth;
		}
		template Optional<template Tuple<struct State, char>> pop_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			if (this.index >= this.input.length())
				return Optional.empty();
			auto next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(template Tuple<>(this, next));
		}
		template Optional<template Tuple<struct State, char>> popAndAppendToTuple_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.pop().map(auto ?_State(void* __self__, auto tuple) {
				struct State this = *(struct State*) __self__;
				return template Tuple<>(tuple.left.append(tuple.right), tuple.right)
			});
		}
		template Optional<struct State> popAndAppendToOption_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			return this.popAndAppendToTuple().map(auto ?_State(void* __self__, auto tuple) {
				struct State this = *(struct State*) __self__;
				return tuple.left
			});
		}
		template Optional<char> peek_State(void* __self__) {
			struct State this = *(struct State*) __self__;
			if (this.index < this.input.length())
				return Optional.of(this.input.charAt(this.index));
			return Optional.empty();
		}
	}
	struct Tuple<A, B>(A left, B right) {
	}
	struct Ok<T, X>(T value) implements Result<T, X>  {
		<R> typeparam R match_Ok(void* __self__, typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			struct Ok this = *(struct Ok*) __self__;
			return whenOk.apply(this.value);
		}
	}
	struct Err<T, X>(X error) implements Result<T, X>  {
		<R> typeparam R match_Err(void* __self__, typeparam R (*)(typeparam T) whenOk, typeparam R (*)(typeparam X) whenErr) {
			struct Err this = *(struct Err*) __self__;
			return whenErr.apply(this.error);
		}
	}
	struct CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader {
		/*private CDefinition(final String type, final String name) {
			this(Optional.empty(), type, name);
		}*/
		struct String generate_CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader(void* __self__) {
			struct CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader this = *(struct CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader*) __self__;
			return this.maybeTypeParameter.map(auto ?_CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader(void* __self__, auto value) {
				struct CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader this = *(struct CDefinition(Optional<String> maybeTypeParameter, String type, String name)
			implements JavaMethodHeader*) __self__;
				return "<" + value + "> "
			}).orElse("") + this.name;
		}
	}
	struct Placeholder(String value) implements JavaParameter {
		struct String generate_Placeholder(String value) implements JavaParameter(void* __self__) {
			struct Placeholder(String value) implements JavaParameter this = *(struct Placeholder(String value) implements JavaParameter*) __self__;
			return Main.wrap(this.value);
		}
	}
	struct JavaConstructor() implements JavaMethodHeader {
		struct String generate_JavaConstructor() implements JavaMethodHeader(void* __self__) {
			struct JavaConstructor() implements JavaMethodHeader this = *(struct JavaConstructor() implements JavaMethodHeader*) __self__;
			return "?";
		}
	}
	template List<struct String> RESERVED_KEYWORDS = List.of("new", "private");
	template SequencedCollection<template List<struct String>> typeParams = template ArrayList<>();
	struct Main new_Main() {
		struct Main this;
		return this;
	}
	void main_Main(void* __self__, [struct String]* args) {
		struct Main this = *(struct Main*) __self__;
		auto source = Paths.get(".", "src", "java", "magma", "Main.java");
		auto target = Paths.get(".", "src", "windows", "magma", "Main.c");
		Main.readString(source).match(auto ?_Main(void* __self__, auto input) {
			struct Main this = *(struct Main*) __self__;
			auto output = Main.compile(input);
			return Main.writeString(target, output);
		}, Optional.of).ifPresent(Throwable.printStackTrace);
	}
	template Optional<struct IOException> writeString_Main(void* __self__, struct Path target, struct CharSequence output) {
		struct Main this = *(struct Main*) __self__;
		/*try {
			Files.writeString(target, output);
			return Optional.empty();
		}*/
		/*catch (final IOException e) {
			return Optional.of(e);
		}*/
	}
	template Result<struct String, struct IOException> readString_Main(void* __self__, struct Path source) {
		struct Main this = *(struct Main*) __self__;
		/*try {
			return new Ok<>(Files.readString(source));
		}*/
		/*catch (final IOException e) {
			return new Err<>(e);
		}*/
	}
	struct String compile_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStatements(input, Main.compileRootSegment);
	}
	struct String compileStatements_Main(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileAll(input, mapper, Main.foldStatement, "");
	}
	struct String compileAll_Main(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper, struct State (*)(struct State, char) folder, struct CharSequence delimiter) {
		struct Main this = *(struct Main*) __self__;
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	template List<struct String> divide_Main(void* __self__, struct CharSequence input, struct State (*)(struct State, char) folder) {
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
	struct State foldDecorated_Main(void* __self__, struct State (*)(struct State, char) folder, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		return Main.foldSingleQuotes(state, next).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.foldDoubleQuotes(state, next)
		}).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return folder.apply(state, next)
		});
	}
	template Optional<struct State> foldDoubleQuotes_Main(void* __self__, struct State state, char next) {
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
	template Optional<struct State> foldSingleQuotes_Main(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\'' != next)
			return Optional.empty();
		return state.append('\'').popAndAppendToTuple().flatMap(auto ?_Main(void* __self__, auto tuple) {
			struct Main this = *(struct Main*) __self__;
			return Main.foldEscapeChar(tuple.left, tuple.right)
		}).flatMap(State.popAndAppendToOption);
	}
	template Optional<struct State> foldEscapeChar_Main(void* __self__, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\\' == next)
			return state.popAndAppendToOption();
		return Optional.of(state);
	}
	struct State foldStatement_Main(void* __self__, struct State current, char c) {
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
	struct String compileRootSegment_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("import "))
			return "";
		auto modifiers = Main.compileStructure("class", strip, 0);
		return modifiers.orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(strip)
		});
	}
	template Optional<struct String> compileStructure_Main(void* __self__, struct String type, struct String input, int depth) {
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
	template Optional<struct String> assembleStructure_Main(void* __self__, int depth, struct CharSequence content, struct CharSequence name, template Collection<struct String> typeParams) {
		struct Main this = *(struct Main*) __self__;
		auto outputContent = Main.compileStatements(content, auto ?_Main(void* __self__, auto input1) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileClassSegment(input1, depth + 1, name)
		});
		struct String typeParamsString;
		if (typeParams.isEmpty())
			typeParamsString = "";
		else Optional[typeParamsString = "<" + String.join(", ", typeParams) + "> ";]
		return Optional.of("struct " + name + typeParamsString + " {" + outputContent + Main.createIndent(depth) + "}");
	}
	struct String compileClassSegment_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth, structName);
	}
	struct String compileClassSegmentValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStructure("class", input, depth).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileStructure("interface", input, depth)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileStructure("record", input, depth)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileMethod(input, depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileField(input, depth, structName)
		}).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileField_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth, structName).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.parseDefinition(withoutEnd).map(CDefinition.generate)
		}).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + ";"
		});
	}
	template Optional<struct String> compileInitialization_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto valueSeparator = input.indexOf('=');
		if (0 > valueSeparator)
			return Optional.empty();
		auto definition = input.substring(0, valueSeparator);
		auto value = input.substring(valueSeparator + 1);
		auto destination = Main.parseDefinition(definition).map(CDefinition.generate).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileValueOrPlaceholder(definition, depth, structName)
		});
		return Main.compileValue(value, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return destination + result
		});
	}
	struct String compileValueOrPlaceholder_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileValue(input, depth, structName).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		return Main.compileLambda(strip, depth, structName).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileNumber(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileInvokable(strip, depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileAccess(strip, ".", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileAccess(strip, "::", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileString(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileChar(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "==", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "!=", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "+", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "-", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "<", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "&&", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, "||", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, ">=", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileOperator(strip, ">", depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileIdentifier(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileNot(depth, strip, structName)
		});
	}
	template Optional<struct String> compileChar_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.isEmpty() && '\'' == input.charAt(input.length() - 1))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileNot_Main(void* __self__, int depth, struct String strip, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return Optional.of("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth, structName));
		return Optional.empty();
	}
	template Optional<struct String> compileLambda_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.indexOf("->");
		if (0 > index)
			return Optional.empty();
		auto name = input.substring(0, index).strip();
		auto after = input.substring(index + "->".length()).strip();
		template List<struct JavaParameter> params;
		if (name.contentEquals("()"))
			params = Collections.emptyList();
		else Optional[struct if (Main.isIdentifier(name))
			params = Collections.singletonList(struct CDefinition("auto", name));]
		else Optional[struct return Optional.empty();]
		struct JavaMethodHeader definition = struct CDefinition("auto", "?");
		if (after.isEmpty() || '{' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth, structName).flatMap(auto ?_Main(void* __self__, auto value) {
				struct Main this = *(struct Main*) __self__;
				return Main.assembleFunction(depth, params, definition, Main.createIndent(depth + 1) + value, structName)
			});
		auto content = after.substring(1, after.length() - 1);
		return Main.assembleFunction(depth, params, definition, Main.compileFunctionSegments(depth, content, structName), structName);
	}
	template Optional<struct String> compileString_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isString(input))
			return Optional.of(input);
		return Optional.empty();
	}
	int isString_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		return !input.isEmpty() && '\"' == input.charAt(input.length() - 1);
	}
	template Optional<struct String> compileOperator_Main(void* __self__, struct CharSequence input, struct CharSequence operator, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto divisions = Main.divide(input, (state, next) -  > Main.foldOperator(operator, state, next));
		if (2 > divisions.size())
			return Optional.empty();
		auto left = divisions.getFirst();
		auto right = divisions.getLast();
		return Main.compileValue(left, depth, structName).flatMap(auto ?_Main(void* __self__, auto leftResult) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileValue(right, depth, structName).map(auto ?_Main(void* __self__, auto rightResult) {
			struct Main this = *(struct Main*) __self__;
			return leftResult + rightResult
		})
		});
	}
	struct State foldOperator_Main(void* __self__, struct CharSequence operator, struct State state, char next) {
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
	template Optional<struct State> tryAdvanceAtOperator_Main(void* __self__, struct CharSequence operator, struct State state, char next) {
		struct Main this = *(struct Main*) __self__;
		if (!state.isLevel() || next != operator.charAt(0))
			return Optional.empty();
		if (1 == operator.length())
			return Optional.of(state.advance());
		if (2 != operator.length())
			return Optional.empty();
		if (state.hasNextChar(operator.charAt(1)))
			return Optional.of(state.pop().map(auto ?_Main(void* __self__, auto tuple) {
				struct Main this = *(struct Main*) __self__;
				return tuple.left
			}).orElse(state).advance());
		return Optional.empty();
	}
	template Optional<struct String> compileIdentifier_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isIdentifier_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (/*input.isEmpty() || Main.RESERVED_KEYWORDS.contains(input)*/)
			return false;
		return IntStream.range(0, input.length()).allMatch(auto ?_Main(void* __self__, auto index) {
			struct Main this = *(struct Main*) __self__;
			return Main.isIdentifierChar(input, index)
		});
	}
	int isIdentifierChar_Main(void* __self__, struct CharSequence input, int index) {
		struct Main this = *(struct Main*) __self__;
		auto next = input.charAt(index);
		if (0 == index)
			return Character.isLetter(next);
		return Character.isLetterOrDigit(next);
	}
	template Optional<struct String> compileAccess_Main(void* __self__, struct String input, struct String delimiter, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.lastIndexOf(delimiter);
		if (0 > index)
			return Optional.empty();
		auto before = input.substring(0, index);
		auto property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property))
			return Optional.empty();
		return Main.compileValue(before, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + property
		});
	}
	template Optional<struct String> compileNumber_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isNumber(input))
			return Optional.of(input);
		else Optional[struct return Optional.empty();]
	}
	int isNumber_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		auto length = input.length();
		return IntStream.range(0, length).mapToObj(input.charAt).allMatch(Character.isDigit);
	}
	template Optional<struct String> compileInvokable_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
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
		else Optional[outputArguments = Main.compileValues(arguments, auto ?_Main(void* __self__, auto input1) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileValueOrPlaceholder(input1, depth, structName)
		});]
		return Main.compileConstructor(caller).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileValue(caller, depth, structName)
		}).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + ")"
		});
	}
	struct State foldInvocationStart_Main(void* __self__, struct State state, char next) {
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
	template Optional<struct String> compileConstructor_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("new ")){ 
			auto slice = input.substring("new ".length());
			auto output = Main.compileTypeOrPlaceholder(slice);
			return Optional.of(output);
		}
		return Optional.empty();
	}
	template Optional<struct String> compileMethod_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
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
																		.<JavaMethodHeader>map(value -> value)
																		.or(() -> Main.parseConstructor(structName, definitionString));*/
		if (maybeDefinition.isEmpty())
			return Optional.empty();
		auto definable = maybeDefinition.get();
		if (/*definable instanceof final CDefinition definition*/)
			Main.typeParams.add(definition.maybeTypeParameter.stream().toList());
		auto params = Main.divide(paramsString, Main.foldValue).stream().map(String.strip).filter(auto ?_Main(void* __self__, auto segment) {
			struct Main this = *(struct Main*) __self__;
			return !segment.isEmpty()
		}).map(Main.parseParameter).toList();
		if (/*definable instanceof CDefinition*/)
			Main.typeParams.removeLast();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1)){ 
			struct String definition1 = definable.generate();
			return Optional.of(Main.generateFunction(params, definition1, ";"));
		}
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Main.assembleFunction(depth, params, definable, Main.compileFunctionSegments(depth, content, structName), structName);
	}
	struct JavaParameter parseParameter_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return /*Main.parseDefinition(input).<JavaParameter>map(value -> value).orElseGet(() -> new Placeholder(input))*/;
	}
	template Optional<struct JavaConstructor> parseConstructor_Main(void* __self__, struct CharSequence structName, struct String definitionString) {
		struct Main this = *(struct Main*) __self__;
		auto i = definitionString.lastIndexOf(' ');
		if (/*0 <= i*/){ 
			auto substring = definitionString.substring(i + 1).strip();
			if (substring.contentEquals(structName))
				return Optional.of(struct JavaConstructor());
		}
		return Optional.empty();
	}
	template Optional<struct String> assembleFunction_Main(void* __self__, int depth, template Collection<struct JavaParameter> oldParams, struct JavaMethodHeader oldHeader, struct String content, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		template SequencedCollection<struct JavaParameter> newParams = template ArrayList<>(oldParams);
		/*switch (oldHeader) {
			case JavaConstructor() -> {
				final JavaParameter newHeader = new CDefinition("struct " + structName, "new_" + structName);
				final String newContent =
						Main.createIndent(depth + 1) + "struct " + structName + " this;" + content + Main.createIndent(depth + 1) +
						"return this;";
				return Optional.of(
						Main.generateFunction(newParams, newHeader.generate(), " {" + newContent + Main.createIndent(depth) + "}"));
			}
			case CDefinition(
					final Optional<String> maybeTypeParameter, final String type, final String name
			) -> {
				final CDefinition newHeader;
				final String newContent;
				newHeader = new CDefinition(maybeTypeParameter, type, name + "_" + structName);
				newParams.addFirst(new CDefinition("void*", "__self__"));
				newContent =
						Main.createIndent(depth + 1) + "struct " + structName + " this = *(struct " + structName + "*) __self__;" +
						content;
				return Optional.of(
						Main.generateFunction(newParams, newHeader.generate(), " {" + newContent + Main.createIndent(depth) + "}"));
			}
			case null, default -> {
				return Optional.empty();
			}
		}*/
	}
	struct String generateFunction_Main(void* __self__, template Collection<struct JavaParameter> params, struct String definition, struct String content) {
		struct Main this = *(struct Main*) __self__;
		auto joinedParams = params.stream().map(JavaParameter.generate).collect(Collectors.joining(", "));
		return definition + content;
	}
	struct String compileFunctionSegments_Main(void* __self__, int depth, struct CharSequence content, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileStatements(content, auto ?_Main(void* __self__, auto input1) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileFunctionSegment(input1, depth + 1, structName)
		});
	}
	struct String compileValues_Main(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileAll(input, mapper, Main.foldValue, ", ");
	}
	struct String createIndent_Main(void* __self__, int depth) {
		struct Main this = *(struct Main*) __self__;
		return System.lineSeparator() + "\t".repeat(depth);
	}
	struct String compileFunctionSegment_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth, structName);
	}
	struct String compileFunctionSegmentValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileConditional(input, depth, "while", structName).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileConditional(input, depth, "if", structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileElse(input, depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileFunctionStatement(input, depth, structName)
		}).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileElse_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("else")){ 
			auto substring = input.substring("else".length());
			return Optional.of("else " + Main.compileFunctionStatement(substring, depth, structName));
		}
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileFunctionStatement_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + ";"
		});
	}
	template Optional<struct String> compileBreak_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if ("break".contentEquals(input))
			return Optional.of("break");
		else Optional[struct return Optional.empty();]
	}
	template Optional<struct String> compileConditional_Main(void* __self__, struct String input, int depth, struct String type, struct CharSequence structName) {
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
		return Optional.of(before + Main.compileWithBraces(depth, maybeWithBraces, structName).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileFunctionSegment(maybeWithBraces, depth + 1, structName)
		}));
	}
	struct State foldConditionEnd_Main(void* __self__, struct State state, char next) {
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
	template Optional<struct String> compileWithBraces_Main(void* __self__, int depth, struct String input, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto withBraces = input.strip();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of("{ " + Main.compileFunctionSegments(depth, content, structName) + Main.createIndent(depth) + "}");
	}
	template Optional<struct String> compileFunctionStatementValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("return ")){ 
			auto value = input.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth, structName));
		}
		return Main.compileInvokable(input, depth, structName).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileInitialization(input, depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.parseDefinition(input).map(CDefinition.generate)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compilePostFix(input, depth, structName)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileBreak(input)
		});
	}
	template Optional<struct String> compilePostFix_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("++"))
			return Optional.empty();
		auto slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + "++"
		});
	}
	struct State foldValue_Main(void* __self__, struct State state, char next) {
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
	template Optional<struct CDefinition> parseDefinition_Main(void* __self__, struct String input) {
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
			return Optional.of(struct CDefinition(Optional.empty(), type, name));
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
	template Optional<struct CDefinition> assembleDefinition_Main(void* __self__, template Optional<struct String> maybeTypeParameter, struct String typeString, struct String name) {
		struct Main this = *(struct Main*) __self__;
		auto maybeType = Main.compileType(typeString);
		if (maybeType.isEmpty())
			return Optional.empty();
		auto type = maybeType.orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(typeString)
		});
		auto generated = struct CDefinition(maybeTypeParameter, type, name);
		return Optional.of(generated);
	}
	struct State foldTypeSeparator_Main(void* __self__, struct State state, char next) {
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
	struct String compileTypeOrPlaceholder_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return Main.compileType(input).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.wrap(input)
		});
	}
	template Optional<struct String> compileType_Main(void* __self__, struct String input) {
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
		if (Main.typeParams.stream().anyMatch(auto ?_Main(void* __self__, auto frame) {
			struct Main this = *(struct Main*) __self__;
			return frame.contains(strip)
		}))
			return Optional.of("typeparam " + strip);
		return Main.compileGenericType(strip).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileArrayType(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileStructureType(strip)
		});
	}
	template Optional<struct String> compileStructureType_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return Optional.of("struct " + input);
		return Optional.empty();
	}
	template Optional<struct String> compileGenericType_Main(void* __self__, struct String strip) {
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
	template List<struct String> beforeTypeArguments_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty())
			return Collections.emptyList();
		return Main.divide(input, Main.foldValue).stream().map(Main.compileTypeOrPlaceholder).toList();
	}
	template Optional<struct String> compileArrayType_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("[]"))
			return Optional.empty();
		auto withoutEnd = input.substring(0, input.length() - "[]".length());
		auto slice = Main.compileTypeOrPlaceholder(withoutEnd);
		return Optional.of("[" + slice + "]*");
	}
	struct String wrap_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return "/*" + input + "*/";
	}
}