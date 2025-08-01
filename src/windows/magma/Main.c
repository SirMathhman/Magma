/*private */struct Type {
	void* impl;
};
/*private static final */struct Lists {
};
/*private */struct List_char* {
	void* impl;
};
/*private static */struct DivideState {
	/*private*/ struct List_char* segments;
	/*private*/ char* buffer;
	/*private*/ int depth;
};
/*private */struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<Type> typeArguments, List<String> typeParameters) {
};
/*private */struct Tuple<Left, Right>(Left left, Right right) {
};
/*private */struct CStructure(String modifiers, String name, String content) {
};
/*private */struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) {
};
/*private */struct Ref(Type type) implements Type {
};
/*private static */struct StructType implements Type {/*private final String monomorphizedName;*/
};
/*private static */struct Placeholder implements Type {/*private final String strip;*/
};
/*public final */struct Main {
	/*'*/;
	/*'*/;/*return current;*/
};
char* generate(/*);*/
struct Type Type(){
	struct Type this;
	return this;
} empty(/*) {
			return new JavaList<>();
		}*/
 of(/*final T... values) {
			return new JavaList<>(new ArrayList<>(Arrays.asList(values)));
		}*/
struct Lists Lists(){
	struct Lists this;
	return this;
}struct List_char* add(/*T element);*/
char* stream(/*);*/
struct List_char* List_char*(){
	struct List_char* this;
	return this;
}/*private*/ char* advance(/*) {
			this.segments = this.segments.add(this.buffer);
			this.buffer = "";
			return this;
		}*/
/*private*/ char* append(/*final char c) {
			this.buffer = this.buffer + c;
			return this;
		}*/
/*private*/ char* isLevel(/*) {
			return 0 == this.depth;
		}*/
/*private*/ char* exit(/*) {
			this.depth = this.depth - 1;
			return this;
		}*/
/*private*/ char* enter(/*) {
			this.depth = this.depth + 1;
			return this;
		}*/
/*private*/ char* stream(/*) {
			return this.segments.stream();
		}*/
/*final*/ char* isShallow(/*) {
			return 1 == this.depth;
		}*/
struct DivideState DivideState(){
	struct DivideState this;
	return this;
}char* ParseState(/*) {
			this(Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty());
		}*/
char* addCStructure(/*final CStructure generated) {
			return new ParseState(this.javaStructures, this.cStructures.add(generated), this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}*/
char* addFunction(/*final String generated) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions.add(generated), this.visited,
														this.typeArguments, this.typeParameters);
		}*/
char* addJavaStructure(/*final JavaStructure javaStructure) {
			return new ParseState(this.javaStructures.add(javaStructure), this.cStructures, this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}*/
char* addVisited(/*final String name) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited.add(name),
														this.typeArguments, this.typeParameters);
		}*/
char* withArgument(/*final Type argument) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, Lists.of(argument),
														this.typeParameters);
		}*/
char* withTypeParameters(/*final String values) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, this.typeArguments,
														Lists.of(values));
		}*/
struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<Type> typeArguments, List<String> typeParameters) ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<Type> typeArguments, List<String> typeParameters)(){
	struct ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<Type> typeArguments, List<String> typeParameters) this;
	return this;
}struct Tuple<Left, Right>(Left left, Right right) Tuple<Left, Right>(Left left, Right right)(){
	struct Tuple<Left, Right>(Left left, Right right) this;
	return this;
}/*private*/ char* generate(/*) {
			return Main.generatePlaceholder(this.modifiers()) + "struct " + this.name() + " {" + this.content() +
						 System.lineSeparator() + "};" + System.lineSeparator();
		}*/
struct CStructure(String modifiers, String name, String content) CStructure(String modifiers, String name, String content)(){
	struct CStructure(String modifiers, String name, String content) this;
	return this;
}struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) JavaStructure(String type, String modifiers, String name, String typeParameters, String content)(){
	struct JavaStructure(String type, String modifiers, String name, String typeParameters, String content) this;
	return this;
}/*@Override
		public*/ char* generate(/*) {
			return this.type.generate() + "*";
		}*/
struct Ref(Type type) implements Type Ref(Type type) implements Type(){
	struct Ref(Type type) implements Type this;
	return this;
}char* StructType(/*final String monomorphizedName) {this.monomorphizedName = monomorphizedName;}*/
/*@Override
		public*/ char* generate(/*) {
			return "struct " + this.monomorphizedName;
		}*/
struct StructType implements Type StructType implements Type(){
	struct StructType implements Type this;
	return this;
}char* Placeholder(/*final String strip) {this.strip = strip;}*/
/*@Override
		public*/ char* generate(/*) {
			return Main.generatePlaceholder(this.strip);
		}*/
struct Placeholder implements Type Placeholder implements Type(){
	struct Placeholder implements Type this;
	return this;
}char* Main(/*) {}*/
/*public static*/ char* main(/*final String[] args) {
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
/*private static*/ char* compile(/*final CharSequence input) {
		final var tuple = Main.compileStatements(new ParseState(), input, Main::compileRootSegment);
		final var newState = tuple.left;
		final var joined = newState.cStructures.stream().map(CStructure::generate).collect(Collectors.joining()) +
											 newState.functions.stream().collect(Collectors.joining());

		return joined + tuple.right;
	}*/
/*private static Tuple<ParseState,*/ char* compileStatements(/*final ParseState state,
																														 final CharSequence input,
																														 final BiFunction<ParseState, String, Tuple<ParseState, String>> mapper) {
		final var current = Main.divide(input).stream().reduce(new Tuple<>(state, ""), (tuple, s) -> {
			final var tuple0 = mapper.apply(tuple.left, s);
			final var append = tuple.right + tuple0.right;
			return new Tuple<>(tuple0.left, append);
		}, (_, next) -> next);

		return new Tuple<>(current.left, current.right);
	}*/
/*private static Tuple<ParseState,*/ char* compileRootSegment(/*final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return new Tuple<>(state, "");

		final var tuple = Main.compileRootSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*/
/*private static Tuple<ParseState,*/ char* compileRootSegmentValue(/*final ParseState state, final String input) {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileClass(/*final ParseState state, final String input) {
		return Main.compileStructure("class", state, input)
							 .or(() -> Main.compileStructure("interface", state, input))
							 .or(() -> Main.compileStructure("record", state, input));
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileStructure(/*final String type,
																																			final ParseState state,
																																			final String input) {
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
/*private static*/ char* assembleStructure(/*final ParseState state,
																							final String modifiers,
																							final String beforeContent,
																							final String content,
																							final String type) {
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
/*private static*/ char* attachStructure(/*final ParseState state,
																						final String modifiers,
																						final String name,
																						final CharSequence content,
																						final CharSequence type) {
		if (state.visited.stream().anyMatch(name::contentEquals)) return state;
		final var withVisited = state.addVisited(name);

		final var tuple = Main.compileStatements(withVisited, content, Main::compileClassSegment);
		final var outputContent = tuple.right;

		final var generated = new CStructure(modifiers, name, Main.createBeforeContent(type) + outputContent);
		return tuple.left.addCStructure(generated).addFunction(Main.generateConstructor(name));
	}*/
/*private static*/ char* createBeforeContent(/*final CharSequence type) {
		if ("interface".contentEquals(type)) return System.lineSeparator() + "\tvoid* impl;";
		return "";
	}*/
/*private static*/ char* generateConstructor(/*final String name) {
		return "struct " + name + " " + name + "(){" + System.lineSeparator() + "\tstruct " + name + " this;" +
					 System.lineSeparator() + "\treturn this;" + System.lineSeparator() + "}";
	}*/
/*private static Tuple<ParseState,*/ char* compileClassSegment(/*final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.isEmpty()) return new Tuple<>(state, "");

		final var tuple = Main.compileClassSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*/
/*private static Tuple<ParseState,*/ char* compileClassSegmentValue(/*final ParseState state, final String input) {
		return Main.compileClass(state, input)
							 .or(() -> Main.compileField(state, input))
							 .or(() -> Main.compileMethod(state, input))
							 .orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileMethod(/*final ParseState state, final String input) {
		final var index = input.indexOf('(');
		if (0 <= index) {
			final var definition = input.substring(0, index);
			final var withParams = input.substring(index + 1);
			final var tuple = Main.compileDefinition(state, definition);
			final var generated = tuple.right + "(" + Main.generatePlaceholder(withParams) + System.lineSeparator();
			return Optional.of(new Tuple<>(tuple.left.addFunction(generated), ""));
		}

		return Optional.empty();
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileField(/*final ParseState state, final String input) {
		if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
			final var withoutEnd = input.substring(0, input.length() - ";".length());
			final var i = withoutEnd.indexOf('=');
			if (0 <= i) {
				final var substring = withoutEnd.substring(0, i);
				final var tuple = Main.compileDefinition(state, substring);
				return Optional.of(new Tuple<>(tuple.left, System.lineSeparator() + "\t" + tuple.right + ";"));
			}
		}

		return Optional.empty();
	}*/
/*private static Tuple<ParseState,*/ char* compileDefinition(/*final ParseState state, final String input) {
		final var strip = input.strip();
		final var i = strip.lastIndexOf(' ');
		if (0 <= i) {
			final var beforeName = strip.substring(0, i);
			final var name = strip.substring(i + 1);
			final var tuple = Main.compileDefinitionBeforeName(state, beforeName);
			return new Tuple<>(tuple.left, tuple.right + " " + name);
		}

		return new Tuple<>(state, Main.generatePlaceholder(strip));
	}*/
/*private static Tuple<ParseState,*/ char* compileDefinitionBeforeName(/*final ParseState state,
																																			 final String beforeName) {
		final var typeSeparator = beforeName.lastIndexOf(' ');
		if (0 > typeSeparator) return Main.compileType(state, beforeName);

		final var beforeType = beforeName.substring(0, typeSeparator).strip();
		final var type = beforeName.substring(typeSeparator + 1);

		if (!beforeType.isEmpty() && '>' == beforeType.charAt(beforeType.length() - 1)) {
			final var substring = beforeType.substring(0, beforeType.length() - ">".length());
			final var i = substring.indexOf('<');
			if (0 <= i) return new Tuple<>(state, "");
		}

		return Main.assembleDefinition(state, type, beforeType);
	}*/
/*private static Tuple<ParseState,*/ char* assembleDefinition(/*final ParseState state,
																															final String type,
																															final String beforeType) {
		final var tuple = Main.compileType(state, type);
		final var generated = Main.generatePlaceholder(beforeType) + " " + tuple.right;
		return new Tuple<>(tuple.left, generated);
	}*/
/*private static Tuple<ParseState,*/ char* compileType(/*final ParseState state, final String input) {
		final var tuple = Main.compileType0(state, input);
		return new Tuple<>(tuple.left, tuple.right.generate());
	}*/
/*private static Tuple<ParseState,*/ char* compileType0(/*final ParseState state, final String input) {
		final var strip = input.strip();
		if ("int".contentEquals(strip)) return new Tuple<>(state, Primitive.Int);
		if ("String".contentEquals(strip)) return new Tuple<>(state, new Ref(Primitive.Char));

		return Main.compileGenericType(state, strip)
							 .or(() -> Main.compileTypeParam(state, strip))
							 .orElseGet(() -> new Tuple<>(state, new Placeholder(strip)));
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileTypeParam(/*final ParseState state, final String input) {
		return state.typeArguments.stream().findFirst().map(first -> {
			return new Tuple<>(state, first);
		});
	}*/
/*private static Optional<Tuple<ParseState,*/ char* compileGenericType(/*final ParseState state, final String strip) {
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

		final var monomorphizedName = javaStructure.name + "_" + outputType.generate();
		final var parseState =
				Main.attachStructure(left.withArgument(outputType), javaStructure.modifiers, monomorphizedName,
														 javaStructure.content, javaStructure.type);

		return Optional.of(new Tuple<>(parseState, new StructType(monomorphizedName)));
	}*/
/*private static*/ struct List_char* divide(/*final CharSequence input) {
		final var length = input.length();
		var current = new DivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return new JavaList<>(current.advance().stream().toList());
	}*/
/*private static*/ char* fold(/*final DivideState state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}*/
/*if*/(/*'{' == c) return current.enter();
		if ('}*/
struct Main Main(){
	struct Main this;
	return this;
}/*private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*//*private enum Primitive implements Type {
		Int("int"), Char("char");

		private final String value;

		Primitive(final String value) {this.value = value;}

		@Override
		public String generate() {
			return this.value;
		}
	}*//*}*/