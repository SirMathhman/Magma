// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Main {
};
struct State {
	/*private final*/ ArrayList<char*> segments;
	/*private final*/ char* input;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ int depth;
	/*private*/ int index;
};
State new_State(char* input){
	State this;
	this.input = input;
	this.buffer = /*new StringBuilder()*/;
	this.depth = 0;
	this.segments = /*new ArrayList<String>()*/;
	this.index = 0;
	return this;
}
/*private*/ Stream<char*> stream(){
	State this;
	/*return this.segments.stream()*/;
	return this;
}
/*private*/ /*State*/ enter(){
	State this;
	this.depth = this.depth + 1;
	/*return this*/;
	return this;
}
/*private*/ /*State*/ exit(){
	State this;
	this.depth = this.depth - 1;
	/*return this*/;
	return this;
}
/*private*/ /*boolean*/ isShallow(){
	State this;
	/*return this*/.depth = /*= 1*/;
	return this;
}
/*private*/ /*boolean*/ isLevel(){
	State this;
	/*return this*/.depth = /*= 0*/;
	return this;
}
/*private*/ /*State*/ append(/*char*/ c){
	State this;
	/*this.buffer.append(c)*/;
	/*return this*/;
	return this;
}
/*private*/ /*State*/ advance(){
	State this;
	/*this.segments.add(this.buffer.toString())*/;
	this.buffer = /*new StringBuilder()*/;
	/*return this*/;
	return this;
}
/*public Optional<Tuple<State,*/ /*Character>>*/ pop(){
	State this;
	/*if (this*/.index > = this.input.length()) return Optional.empty();
	/*final char next*/ = this.input.charAt(this.index);
	/*this.index++*/;
	/*return Optional.of(new Tuple<State, Character>(this, next))*/;
	return this;
}
/*public Optional<Tuple<State,*/ /*Character>>*/ popAndAppendToTuple(){
	State this;
	/*return this.pop().map(tuple -> new Tuple<State, Character>(tuple.left.append(tuple.right), tuple.right))*/;
	return this;
}
/*public*/ Optional</*State*/> popAndAppendToOption(){
	State this;
	/*return this.popAndAppendToTuple().map(tuple -> tuple.left)*/;
	return this;
}
template <typeparam A, typeparam B>
struct Tuple {
	/*A*/ left;
	/*B*/ right;
};
/*public static*/ /*void*/ main(/*String[]*/ args){
	Main this;
	/*try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);

			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
			final Path targetParent = target.getParent();

			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
			Files.writeString(target, "// File generated from '" + source + "'. This is not source code!\n" + compile(input));
		}*/
	/*catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}*/
	return this;
}
/*private static*/ char* compile(char* input){
	Main this;
	/*final String joined*/ = /*compileStatements(input, Main::compileRootSegment)*/;
	/*return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() +
					 "\treturn 0;" + System.lineSeparator() + "}"*/;
	return this;
}
/*private static*/ char* compileStatements(char* input/**/ Function<String/*String>*/ mapper){
	Main this;
	/*return compileAll(input, Main::foldStatement, mapper)*/;
	return this;
}
/*private static*/ char* compileAll(char* input/**/ BiFunction<State/**/ Character/*State>*/ folder/**/ Function<String/*String>*/ mapper){
	Main this;
	/*return divide(input, folder).map(mapper).collect(Collectors.joining())*/;
	return this;
}
/*private static*/ Stream<char*> divide(char* input/**/ BiFunction<State/**/ Character/*State>*/ folder){
	Main this;
	/*State current*/ = /*new State(input)*/;
	/*while (true) {
			final Optional<Tuple<State, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<State, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}*/
	/*return current.advance().stream()*/;
	return this;
}
/*private static*/ /*State*/ foldEscaped(/*State*/ state/*char*/ next/**/ BiFunction<State/**/ Character/*State>*/ folder){
	Main this;
	/*return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next))
																				.orElseGet(() -> folder.apply(state, next))*/;
	return this;
}
/*private static*/ Optional</*State*/> foldSingleQuotes(/*State*/ state/*char*/ next){
	Main this;
	/*if (next !*/ = /*'\'') return Optional*/.empty();
	/*final State appended*/ = state.append(next);
	/*return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(State::popAndAppendToOption)*/;
	return this;
}
/*private static*/ Optional</*State*/> foldEscaped(/*Tuple<State*//*Character>*/ tuple){
	Main this;
	/*if (tuple*/.right = /*= '\\') return tuple*/.left.popAndAppendToOption();
	/*else return Optional.of(tuple.left)*/;
	return this;
}
/*private static*/ Optional</*State*/> foldDoubleQuotes(/*State*/ state/*char*/ next){
	Main this;
	/*if (next !*/ = /*'\"') return Optional*/.empty();
	/*State appended*/ = state.append(next);
	/*while (true) {
			final Optional<Tuple<State, Character>> maybeNext = appended.popAndAppendToTuple();
			if (maybeNext.isPresent()) {
				final Tuple<State, Character> tuple = maybeNext.get();
				appended = tuple.left;

				final char c = tuple.right;
				if (c == '\\') appended = appended.popAndAppendToOption().orElse(appended);
				if (c == '\"') break;
			} else break;
		}*/
	/*return Optional.of(appended)*/;
	return this;
}
/*private static*/ /*State*/ foldStatement(/*State*/ state/*char*/ c){
	Main this;
	/*final State appended*/ = state.append(c);
	/*if (c*/ = /*= ';' && appended*/.isLevel()) return appended.advance();
	/*if (c*/ = /*= '}' && appended*/.isShallow()) return appended.advance().exit();
	/*if (c*/ = /*= '{') return appended*/.enter();
	/*if (c*/ = /*= '}') return appended*/.exit();
	/*return appended*/;
	return this;
}
/*private static*/ char* compileRootSegment(char* input){
	Main this;
	/*final String stripped*/ = input.strip();
	/*if (stripped.startsWith("package ") || stripped.startsWith("import ")) return ""*/;
	/*return compileStructure(stripped, "class").map(Tuple::right).orElseGet(() -> wrap(stripped))*/;
	return this;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileStructure(char* inputchar* type){
	Main this;
	/*final int i*/ = input.indexOf(type + " ");
	/*if (i < 0) return Optional.empty()*/;
	/*final String afterKeyword*/ = input.substring(i + (type + " ").length());
	/*final int contentStart*/ = afterKeyword.indexOf("{");
	/*if (contentStart < 0) return Optional.empty()*/;
	/*final String beforeContent*/ = afterKeyword.substring(0, contentStart).strip();
	/*// if (!isIdentifier(beforeContent)) return Optional.empty()*/;
	/*String beforeMaybeParams*/ = beforeContent;
	/*String recordFields*/ = /*""*/;
	/*if (beforeContent.endsWith(")")) {
			final String slice = beforeContent.substring(0, beforeContent.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter);
			}
		}*/
	/*String name*/ = beforeMaybeParams;
	/*List<String> typeArguments*/ = Collections.emptyList();
	/*if (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1);
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeArguments = divide(arguments, Main::foldValue).map(String::strip).toList();
			}
		}*/
	/*final String afterContent*/ = afterKeyword.substring(contentStart + "{".length()).strip();
	/*if (!afterContent.endsWith("}")) return Optional.empty()*/;
	/*final String content*/ = afterContent.substring(0, afterContent.length() - "}".length());
	/*final List<String> segments*/ = /*divide(content, Main::foldStatement)*/.toList();
	/*StringBuilder inner*/ = /*new StringBuilder()*/;
	/*final StringBuilder outer*/ = /*new StringBuilder()*/;
	/*for (String segment : segments) {
			Tuple<String, String> compiled = compileClassSegment(segment, name);
			inner.append(compiled.left);
			outer.append(compiled.right);
		}*/
	/*String beforeStruct*/;
	/*if (typeArguments*/.isEmpty()) beforeStruct = /*""*/;
	/*else {
			final String templateValues =
					typeArguments.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">")) +
					System.lineSeparator();

			beforeStruct = "template " + templateValues;
		}*/
	/*return Optional.of(new Tuple<String, String>("",
																								 beforeStruct + "struct " + name + " {" + recordFields + inner +
																								 System.lineSeparator() + "};" + System.lineSeparator() + outer))*/;
	return this;
}
/*private static*/ char* compileValues(char* input/**/ Function<String/*String>*/ mapper){
	Main this;
	/*return compileAll(input, Main::foldValue, mapper)*/;
	return this;
}
/*private static*/ char* compileParameter(char* input1){
	Main this;
	/*if (input1.isEmpty()) return ""*/;
	/*return generateField(input1)*/;
	return this;
}
/*private static*/ char* generateField(char* input){
	Main this;
	/*return generateStatement(compileDefinition(input).orElseGet(() -> wrap(input)))*/;
	return this;
}
/*private static*/ char* generateStatement(char* content){
	Main this;
	/*return System.lineSeparator() + "\t" + content + ";"*/;
	return this;
}
/*private static*/ /*State*/ foldValue(/*State*/ state/*char*/ next){
	Main this;
	/*if (next*/ = /*= ',' && state*/.isLevel()) return state.advance();
	/*else return state.append(next)*/;
	return this;
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegment(char* inputchar* name){
	Main this;
	/*final String stripped*/ = input.strip();
	/*if (stripped.isEmpty()) return new Tuple<String, String>("", "")*/;
	/*return compileClassSegmentValue(stripped, name)*/;
	return this;
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegmentValue(char* inputchar* name){
	Main this;
	/*return compileStructure(input, "class").or(() -> compileStructure(input, "record"))
																					 .or(() -> compileField(input))
																					 .or(() -> compileMethod(input, name))
																					 .orElseGet(() -> new Tuple<String, String>(
																							 wrap(input) + System.lineSeparator(), ""))*/;
	return this;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileMethod(char* inputchar* name){
	Main this;
	/*final int paramStart*/ = input.indexOf("(");
	/*if (paramStart < 0) return Optional.empty()*/;
	/*final String beforeParams*/ = input.substring(0, paramStart).strip();
	/*final String withParams*/ = input.substring(paramStart + 1);
	/*final int paramEnd*/ = withParams.indexOf(")");
	/*if (paramEnd < 0) return Optional.empty()*/;
	/*final String definition*/ = /*compileDefinition(beforeParams)*/.or(() -> compileConstructor(beforeParams)).orElseGet(() -> wrap(beforeParams));
	/*final String inputParams*/ = withParams.substring(0, paramEnd);
	/*final String withBraces*/ = withParams.substring(paramEnd + 1).strip();
	/*if (!withBraces.startsWith("{") || !withBraces.endsWith("}")) return Optional.empty()*/;
	/*final String inputBody*/ = withBraces.substring(1, withBraces.length() - 1);
	/*final String outputParams*/ = /*compileParameters(inputParams)*/;
	/*final String compiledBody*/ = /*compileStatements(inputBody, Main::compileMethodSegment)*/;
	/*final String outputBody*/ = /*generateStatement(name + " this") + compiledBody + generateStatement("return this")*/;
	/*final String generated*/ = /*definition + "(" + outputParams + "){" + outputBody + System*/.lineSeparator() + "}" + System.lineSeparator();
	/*return Optional.of(new Tuple<String, String>("", generated))*/;
	return this;
}
/*private static*/ char* compileParameters(char* input){
	Main this;
	/*if(input.isEmpty()) return ""*/;
	/*return compileValues(input, slice -> compileDefinition(slice).orElse(""))*/;
	return this;
}
/*private static*/ char* compileMethodSegment(char* input){
	Main this;
	/*final String stripped*/ = input.strip();
	/*if (stripped.isEmpty()) return ""*/;
	/*return System.lineSeparator() + "\t" + compileMethodSegmentValue(stripped)*/;
	return this;
}
/*private static*/ char* compileMethodSegmentValue(char* input){
	Main this;
	/*if (input.endsWith(";")) {
			final String slice = input.substring(0, input.length() - 1);
			return compileMethodStatementValue(slice) + ";";
		}*/
	/*return wrap(input)*/;
	return this;
}
/*private static*/ char* compileMethodStatementValue(char* input){
	Main this;
	/*final int i*/ = input.indexOf("=");
	/*if (i >= 0) {
			final String destination = input.substring(0, i);
			final String source = input.substring(i + 1);
			return compileExpression(destination) + " = " + compileExpression(source);
		}*/
	/*return wrap(input)*/;
	return this;
}
/*private static*/ char* compileExpression(char* input){
	Main this;
	/*final String stripped*/ = input.strip();
	/*final int i*/ = stripped.indexOf(".");
	/*if (i >= 0) {
			final String substring = stripped.substring(0, i);
			final String name = stripped.substring(i + 1);
			return compileExpression(substring) + "." + name;
		}*/
	/*if (isIdentifier(stripped)) return stripped*/;
	/*if (isNumber(stripped)) return stripped*/;
	/*return wrap(stripped)*/;
	return this;
}
/*private static*/ /*boolean*/ isNumber(char* input){
	Main this;
	/*for (int i*/ = 0;
	/*i < input.length()*/;
	/*i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) return false;
		}*/
	/*return true*/;
	return this;
}
/*private static*/ /*boolean*/ isIdentifier(char* input){
	Main this;
	/*for (int i*/ = 0;
	/*i < input.length()*/;
	/*i++) if (!Character.isLetter(input.charAt(i))) return false*/;
	/*return true*/;
	return this;
}
/*private static*/ Optional<char*> compileConstructor(char* beforeParams){
	Main this;
	/*final int separator*/ = beforeParams.lastIndexOf(" ");
	/*if (separator < 0) return Optional.empty()*/;
	/*final String name*/ = beforeParams.substring(separator + " ".length());
	/*return Optional.of(name + " new_" + name)*/;
	return this;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileField(char* input){
	Main this;
	/*if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			return Optional.of(new Tuple<String, String>(generateField(substring), ""));
		}*/
	/*else return Optional.empty()*/;
	return this;
}
/*private static*/ Optional<char*> compileDefinition(char* input){
	Main this;
	/*final int index*/ = input.lastIndexOf(" ");
	/*if (index < 0) return Optional.of(wrap(input))*/;
	/*final String beforeName*/ = input.substring(0, index).strip();
	/*final String name*/ = input.substring(index + " ".length()).strip();
	/*final int typeSeparator*/ = beforeName.lastIndexOf(" ");
	/*if (typeSeparator < 0) return compileType(beforeName).map(type -> type + " " + name)*/;
	/*final String beforeType*/ = beforeName.substring(0, typeSeparator);
	/*final String typeString*/ = beforeName.substring(typeSeparator + " ".length());
	/*return compileType(typeString).map(type -> wrap(beforeType) + " " + type + " " + name)*/;
	return this;
}
/*private static*/ Optional<char*> compileType(char* input){
	Main this;
	/*final String stripped*/ = input.strip();
	/*if (stripped.equals("public")) return Optional.empty()*/;
	/*if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String arguments = withoutEnd.substring(argumentStart + "<".length());
				return Optional.of(base + "<" + compileType(arguments).orElse("") + ">");
			}
		}*/
	/*if (stripped.equals("String")) return Optional.of("char*")*/;
	/*if (stripped.equals("int")) return Optional.of("int")*/;
	/*return Optional.of(wrap(stripped))*/;
	return this;
}
/*private static*/ char* wrap(char* input){
	Main this;
	/*final String replaced*/ = input.replace("/*", "start").replace("*/", "end");
	/*return "start" + replaced + "end"*/;
	return this;
}
/**/int main(){
	main_Main();
	return 0;
}