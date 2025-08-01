/*private */struct CType {
	void* impl;
};
/*private */struct CDefined {
	void* impl;
};
/*private static final */struct Lists {/*static <T> List<T> empty() {
			return new JavaList<>();
		}*//*@SafeVarargs
		static <T> List<T> of(final T... values) {
			return new JavaList<>(new ArrayList<>(Arrays.asList(values)));
		}*/
};
/*private */struct List_char_ref {
	void* impl;
};
/*private static */struct DivideState {
	/*private*/ struct List_char_ref segments;
	/*private*/ char* buffer;
	/*private*/ int depth;
};
/*private */struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<CType> typeArguments, List<String> typeParameters) {
};
/*private */struct Tuple<Left, Right>(Left left, Right right) {
};
/*private */struct CStructure(String modifiers, String name, String content) {
};
/*private */struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) {
};
/*private */struct Ref(CType type) implements CType {
};
/*private */struct StructType(String name) implements CType {
};
/*private */struct Placeholder(String value) implements CType, CDefined {
};
/*private */struct Definition(Optional<String> beforeType, CType type, String name) implements CDefined {
};
/*public final */struct Main {
	/*' */;
	/*' */;/*return current;*/
};
char* generate(/**/)/*;*/
char* stringify(/**/)/*;*/
struct CType CType(){
	struct CType this;
	return this;
}
char* generate(/**/)/*;*/
struct CDefined CDefined(){
	struct CDefined this;
	return this;
}
struct Lists Lists(){
	struct Lists this;
	return this;
}
struct List_char_ref add(char* element)/*;*/
/*Stream<T>*/ stream(/**/)/*;*/
int size(/**/)/*;*/
char* get(int index)/*;*/
struct List_char_ref List_char_ref(){
	struct List_char_ref this;
	return this;
}
/*private*/ /*DivideState*/ advance(/**/)/* {
			this.segments = this.segments.add(this.buffer);
			this.buffer = "";
			return this;
		}*/
/*private*/ /*DivideState*/ append(/*final*/ /*char*/ c)/* {
			this.buffer = this.buffer + c;
			return this;
		}*/
/*private*/ /*boolean*/ isLevel(/**/)/* {
			return 0 == this.depth;
		}*/
/*private*/ /*DivideState*/ exit(/**/)/* {
			this.depth = this.depth - 1;
			return this;
		}*/
/*private*/ /*DivideState*/ enter(/**/)/* {
			this.depth = this.depth + 1;
			return this;
		}*/
/*private*/ /*Stream<String>*/ stream(/**/)/* {
			return this.segments.stream();
		}*/
/*final*/ /*boolean*/ isShallow(/**/)/* {
			return 1 == this.depth;
		}*/
struct DivideState DivideState(){
	struct DivideState this;
	return this;
}
/*private*/ ParseState(/**/)/* {
			this(Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty());
		}*/
/*ParseState*/ addCStructure(/*final*/ /*CStructure*/ generated)/* {
			return new ParseState(this.javaStructures, this.cStructures.add(generated), this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}*/
/*ParseState*/ addFunction(/*final*/ char* generated)/* {
			return new ParseState(this.javaStructures, this.cStructures, this.functions.add(generated), this.visited,
														this.typeArguments, this.typeParameters);
		}*/
/*ParseState*/ addJavaStructure(/*final*/ /*JavaStructure*/ javaStructure)/* {
			return new ParseState(this.javaStructures.add(javaStructure), this.cStructures, this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}*/
/*ParseState*/ addVisited(/*final*/ char* name)/* {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited.add(name),
														this.typeArguments, this.typeParameters);
		}*/
/*ParseState*/ withArgument(/*final*/ /*CType*/ argument)/* {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, Lists.of(argument),
														this.typeParameters);
		}*/
/*public*/ /*ParseState*/ withParameters(/*final*/ char* typeParameters)/* {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, this.typeArguments,
														Lists.of(typeParameters));
		}*/
struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<CType> typeArguments, List<String> typeParameters) ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<CType> typeArguments, List<String> typeParameters)(){
	struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<CType> typeArguments, List<String> typeParameters) this;
	return this;
}
struct Tuple<Left, Right>(Left left, Right right) Tuple<Left, Right>(Left left, Right right)(){
	struct Tuple<Left, Right>(Left left, Right right) this;
	return this;
}
/*private*/ char* generate(/**/)/* {
			return Main.generatePlaceholder(this.modifiers()) + "struct " + this.name() + " {" + this.content() +
						 System.lineSeparator() + "};" + System.lineSeparator();
		}*/
struct CStructure(String modifiers, String name, String content) CStructure(String modifiers, String name, String content)(){
	struct CStructure(String modifiers, String name, String content) this;
	return this;
}
struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) JavaStructure(String type, String modifiers, String name, String typeParameters, String content)(){
	struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) this;
	return this;
}
/*@Override
		public*/ char* generate(/**/)/* {
			return this.type.generate() + "*";
		}*/
/*@Override
		public*/ char* stringify(/**/)/* {
			return this.type.stringify() + "_ref";
		}*/
struct Ref(CType type) implements CType Ref(CType type) implements CType(){
	struct Ref(CType type) implements CType this;
	return this;
}
/*@Override
		public*/ char* generate(/**/)/* {
			return "struct " + this.name;
		}*/
/*@Override
		public*/ char* stringify(/**/)/* {
			return this.name;
		}*/
struct StructType(String name) implements CType StructType(String name) implements CType(){
	struct StructType(String name) implements CType this;
	return this;
}
/*@Override
		public*/ char* generate(/**/)/* {
			return Main.generatePlaceholder(this.value);
		}*/
/*@Override
		public*/ char* stringify(/**/)/* {
			return Main.generatePlaceholder(this.value);
		}*/
struct Placeholder(String value) implements CType, CDefined Placeholder(String value) implements CType, CDefined(){
	struct Placeholder(String value) implements CType, CDefined this;
	return this;
}
/*@Override
		public*/ char* generate(/**/)/* {
			final var beforeTypeString = this.beforeType.map(before -> Main.generatePlaceholder(before) + " ").orElse("");
			return beforeTypeString + this.type.generate() + " " + this.name;
		}*/
struct Definition(Optional<String> beforeType, CType type, String name) implements CDefined Definition(Optional<String> beforeType, CType type, String name) implements CDefined(){
	struct Definition(Optional<String> beforeType, CType type, String name) implements CDefined this;
	return this;
}
/*private*/ Main(/**/)/* {}*/
/*public static*/ /*void*/ main(/*final*/ /*String[]*/ args)/* {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");

			final var joined = Main.compile(input);
			Files.writeString(target, joined);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*/
/*private static*/ char* compile(/*final*/ /*CharSequence*/ input)/* {
		final var tuple = Main.compileStatements(new ParseState(), input, Main::compileRootSegment);
		final var newState = tuple.left;
		final var joined = newState.cStructures.stream().map(CStructure::generate).collect(Collectors.joining()) +
											 newState.functions.stream().collect(Collectors.joining());

		return joined + tuple.right;
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileStatements(/*final*/ /*ParseState*/ state, /*final*/ /*CharSequence*/ input, /*final*/ BiFunction<ParseState, /* String*/, /* Tuple<ParseState*/, /*String>>*/ mapper)/* {
		return Main.compileAll(state, input, Main::foldStatements, mapper, "");
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileAll(/*final*/ /*ParseState*/ state, /*final*/ /*CharSequence*/ input, /*final*/ BiFunction<DivideState, /* Character*/, /*DivideState>*/ folder, /*final*/ BiFunction<ParseState, /* String*/, /* Tuple<ParseState*/, /*String>>*/ mapper, /*final*/ char* delimiter)/* {
		final var current = Main.divide(input, folder)
														.stream()
														.reduce(new Tuple<>(state, ""),
																		(tuple, segment) -> Main.fold(mapper, delimiter, tuple, segment),
																		(_, next) -> next);

		return new Tuple<>(current.left, current.right);
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ fold(/*final*/ BiFunction<ParseState, /* String*/, /* Tuple<ParseState*/, /*String>>*/ mapper, /*final*/ char* delimiter, /*final*/ Tuple<ParseState, /*String>*/ tuple, /*final*/ char* segment)/* {
		final var currentState = tuple.left;
		final var currentBuffer = tuple.right;

		final var result = mapper.apply(currentState, segment);
		final var append = currentBuffer.isEmpty() ? result.right : currentBuffer + delimiter + result.right;
		return new Tuple<>(result.left, append);
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileRootSegment(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return new Tuple<>(state, "");

		final var tuple = Main.compileRootSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileRootSegmentValue(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileClass(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		return Main.compileStructure("class", state, input)
							 .or(() -> Main.compileStructure("interface", state, input))
							 .or(() -> Main.compileStructure("record", state, input));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileStructure(/*final*/ char* type, /*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var classIndex = input.indexOf(type + " ");
		if (0 > classIndex) return Optional.empty();
		final var before = input.substring(0, classIndex);
		final var after = input.substring(classIndex + (type + " ").length());

		final var i = after.indexOf('{');
		if (0 > i) return Optional.empty();
		final var name = after.substring(0, i).strip();
		final var withEnd = after.substring(i + 1).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		final var parseState = Main.assembleStructure(state, before, name, content, type);
		return Optional.of(new Tuple<>(parseState, ""));
	}*/
/*private static*/ /*ParseState*/ assembleStructure(/*final*/ /*ParseState*/ state, /*final*/ char* modifiers, /*final*/ char* beforeContent, /*final*/ char* content, /*final*/ char* type)/* {
		final var strip = beforeContent.strip();
		if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
			final var withoutEnd = beforeContent.substring(0, strip.length() - 1);
			final var i = withoutEnd.indexOf('<');
			if (0 <= i) {
				final var name = withoutEnd.substring(0, i);
				final var typeParameters = withoutEnd.substring(i + 1);

				return state.addJavaStructure(new JavaStructure(type, modifiers, name, typeParameters, content));
			}
		}

		return Main.attachStructure(state, modifiers, beforeContent, content, type);
	}*/
/*private static*/ /*ParseState*/ attachStructure(/*final*/ /*ParseState*/ state, /*final*/ char* modifiers, /*final*/ char* name, /*final*/ /*CharSequence*/ content, /*final*/ /*CharSequence*/ type)/* {
		if (state.visited.stream().anyMatch(name::contentEquals)) return state;
		final var withVisited = state.addVisited(name);

		final var tuple = Main.compileStatements(withVisited, content, Main::compileClassSegment);
		final var outputContent = tuple.right;

		final var generated = new CStructure(modifiers, name, Main.createBeforeContent(type) + outputContent);
		return tuple.left.addCStructure(generated).addFunction(Main.generateConstructor(name));
	}*/
/*private static*/ char* createBeforeContent(/*final*/ /*CharSequence*/ type)/* {
		if ("interface".contentEquals(type)) return System.lineSeparator() + "\tvoid* impl;";
		return "";
	}*/
/*private static*/ char* generateConstructor(/*final*/ char* name)/* {
		return "struct " + name + " " + name + "(){" + System.lineSeparator() + "\tstruct " + name + " this;" +
					 System.lineSeparator() + "\treturn this;" + System.lineSeparator() + "}" + System.lineSeparator();
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileClassSegment(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var strip = input.strip();
		if (strip.isEmpty()) return new Tuple<>(state, "");

		final var tuple = Main.compileClassSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*/
/*private static Tuple<ParseState,*/ /*String>*/ compileClassSegmentValue(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		return Main.compileClass(state, input)
							 .or(() -> Main.compileField(state, input))
							 .or(() -> Main.compileMethod(state, input))
							 .orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileMethod(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var index = input.indexOf('(');
		if (0 > index) return Optional.empty();
		final var definition = input.substring(0, index);
		final var withParams = input.substring(index + 1);

		final var maybeTuple = Main.compileDefinitionOrPlaceholder(state, definition);
		if (maybeTuple.isEmpty()) return Optional.empty();
		final var tuple = maybeTuple.get();

		final var paramEnd = withParams.indexOf(')');
		if (0 > paramEnd) return Optional.empty();
		final var paramsString = withParams.substring(0, paramEnd);
		final var substring1 = withParams.substring(paramEnd + 1);

		final var tuple1 =
				Main.compileAll(tuple.left, paramsString, Main::foldValues, Main::getParseStateStringTuple, ", ");

		final var generated =
				tuple.right + "(" + tuple1.right + ")" + Main.generatePlaceholder(substring1) + System.lineSeparator();

		return Optional.of(new Tuple<>(tuple1.left.addFunction(generated), ""));

	}*/
/*private static Tuple<ParseState,*/ /*String>*/ getParseStateStringTuple(/*final*/ /*ParseState*/ state1, /*final*/ char* s)/* {
		return Main.compileDefinitionOrPlaceholder(state1, s)
							 .orElseGet(() -> new Tuple<>(state1, Main.generatePlaceholder(s)));
	}*/
/*private static*/ /*DivideState*/ foldValues(/*final*/ /*DivideState*/ state, /*final*/ /*char*/ c)/* {
		if (',' == c) return state.advance();
		return state.append(c);
	}*/
/*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileField(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return Optional.empty();
		final var withoutEnd = input.substring(0, input.length() - ";".length());

		final var i = withoutEnd.indexOf('=');
		if (0 > i) return Optional.empty();
		final var substring = withoutEnd.substring(0, i);
		final var maybeTuple = Main.compileDefinitionOrPlaceholder(state, substring);

		if (maybeTuple.isEmpty()) return Optional.empty();
		final var tuple = maybeTuple.get();

		return Optional.of(new Tuple<>(tuple.left, System.lineSeparator() + "\t" + tuple.right + ";"));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileDefinitionOrPlaceholder(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		return Main.compileDefinition(state, input).map(tuple -> new Tuple<>(tuple.left, tuple.right.generate()));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*CDefined>>*/ compileDefinition(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var strip = input.strip();
		final var i = strip.lastIndexOf(' ');
		if (0 <= i) {
			final var beforeName = strip.substring(0, i);
			final var name = strip.substring(i + 1);
			return Main.compileDefinitionBeforeName(state, beforeName, name);
		}

		return Optional.of(new Tuple<>(state, new Placeholder(input)));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*CDefined>>*/ compileDefinitionBeforeName(/*final*/ /*ParseState*/ state, /*final*/ char* beforeName, /*final*/ char* name)/* {
		final var typeSeparator = beforeName.lastIndexOf(' ');
		if (0 > typeSeparator) {
			final var tuple = Main.compileType0(state, beforeName);
			return Optional.of(new Tuple<>(tuple.left, new Definition(Optional.empty(), tuple.right, name)));
		}

		final var beforeType = beforeName.substring(0, typeSeparator).strip();
		final var type = beforeName.substring(typeSeparator + 1);

		if (!beforeType.isEmpty() && '>' == beforeType.charAt(beforeType.length() - 1)) {
			final var substring = beforeType.substring(0, beforeType.length() - ">".length());
			final var i = substring.indexOf('<');
			if (0 <= i) return Optional.empty();
		}

		final var tuple = Main.compileType0(state, type);
		return Optional.of(new Tuple<>(tuple.left, new Definition(Optional.of(beforeType), tuple.right, name)));
	}*/
/*private static Tuple<ParseState,*/ /*CType>*/ compileType0(/*final*/ /*ParseState*/ state, /*final*/ char* input)/* {
		final var strip = input.strip();
		if ("int".contentEquals(strip)) return new Tuple<>(state, Primitive.Int);
		if ("String".contentEquals(strip)) return new Tuple<>(state, new Ref(Primitive.Char));

		return Main.compileGenericType(state, strip)
							 .or(() -> Main.compileTypeParam(state, input))
							 .orElseGet(() -> new Tuple<>(state, new Placeholder(strip)));
	}*/
/*private static Optional<Tuple<ParseState,*/ /*CType>>*/ compileTypeParam(/*final*/ /*ParseState*/ state, /*final*/ /*CharSequence*/ input)/* {
		final var typeParameters = state.typeParameters;
		return IntStream.range(0, typeParameters.size()).mapToObj(index -> {
			return Main.getObject(state, input, index, typeParameters);
		}).flatMap(Optional::stream).findFirst().map(first -> new Tuple<>(state, first));
	}*/
/*private static*/ /*Optional<CType>*/ getObject(/*final*/ /*ParseState*/ state, /*final*/ /*CharSequence*/ input, /*final*/ int index, /*final*/ struct List_char_ref typeParameters)/* {
		if (typeParameters.get(index).contentEquals(input)) return Optional.of(state.typeArguments.get(index));
		else
			return Optional.empty();
	}*/
/*private static Optional<Tuple<ParseState,*/ /*CType>>*/ compileGenericType(/*final*/ /*ParseState*/ state, /*final*/ char* strip)/* {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var withoutEnd = strip.substring(0, strip.length() - ">".length());

		final var argumentStart = withoutEnd.indexOf('<');
		if (0 > argumentStart) return Optional.empty();
		final var name = withoutEnd.substring(0, argumentStart);
		final var argument = withoutEnd.substring(argumentStart + 1);

		final var tuple1 = Main.compileType0(state, argument);
		final var left = tuple1.left;
		final var outputType = tuple1.right;

		final var maybeStructure =
				left.javaStructures.stream().filter(structure -> structure.name.contentEquals(name)).findFirst();

		if (maybeStructure.isEmpty()) return Optional.empty();
		final var javaStructure = maybeStructure.get();

		final var monomorphizedName = javaStructure.name + "_" + outputType.stringify();
		final var parseState =
				Main.attachStructure(left.withParameters(javaStructure.typeParameters).withArgument(outputType),
														 javaStructure.modifiers, monomorphizedName, javaStructure.content, javaStructure.type);

		return Optional.of(new Tuple<>(parseState, new StructType(monomorphizedName)));
	}*/
/*private static*/ struct List_char_ref divide(/*final*/ /*CharSequence*/ input, /*final*/ BiFunction<DivideState, /* Character*/, /*DivideState>*/ folder)/* {
		final var length = input.length();
		var current = new DivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = folder.apply(current, c);
		}

		return new JavaList<>(current.advance().stream().toList());
	}*/
/*private static*/ /*DivideState*/ foldStatements(/*final*/ /*DivideState*/ state, /*final*/ /*char*/ c)/* {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}*/
/*if */(/*'{'*/ /*==*/ c)/* return current.enter();
		if ('}*/
struct Main Main(){
	struct Main this;
	return this;
}
/*private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*//*private enum Primitive implements CType {
		Int("int"), Char("char");

		private final String value;

		Primitive(final String value) {this.value = value;}

		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String stringify() {
			return this.value;
		}
	}*//*}*/