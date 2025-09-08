/*public */struct Main {};
/*
	private static class State {
		private final StringBuilder buffer = new StringBuilder();
		private final ArrayList<String> segments = new ArrayList<>();
		private int depth = 0;

		private Stream<String> stream() {
			return segments.stream();
		}

		private State append(char c) {
			buffer.append(c);
			return this;
		}

		private State enter() {
			this.depth = depth + 1;
			return this;
		}

		private boolean isShallow() {
			return depth == 1;
		}

		private boolean isLevel() {
			return depth == 0;
		}

		private State advance() {
			segments.add(buffer.toString());
			buffer.setLength(0);
			return this;
		}

		private State exit() {
			this.depth = depth - 1;
			return this;
		}
	}*//*public static*/ void main(char** args){
	/*final*/ Path sourceDirectory = Paths.get(".", "src", "java");
	/*try (Stream<Path> stream = Files.walk(sourceDirectory)) {
			final Set<Path> sources = stream.filter(Files::isRegularFile)
																			.filter(path -> path.toString().endsWith(".java"))
																			.collect(Collectors.toSet());

			for (Path source : sources) {
				final String input = Files.readString(source);

				final Path relative = sourceDirectory.relativize(source);
				final Path parent = relative.getParent();
				final ArrayList<String> segments = new ArrayList<>();
				for (int i = 0; i < parent.getNameCount(); i++) {
					segments.add(parent.getName(i).toString());
				}

				Path targetDirectory = Paths.get(".", "src", "windows");
				for (String segment : segments) {
					targetDirectory = targetDirectory.resolve(segment);
				}

				Files.createDirectories(targetDirectory);

				final String fileName = relative.getFileName().toString();
				final int index = fileName.lastIndexOf(".");
				final String name = fileName.substring(0, index);
				final Path resolve = targetDirectory.resolve(name + ".c");
				Files.writeString(resolve, compile(input));
			}
		}*/
	/*catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}*/
	/**/}
/*private static*/ char* compile(char* input){
	/*return compileStatements(input, Main::compileRootSegment);*/
	/**/}
/*private static*/ char* compileStatements(/*String input, Function<String,*/ /*String>*/ mapper){
	/*return compileAll(input, mapper, Main::foldStatement, "");*/
	/**/}
/*private static*/ char* compileAll(/*String input,
																	 Function<String, String> mapper,
																	 BiFunction<State, Character, State> folder,
																	*/ char* delimiter){
	/*return divide(input, folder).map(mapper).collect(Collectors.joining(delimiter));*/
	/**/}
/*private static*/ /*Stream<String>*/ divide(/*String input, BiFunction<State, Character,*/ /*State>*/ folder){
	State current = /*new State*/();
	/*for*/ /*(int*/ i = /*0*/;
	/*i < input.length();*/
	/*i++) {
			final char c = input.charAt(i);
			current = folder.apply(current, c);
		}*/
	/*return current.advance().stream();*/
	/**/}
/*private static*/ State foldStatement(/*State current,*/ char c){
	/*final*/ State appended = current.append(c);
	if (c = /*= '*/;
	/*' && appended.isLevel()) return appended.advance();*/
	/*if (c == '*/}
/*' & appended.isShallow()) return appended.advance().exit();*//*
		if (c == '{') return appended.enter();
		if (c == '}*//*') return appended.exit();*//*
		return appended;*//*
	}*//*private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*//*private static String compileRootSegment(String input) {
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		final int classIndex = stripped.indexOf("*/struct ");
		if (classIndex >= 0) {};
/*
			final String modifiers = stripped.substring(0, classIndex);*//*
			final String remainder = stripped.substring(classIndex + "class ".length());*//*
			final int i = remainder.indexOf("{");
			if (i >= 0) {
				final String name = remainder.substring(0, i).strip();
				final String content = remainder.substring(i + "{".length());
				return wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() +
							 compileStatements(content, Main::compileClassSegment);
			}
		}

		return wrap(stripped);
	}*//*private static*/ char* compileClassSegment(char* input){
	/*final*/ int i = input.indexOf("(");
	/*if (i >= 0) {
			final String definition = input.substring(0, i);
			final String withParams = input.substring(i + "(".length());
			final int i1 = withParams.indexOf(")");
			if (i1 >= 0) {
				final String params = withParams.substring(0, i1);
				final String content = withParams.substring(i1 + ")".length()).strip();
				if (content.startsWith("{") && content.endsWith("}")) {
					final String slice = content.substring(1, content.length() - 1);
					final Optional<String> s = compileDefinition(definition);
					if (s.isPresent()) {
						return s.get() + "(" + compileDefinition(params).orElseGet(() -> wrap(params)) + "){" +
									 compileStatements(slice, Main::compileFunctionSegment) + "}" + System.lineSeparator();
					}
				}
			}
		}*/
	/*return wrap(input);*/
	/**/}
/*private static*/ /*Optional<String>*/ compileDefinition(char* input){
	/*final*/ char* stripped = input.strip();
	/*final*/ int i = stripped.lastIndexOf(" ");
	/*if (i < 0) return Optional.empty();*/
	/*final*/ char* beforeName = stripped.substring(/*0*/, /*i)*/.strip();
	/*final*/ char* name = stripped.substring(/*i + " "*/.length());
	/*final*/ int i1 = beforeName.lastIndexOf(" ");
	/*if (i1 < 0) {
			return Optional.of(compileType(beforeName) + " " + name);
		}*/
	/*final*/ char* modifiers = beforeName.substring(/*0*/, /*i1*/);
	/*final*/ char* type = beforeName.substring(/*i1 + " "*/.length());
	/*return Optional.of(wrap(modifiers) + " " + compileType(type) + " " + name);*/
	/**/}
/*private static*/ char* compileType(char* input){
	/*final*/ char* stripped = input.strip();
	/*if (stripped.equals("void")) return "void";*/
	/*if (stripped.equals("String")) return "char*";*/
	/*if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - "[]".length());
			return compileType(slice) + "*";
		}*/
	/*if (isIdentifier(stripped)) return stripped;*/
	/*return wrap(stripped);*/
	/**/}
/*private static*/ boolean isIdentifier(char* input){
	/*for*/ /*(int*/ i = /*0*/;
	/*i < input.length();*/
	/*i++) {
			final char c = input.charAt(i);
			if (!Character.isLetter(c)) return false;
		}*/
	/*return true;*/
	/**/}
/*private static*/ char* compileFunctionSegment(char* input){
	/*final*/ char* stripped = input.strip();
	/*return System.lineSeparator() + "\t" + compileFunctionSegmentValue(stripped);*/
	/**/}
/*private static*/ char* compileFunctionSegmentValue(char* input){
	/*if (input.endsWith(";*/
	/*")) {
			final String slice = input.substring(0, input.length() - ";".length());
			final int i = slice.indexOf("=");
			if (i >= 0) {
				final String definition = slice.substring(0, i);
				final String value = slice.substring(i + "=".length());
				final Optional<String> s = compileDefinition(definition);
				if (s.isPresent()) {
					return s.get() + " = " + compileExpression(value) + ";";
				}
			}
		}*/
	/*return wrap(input);*/
	/**/}
/*private static*/ char* compileExpression(char* value){
	/*final*/ char* stripped = value.strip();
	/*if (stripped.startsWith("\"") && stripped.endsWith("\"")) return stripped;*/
	/*if (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - ")".length());
			final int i = slice.indexOf("(");
			if (i >= 0) {
				final String caller = slice.substring(0, i);
				final String arguments = slice.substring(i + "(".length());
				return compileExpression(caller) + "(" +
							 compileAll(arguments, Main::compileExpression, Main::foldValues, ", ") + ")";
			}
		}*/
	/*final*/ int i = stripped.lastIndexOf(".");
	/*if (i >= 0) {
			final String parent = stripped.substring(0, i);
			final String property = stripped.substring(i + ".".length());
			return compileExpression(parent) + "." + property;
		}*/
	/*if (isIdentifier(stripped)) return stripped;*/
	/*return wrap(stripped);*/
	/**/}
/*private static*/ State foldValues(/*State state,*/ char c){
	if (c = /*= ',' && state*/.isLevel(/*)) return state*/.advance();
	/*final*/ State appended = state.append(c);
	if (c = /*= '*/(/*') return appended*/.enter();
	if (c = /*= ')') return appended*/.exit();
	/*return appended;*/
	/**/}
/*
}*/