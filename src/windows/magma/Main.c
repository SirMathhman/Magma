struct Main {
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
		}, /* Some::new*/).ifPresent(Throwable.printStackTrace);
	}
	template Option<struct IOException> writeString_Main(void* __self__, struct Path target, struct CharSequence output) {
		struct Main this = *(struct Main*) __self__;
		/*try {
			Files.writeString(target, output);
			return new None<>();
		}*/
		/*catch (final IOException e) {
			return new Some<>(e);
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
	struct String compileAll_Main(void* __self__, struct CharSequence input, struct String (*)(struct String) mapper, struct DivideState (*)(struct DivideState, char) folder, struct CharSequence delimiter) {
		struct Main this = *(struct Main*) __self__;
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	template List<struct String> divide_Main(void* __self__, struct CharSequence input, struct DivideState (*)(struct DivideState, char) folder) {
		struct Main this = *(struct Main*) __self__;
		auto current = struct DivideState(input);
		while (true){ 
			auto popped = current.pop().toTuple(template Tuple<>(current, '\0'));
			if (!popped.left())
				break;
			auto tuple = popped.right();
			current = Main.foldDecorated(folder, tuple.left(), tuple.right());
		}
		return current.advance().stream().toList();
	}
	struct DivideState foldDecorated_Main(void* __self__, struct DivideState (*)(struct DivideState, char) folder, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		return Main.foldSingleQuotes(state, next).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.foldDoubleQuotes(state, next)
		}).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return folder.apply(state, next)
		});
	}
	template Option<struct DivideState> foldDoubleQuotes_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\"' != next)
			return template None<>();
		auto current = state.append('\"');
		while (true){ 
			auto maybeTuple = current.popAndAppendToTuple().toTuple(template Tuple<>(current, '\0'));
			if (!maybeTuple.left())
				break;
			auto tuple = maybeTuple.right();
			current = tuple.left();
			if ('\\' == tuple.right())
				current = current.popAndAppendToOption().orElse(current);
			if ('\"' == tuple.right())
				break;
		}
		return template Some<>(current);
	}
	template Option<struct DivideState> foldSingleQuotes_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\'' != next)
			return template None<>();
		return state.append('\'').popAndAppendToTuple().flatMap(auto ?_Main(void* __self__, auto tuple) {
			struct Main this = *(struct Main*) __self__;
			return Main.foldEscapeChar(tuple.left(), tuple.right())
		}).flatMap(DivideState.popAndAppendToOption);
	}
	template Option<struct DivideState> foldEscapeChar_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		if ('\\' == next)
			return state.popAndAppendToOption();
		return template Some<>(state);
	}
	struct DivideState foldStatement_Main(void* __self__, struct DivideState current, char c) {
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
			return Placeholder.wrap(strip)
		});
	}
	template Option<struct String> compileStructure_Main(void* __self__, struct String type, struct String input, int depth) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.indexOf(type + " ");
		if (0 > index)
			return template None<>();
		auto withName = input.substring(index + (type + " ").length());
		auto contentStart = withName.indexOf('{');
		if (0 > contentStart)
			return template None<>();
		auto beforeContent = withName.substring(0, contentStart).strip();
		auto withEnd = withName.substring(contentStart + "{".length()).strip();
		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1))
			return template None<>();
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
	template Option<struct String> assembleStructure_Main(void* __self__, int depth, struct CharSequence content, struct CharSequence name, template Collection<struct String> typeParams) {
		struct Main this = *(struct Main*) __self__;
		auto outputContent = Main.compileStatements(content, auto ?_Main(void* __self__, auto input1) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileClassSegment(input1, depth + 1, name)
		});
		struct String typeParamsString;
		if (typeParams.isEmpty())
			typeParamsString = "";
		else Some[value=typeParamsString = "<" + String.join(", ", typeParams) + "> ";]
		return template Some<>("struct " + name + typeParamsString + " {" + outputContent + Main.createIndent(depth) + "}");
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
			return Placeholder.wrap(input)
		});
	}
	template Option<struct String> compileField_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
			return template None<>();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth, structName).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.parseDefinition(withoutEnd).map(CDefinition.generate)
		}).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + ";"
		});
	}
	template Option<struct String> compileInitialization_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto valueSeparator = input.indexOf('=');
		if (0 > valueSeparator)
			return template None<>();
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
			return Placeholder.wrap(input)
		});
	}
	template Option<struct String> compileValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
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
	template Option<struct String> compileChar_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.isEmpty() && '\'' == input.charAt(input.length() - 1))
			return template Some<>(input);
		else magma.option.None@6e5e91e4
	}
	template Option<struct String> compileNot_Main(void* __self__, int depth, struct String strip, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return template Some<>("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth, structName));
		return template None<>();
	}
	template Option<struct String> compileLambda_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.indexOf("->");
		if (0 > index)
			return template None<>();
		auto name = input.substring(0, index).strip();
		auto after = input.substring(index + "->".length()).strip();
		template List<struct JavaParameter> params;
		if (name.contentEquals("()"))
			params = Collections.emptyList();
		else Some[value=struct if (Main.isIdentifier(name))
			params = Collections.singletonList(struct CDefinition("auto", name));]
		else magma.option.None@2cdf8d8a
		struct JavaMethodHeader definition = struct CDefinition("auto", "?");
		if (after.isEmpty() || '{' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth, structName).flatMap(auto ?_Main(void* __self__, auto value) {
				struct Main this = *(struct Main*) __self__;
				return Main.assembleFunction(depth, params, definition, Main.createIndent(depth + 1) + value, structName)
			});
		auto content = after.substring(1, after.length() - 1);
		return Main.assembleFunction(depth, params, definition, Main.compileFunctionSegments(depth, content, structName), structName);
	}
	template Option<struct String> compileString_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isString(input))
			return template Some<>(input);
		return template None<>();
	}
	int isString_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		return !input.isEmpty() && '\"' == input.charAt(input.length() - 1);
	}
	template Option<struct String> compileOperator_Main(void* __self__, struct CharSequence input, struct CharSequence operator, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto divisions = Main.divide(input, (state, next) -  > Main.foldOperator(operator, state, next));
		if (2 > divisions.size())
			return template None<>();
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
	struct DivideState foldOperator_Main(void* __self__, struct CharSequence operator, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		return Main.tryAdvanceAtOperator(operator, state, next).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.getState(state, next)
		});
	}
	struct DivideState getState_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Option<struct DivideState> tryAdvanceAtOperator_Main(void* __self__, struct CharSequence operator, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		if (!state.isLevel() || next != operator.charAt(0))
			return template None<>();
		if (1 == operator.length())
			return template Some<>(state.advance());
		if (2 != operator.length())
			return template None<>();
		if (state.hasNextChar(operator.charAt(1)))
			return template Some<>(state.pop().map(Tuple.left).orElse(state).advance());
		return template None<>();
	}
	template Option<struct String> compileIdentifier_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return template Some<>(input);
		else magma.option.None@30946e09
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
	template Option<struct String> compileAccess_Main(void* __self__, struct String input, struct String delimiter, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto index = input.lastIndexOf(delimiter);
		if (0 > index)
			return template None<>();
		auto before = input.substring(0, index);
		auto property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property))
			return template None<>();
		return Main.compileValue(before, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + property
		});
	}
	template Option<struct String> compileNumber_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isNumber(input))
			return template Some<>(input);
		else magma.option.None@5cb0d902
	}
	int isNumber_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		auto length = input.length();
		return IntStream.range(0, length).mapToObj(input.charAt).allMatch(Character.isDigit);
	}
	template Option<struct String> compileInvokable_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty() || ')' != input.charAt(input.length() - 1))
			return template None<>();
		auto withoutEnd = input.substring(0, input.length() - 1);
		auto divisions = Main.divide(withoutEnd, Main.foldInvocationStart);
		if (2 > divisions.size())
			return template None<>();
		auto withParamStart = String.join("", divisions.subList(0, divisions.size() - 1));
		auto arguments = divisions.getLast();
		if (withParamStart.isEmpty() || '(' != withParamStart.charAt(withParamStart.length() - 1))
			return template None<>();
		auto caller = withParamStart.substring(0, withParamStart.length() - 1);
		struct String outputArguments;
		if (arguments.isEmpty())
			outputArguments = "";
		else Some[value=outputArguments = Main.compileValues(arguments, auto ?_Main(void* __self__, auto input1) {
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
	struct DivideState foldInvocationStart_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		auto appended = state.append(next);
		if ('(' == next){ 
			auto enter = appended.enter();
			if (enter.isShallow())
				return enter.advance();
			else Some[value=struct return enter;]
		}
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Option<struct String> compileConstructor_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("new ")){ 
			auto slice = input.substring("new ".length());
			auto output = Main.compileTypeOrPlaceholder(slice);
			return template Some<>(output);
		}
		return template None<>();
	}
	template Option<struct String> compileMethod_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto paramStart = input.indexOf('(');
		if (0 > paramStart)
			return template None<>();
		auto definitionString = input.substring(0, paramStart).strip();
		auto withParams = input.substring(paramStart + 1);
		auto paramEnd = withParams.indexOf(')');
		if (0 > paramEnd)
			return template None<>();
		auto paramsString = withParams.substring(0, paramEnd);
		auto withBraces = withParams.substring(paramEnd + 1).strip();
		return /*Main.parseDefinition(definitionString)
							 .<JavaMethodHeader>map(value -> value)
							 .or(() -> Main.parseConstructor(structName, definitionString).map(value -> value))
							 .flatMap(definable -> Main.getStringOptional(depth, structName, definable, paramsString, withBraces))*/;
	}
	template Option<struct String> getStringOptional_Main(void* __self__, int depth, struct CharSequence structName, struct JavaMethodHeader definable, struct CharSequence paramsString, struct String withBraces) {
		struct Main this = *(struct Main*) __self__;
		if (/*definable instanceof final CDefinition definition*/)
			Main.typeParams.add(definition.maybeTypeParameter().stream().toList());
		auto params = Main.getList(paramsString);
		if (/*definable instanceof CDefinition*/)
			Main.typeParams.removeLast();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1)){ 
			struct String definition1 = definable.generate();
			return template Some<>(Main.generateFunction(params, definition1, ";"));
		}
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return Main.assembleFunction(depth, params, definable, Main.compileFunctionSegments(depth, content, structName), structName);
	}
	template List<struct JavaParameter> getList_Main(void* __self__, struct CharSequence paramsString) {
		struct Main this = *(struct Main*) __self__;
		return Main.divide(paramsString, Main.foldValue).stream().map(String.strip).filter(auto ?_Main(void* __self__, auto segment) {
			struct Main this = *(struct Main*) __self__;
			return !segment.isEmpty()
		}).map(Main.parseParameter).toList();
	}
	struct JavaParameter parseParameter_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		return /*Main.parseDefinition(input).<JavaParameter>map(value -> value).orElseGet(() -> new Placeholder(input))*/;
	}
	template Option<struct JavaConstructor> parseConstructor_Main(void* __self__, struct CharSequence structName, struct String definitionString) {
		struct Main this = *(struct Main*) __self__;
		auto i = definitionString.lastIndexOf(' ');
		if (/*0 <= i*/){ 
			auto substring = definitionString.substring(i + 1).strip();
			if (substring.contentEquals(structName))
				return template Some<>(struct JavaConstructor());
		}
		return template None<>();
	}
	template Option<struct String> assembleFunction_Main(void* __self__, int depth, template Collection<struct JavaParameter> oldParams, struct JavaMethodHeader oldHeader, struct String content, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		template SequencedCollection<struct JavaParameter> newParams = template ArrayList<>(oldParams);
		/*switch (oldHeader) {
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
			return Placeholder.wrap(input)
		});
	}
	template Option<struct String> compileElse_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("else")){ 
			auto substring = input.substring("else".length());
			return template Some<>("else " + Main.compileFunctionStatement(substring, depth, structName));
		}
		else magma.option.None@46fbb2c1
	}
	template Option<struct String> compileFunctionStatement_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return template None<>();
		auto withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + ";"
		});
	}
	template Option<struct String> compileBreak_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if ("break".contentEquals(input))
			return template Some<>("break");
		else magma.option.None@1698c449
	}
	template Option<struct String> compileConditional_Main(void* __self__, struct String input, int depth, struct String type, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (!input.startsWith(type))
			return template None<>();
		auto withoutStart = input.substring(type.length()).strip();
		if (withoutStart.isEmpty() || '(' != withoutStart.charAt(0))
			return template None<>();
		auto withCondition = withoutStart.substring(1);
		auto divisions = Main.divide(withCondition, Main.foldConditionEnd);
		if (2 > divisions.size())
			return template None<>();
		auto withEnd = String.join("", divisions.subList(0, divisions.size() - 1));
		auto maybeWithBraces = divisions.getLast();
		if (withEnd.isEmpty() || ')' != withEnd.charAt(withEnd.length() - 1))
			return template None<>();
		auto condition = withEnd.substring(0, withEnd.length() - 1);
		auto before = type + ")";
		return template Some<>(before + Main.compileWithBraces(depth, maybeWithBraces, structName).orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileFunctionSegment(maybeWithBraces, depth + 1, structName)
		}));
	}
	struct DivideState foldConditionEnd_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		auto appended = state.append(next);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			if (appended.isLevel())
				return appended.advance();
		else Some[value=struct return appended.exit();]
		return appended;
	}
	template Option<struct String> compileWithBraces_Main(void* __self__, int depth, struct String input, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		auto withBraces = input.strip();
		if (withBraces.isEmpty() || '{' != withBraces.charAt(withBraces.length() - 1))
			return template None<>();
		auto content = withBraces.substring(1, withBraces.length() - 1);
		return template Some<>("{ " + Main.compileFunctionSegments(depth, content, structName) + Main.createIndent(depth) + "}");
	}
	template Option<struct String> compileFunctionStatementValue_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (input.startsWith("return ")){ 
			auto value = input.substring("return ".length());
			return template Some<>("return " + Main.compileValueOrPlaceholder(value, depth, structName));
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
	template Option<struct String> compilePostFix_Main(void* __self__, struct String input, int depth, struct CharSequence structName) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("++"))
			return template None<>();
		auto slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth, structName).map(auto ?_Main(void* __self__, auto result) {
			struct Main this = *(struct Main*) __self__;
			return result + "++"
		});
	}
	struct DivideState foldValue_Main(void* __self__, struct DivideState state, char next) {
		struct Main this = *(struct Main*) __self__;
		if (',' == next && state.isLevel())
			return state.advance();
		auto appended = state.append(next);
		if ('-' == next && state.hasNextChar('>'))
			return appended.popAndAppendToOption().orElse(appended);
		if ('(' == next)
			return appended.enter();
		if (')' == next)
			return appended.exit();
		return appended;
	}
	template Option<struct CDefinition> parseDefinition_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		auto index = strip.lastIndexOf(' ');
		if (0 > index)
			return template None<>();
		auto beforeName = strip.substring(0, index);
		auto name = strip.substring(index + " ".length());
		auto divisions = Main.divide(beforeName, Main.foldTypeSeparator);
		if (2 > divisions.size())
			return Main.compileType(beforeName).map(auto ?_Main(void* __self__, auto type) {
				struct Main this = *(struct Main*) __self__;
				return struct CDefinition(template None<>(), type, name)
			});
		auto joined = String.join(" ", divisions.subList(0, divisions.size() - 1)).strip();
		auto typeString = divisions.getLast();
		if (!joined.isEmpty() && '>' == joined.charAt(joined.length() - 1)){ 
			auto withoutEnd = joined.substring(0, joined.length() - 1);
			auto typeParamStart = withoutEnd.lastIndexOf('<');
			if (/*0 <= typeParamStart*/){ 
				auto typeParameterString = withoutEnd.substring(typeParamStart + 1).strip();
				Main.typeParams.add(List.of(typeParameterString));
				auto definition = Main.assembleDefinition(template Some<>(typeParameterString), typeString, name);
				Main.typeParams.removeLast();
				return definition;
			}
		}
		return Main.assembleDefinition(template None<>(), typeString, name);
	}
	template Option<struct CDefinition> assembleDefinition_Main(void* __self__, template Option<struct String> maybeTypeParameter, struct String typeString, struct String name) {
		struct Main this = *(struct Main*) __self__;
		auto maybeType = Main.compileType(typeString);
		if (maybeType.isEmpty())
			return template None<>();
		auto type = maybeType.orElseGet(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Placeholder.wrap(typeString)
		});
		auto generated = struct CDefinition(maybeTypeParameter, type, name);
		return template Some<>(generated);
	}
	struct DivideState foldTypeSeparator_Main(void* __self__, struct DivideState state, char next) {
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
			return Placeholder.wrap(input)
		});
	}
	template Option<struct String> compileType_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		auto strip = input.strip();
		if ("int".contentEquals(strip) || "boolean".contentEquals(strip))
			return template Some<>("int");
		if ("var".contentEquals(strip))
			return template Some<>("auto");
		if ("void".contentEquals(strip))
			return template Some<>("void");
		if ("char".contentEquals(strip) || "Character".contentEquals(strip))
			return template Some<>("char");
		if ("String".contentEquals(strip))
			return template Some<>("struct String");
		if (Main.typeParams.stream().anyMatch(auto ?_Main(void* __self__, auto frame) {
			struct Main this = *(struct Main*) __self__;
			return frame.contains(strip)
		}))
			return template Some<>("typeparam " + strip);
		return Main.compileGenericType(strip).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileArrayType(strip)
		}).or(auto ?_Main(void* __self__) {
			struct Main this = *(struct Main*) __self__;
			return Main.compileStructureType(strip)
		});
	}
	template Option<struct String> compileStructureType_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (Main.isIdentifier(input))
			return template Some<>("struct " + input);
		return template None<>();
	}
	template Option<struct String> compileGenericType_Main(void* __self__, struct String strip) {
		struct Main this = *(struct Main*) __self__;
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1))
			return template None<>();
		auto withoutEnd = strip.substring(0, strip.length() - 1);
		auto index = withoutEnd.indexOf('<');
		if (0 > index)
			return template None<>();
		auto base = withoutEnd.substring(0, index);
		auto inputArguments = withoutEnd.substring(index + "<".length());
		auto outputArgs = Main.beforeTypeArguments(inputArguments);
		if (base.contentEquals("Function"))
			return template Some<>(outputArgs.getLast() + ")");
		if ("BiFunction".contentEquals(base))
			return template Some<>(outputArgs.getLast() + ")");
		auto outputArgsString = String.join(", ", outputArgs);
		return template Some<>("template " + base + "<" + outputArgsString + ">");
	}
	template List<struct String> beforeTypeArguments_Main(void* __self__, struct CharSequence input) {
		struct Main this = *(struct Main*) __self__;
		if (input.isEmpty())
			return Collections.emptyList();
		return Main.divide(input, Main.foldValue).stream().map(Main.compileTypeOrPlaceholder).toList();
	}
	template Option<struct String> compileArrayType_Main(void* __self__, struct String input) {
		struct Main this = *(struct Main*) __self__;
		if (!input.endsWith("[]"))
			return template None<>();
		auto withoutEnd = input.substring(0, input.length() - "[]".length());
		auto slice = Main.compileTypeOrPlaceholder(withoutEnd);
		return template Some<>("[" + slice + "]*");
	}
}