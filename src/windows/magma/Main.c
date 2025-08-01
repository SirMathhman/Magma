/*private static final */struct Lists {
};
/*private static */struct DivideState {
	/*private*/ /*List<String>*/ segments;
	/*private*/ /*String*/ buffer;
	/*private*/ int depth;
};
/*public final */struct Main {
	/*'*/;
	/*'*/;/*return current;*/
};
/*private*/ /*record*/ JavaList<T>(/*java.util.List<T> elements) implements Main.List<T> {
		private JavaList() {
			this(new ArrayList<>());
		}

		@Override
		public List<T> add(final T element) {
			this.elements.add(element);
			return this;
		}

		@Override
		public Stream<T> stream() {
			return this.elements.stream();
		}
	}*//*static <T>*/ /*List<T>*/ empty(/*) {
			return new JavaList<>();
		}*/struct Lists Lists(){
	struct Lists this;
	return this;
}/*private*/ /*DivideState*/ advance(/*) {
			this.segments = this.segments.add(this.buffer);
			this.buffer = "";
			return this;
		}*//*private*/ /*DivideState*/ append(/*final char c) {
			this.buffer = this.buffer + c;
			return this;
		}*//*private*/ /*boolean*/ isLevel(/*) {
			return 0 == this.depth;
		}*//*private*/ /*DivideState*/ exit(/*) {
			this.depth = this.depth - 1;
			return this;
		}*//*private*/ /*DivideState*/ enter(/*) {
			this.depth = this.depth + 1;
			return this;
		}*//*private*/ /*Stream<String>*/ stream(/*) {
			return this.segments.stream();
		}*//*final*/ /*boolean*/ isShallow(/*) {
			return 1 == this.depth;
		}*/struct DivideState DivideState(){
	struct DivideState this;
	return this;
}/*private*/ /*record*/ ParseState(/*List<String> structures, List<String> functions) {
		private ParseState() {
			this(Lists.empty(), Lists.empty());
		}

		ParseState addStructure(final String generated) {
			return new ParseState(this.structures.add(generated), this.functions);
		}

		ParseState addFunction(final String generated) {
			return new ParseState(this.structures, this.functions.add(generated));
		}
	}*//*private record*/ /*Tuple<Left,*/ Right>(/*Left left, Right right) {}*//*private*/ Main(/*) {}*//*public static*/ /*void*/ main(/*final String[] args) {
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
	}*//*private static*/ /*String*/ compile(/*final CharSequence input) {
		final var tuple = Main.compileStatements(new ParseState(), input, Main::compileRootSegment);
		final var newState = tuple.left;
		final var joined =
				Stream.of(newState.structures, newState.functions).flatMap(List::stream).collect(Collectors.joining());

		return joined + tuple.right;
	}*//*private static Tuple<ParseState,*/ /*String>*/ compileStatements(/*final ParseState state,
																														 final CharSequence input,
																														 final BiFunction<ParseState, String, Tuple<ParseState, String>> mapper) {
		final var current = Main.divide(input).stream().reduce(new Tuple<>(state, ""), (tuple, s) -> {
			final var tuple0 = mapper.apply(tuple.left, s);
			final var append = tuple.right + tuple0.right;
			return new Tuple<>(tuple0.left, append);
		}, (_, next) -> next);

		return new Tuple<>(current.left, current.right);
	}*//*private static Tuple<ParseState,*/ /*String>*/ compileRootSegment(/*final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return new Tuple<>(state, "");

		final var tuple = Main.compileRootSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*//*private static Tuple<ParseState,*/ /*String>*/ compileRootSegmentValue(/*final ParseState state, final String input) {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*//*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileClass(/*final ParseState state, final String input) {
		return Main.compileStructure("class", state, input).or(() -> Main.compileStructure("interface", state, input));
	}*//*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileStructure(/*final String type,
																																			final ParseState state,
																																			final String input) {
		final var classIndex = input.indexOf(type + " ");
		if (0 > classIndex) return Optional.empty();
		final var before = input.substring(0, classIndex);
		final var after = input.substring(classIndex + (type + " ").length());

		final var i = after.indexOf('{');
		if (0 > i) return Optional.empty();
		final var beforeContent = after.substring(0, i).strip();
		if (!beforeContent.isEmpty() && '>' == beforeContent.charAt(beforeContent.length() - 1)) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			if (substring.contains("<")) return Optional.of(new Tuple<>(state, ""));
		}

		final var withEnd = after.substring(i + 1).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		final var tuple = Main.compileStatements(state, content, Main::compileClassSegment);
		final var outputContent = tuple.right;
		final var generated =
				Main.generatePlaceholder(before) + "struct " + beforeContent + " {" + outputContent + System.lineSeparator() +
				"};" + System.lineSeparator();

		return Optional.of(
				new Tuple<>(tuple.left.addStructure(generated).addFunction(Main.generateConstructor(beforeContent)), ""));
	}*//*private static*/ /*String*/ generateConstructor(/*final String name) {
		return "struct " + name + " " + name + "(){" + System.lineSeparator() + "\tstruct " + name + " this;" +
					 System.lineSeparator() + "\treturn this;" + System.lineSeparator() + "}";
	}*//*private static Tuple<ParseState,*/ /*String>*/ compileClassSegment(/*final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.isEmpty()) return new Tuple<>(state, "");

		final var tuple = Main.compileClassSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}*//*private static Tuple<ParseState,*/ /*String>*/ compileClassSegmentValue(/*final ParseState state, final String input) {
		return Main.compileClass(state, input)
							 .or(() -> Main.compileField(state, input))
							 .or(() -> Main.compileMethod(state, input))
							 .orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}*//*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileMethod(/*final ParseState state, final String input) {
		final var index = input.indexOf('(');
		if (0 <= index) {
			final var definition = input.substring(0, index);
			final var withParams = input.substring(index + 1);
			final var generated = Main.compileDefinition(definition) + "(" + Main.generatePlaceholder(withParams);
			return Optional.of(new Tuple<>(state.addFunction(generated), ""));
		}

		return Optional.empty();
	}*//*private static Optional<Tuple<ParseState,*/ /*String>>*/ compileField(/*final ParseState state, final String input) {
		if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
			final var withoutEnd = input.substring(0, input.length() - ";".length());
			final var i = withoutEnd.indexOf('=');
			if (0 <= i) {
				final var substring = withoutEnd.substring(0, i);
				return Optional.of(new Tuple<>(state, System.lineSeparator() + "\t" + Main.compileDefinition(substring) + ";"));
			}
		}

		return Optional.empty();
	}*//*private static*/ /*String*/ compileDefinition(/*final String input) {
		final var strip = input.strip();
		final var i = strip.lastIndexOf(' ');
		if (0 <= i) {
			final var beforeName = strip.substring(0, i);
			final var name = strip.substring(i + 1);
			return Main.compileDefinitionBeforeName(beforeName) + " " + name;
		}

		return Main.generatePlaceholder(strip);
	}*//*private static*/ /*String*/ compileDefinitionBeforeName(/*final String beforeName) {
		final var typeSeparator = beforeName.lastIndexOf(' ');
		if (0 > typeSeparator) return Main.compileType(beforeName);

		final var beforeType = beforeName.substring(0, typeSeparator);
		final var type = beforeName.substring(typeSeparator + 1);
		return Main.generatePlaceholder(beforeType) + " " + Main.compileType(type);
	}*//*private static*/ /*String*/ compileType(/*final String input) {
		final var strip = input.strip();
		if ("int".contentEquals(strip)) return "int";

		return Main.generatePlaceholder(strip);
	}*//*private static*/ /*List<String>*/ divide(/*final CharSequence input) {
		final var length = input.length();
		var current = new DivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return new JavaList<>(current.advance().stream().toList());
	}*//*private static*/ /*DivideState*/ fold(/*final DivideState state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}*//*if*/(/*'{' == c) return current.enter();
		if ('}*/struct Main Main(){
	struct Main this;
	return this;
}/*private static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*//*}*/