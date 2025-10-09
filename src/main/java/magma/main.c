struct Main {};
struct State {};
/*public final StringBuilder buffer = new StringBuilder();*/
/*private final List<String> segments = new ArrayList<String>();*/
/*private int depth = 0;*/
/*private*/ /*Stream<String>*/ stream(/**/){/*
			return segments.stream();
		*/}
/*private*/ /*State*/ enter(/**/){/*
			this.depth = depth + 1;
			return this;
		*/}
/*private*/ /*State*/ exit(/**/){/*
			this.depth = depth - 1;
			return this;
		*/}
/*private*/ /*boolean*/ isShallow(/**/){/*
			return depth == 1;
		*/}
/*private*/ /*State*/ advance(/**/){/*
			segments.add(buffer.toString());
			buffer.setLength(0);
			return this;
		*/}
/*private*/ /*boolean*/ isLevel(/**/){/*
			return depth == 0;
		*/}
/*private*/ /*State*/ append(/*char*/ c){/*
			buffer.append(c);
			return this;
		*/}
/**/

/*public static*/ void main(/*String[]*/ args){/*
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	*/}
/*private static*/ /*String*/ compile(/*String*/ input){/*
		return compileStatements(input, Main::compileRootSegment);
	*/}
/*private static*/ /*String*/ compileStatements(/*String*/ input/*Function<String*//*String>*/ mapper){/*
		return compileAll(input, Main::foldStatement, mapper);
	*/}
/*private static*/ /*String*/ compileAll(/*String*/ input/*BiFunction<State*//*Character*//*State>*/ folder/*Function<String*//*String>*/ mapper){/*
		return divide(input, folder).map(mapper).collect(Collectors.joining());
	*/}
/*private static*/ /*Stream<String>*/ divide(/*String*/ input/*BiFunction<State*//*Character*//*State>*/ folder){/*
		State current = new State();
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			current = folder.apply(current, c);
		}

		return current.advance().stream();
	*/}
/*private static*/ /*State*/ foldStatement(/*State*/ state/*char*/ c){/*
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '*/}
/*' && appended.isShallow()) return appended.advance().exit();*/
/*if (c == '*/{};
/*') return appended.enter();*/
/*if (c == '*/

/*') return appended.exit();*/
/*return appended;*/
/**/
/*private static String compileRootSegment(String input) */{};
/*final String strip = input.strip();*/
/*if (strip.startsWith("package ") || strip.startsWith("import ")) return "";*/
/*return compileClass(strip).orElseGet(() -> wrap(strip));*/
/**/
/*private static Optional<String> compileClass(String input) */{};
/*if (!input.endsWith("*/
/*")) return Optional.empty();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());
		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return Optional.empty();*//*final String substring = withoutEnd.substring(0, index);*//*final String body = withoutEnd.substring(index + "*/{};
/*".length());*/
/*return Optional.of(compileStructureHeader(substring) + "*/{};
/**/

/*;*/
/*" + System.lineSeparator() +
											 compileStatements(body, Main::compileClassSegment));*/
/**/
/*private static String compileClassSegment(String input) */{};
/*final String stripped = input.strip();*/
/*return compileClassSegmentValue(stripped) + System.lineSeparator();*/
/**/
/*private static String compileClassSegmentValue(String input) */{};
/*return compileMethod(input).or(() -> compileClass(input)).orElseGet(() -> wrap(input));*/
/**/
/*private static Optional<String> compileMethod(String input) */{};
/*if (!input.endsWith("*/
/*")) return Optional.empty();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());

		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return Optional.empty();*//*final String header = withoutEnd.substring(0, index).strip();*//*final String body = withoutEnd.substring(index + "*/{};
/*".length());*/
/*if (!header.endsWith(")")) return Optional.empty();*/
/*final String headerWithoutEnd = header.substring(0, header.length() - ")".length());*/
/*final int paramStart = headerWithoutEnd.indexOf("(");*/
/*if (paramStart < 0) return Optional.empty();*/
/*final String definition = headerWithoutEnd.substring(0, paramStart);*/
/*final String params = headerWithoutEnd.substring(paramStart + "(".length());*/
/*return*/ Optional.of(/*compileDefinition(definition) + "("*/ /*+*/ compileAll(params/*Main::foldValue*//*Main::compileDefinition)*/ /*+*/ "){/*" +
				wrap(body) + "*/}
/*");*/
/**/
/*private static State foldValue(State state, char c) */{};
/*if (c == ',') return state.advance();*/
/*else return state.append(c);*/
/**/
/*private static String compileDefinition(String input) */{};
/*final String stripped = input.strip();*/
/*final int index = stripped.lastIndexOf(" ");*/
/*if*/(/*index*/ /*>=*/ 0){/*
			final String beforeName = stripped.substring(0, index).strip();
			final String name = stripped.substring(index + " ".length());
			final int typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final String beforeType = beforeName.substring(0, typeSeparator);
				final String type = beforeName.substring(typeSeparator + " ".length());
				return wrap(beforeType) + " " + compileType(type) + " " + name;
			} else return compileType(beforeName) + " " + name;
		*/}
/*return wrap(stripped);*/
/**/
/*private static String compileType(String input) */{};
/*final String stripped = input.strip();*/
/*if (stripped.equals("void")) return "void";*/
/*return wrap(stripped);*/
/**/
/*private static String compileStructureHeader(String input) */{};
/*final int index = input.indexOf("class ");*/
/*if*/(/*index*/ /*>=*/ 0){/*
			final String name = input.substring(index + "class ".length());
			return "struct " + name;
		*/}
/*return wrap(input);*/
/**/
/*private static String wrap(String input) */{};
/*return "start" + input.replace("start", "start").replace("end", "end") + "end";*/
/**/
/*}*/