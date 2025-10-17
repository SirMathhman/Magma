// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Main {
};
struct Definable extends MethodHeader permits Definition, Placeholder {
	char* generate();
};
/*@Override
		default*/ /*Definable*/ toDefinable(){
	/*return this*/;
}
struct MethodHeader permits Constructor, Definable {
	/*Definable*/ toDefinable();
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
	/*return this.segments.stream()*/;
}
/*private*/ /*State*/ enter(){
	this.depth = this.depth + 1;
	/*return this*/;
}
/*private*/ /*State*/ exit(){
	this.depth = this.depth - 1;
	/*return this*/;
}
/*private*/ /*boolean*/ isShallow(){
	/*return this*/.depth = /*= 1*/;
}
/*private*/ /*boolean*/ isLevel(){
	/*return this*/.depth = /*= 0*/;
}
/*private*/ /*State*/ append(/*char*/ c){
	/*this.buffer.append(c)*/;
	/*return this*/;
}
/*private*/ /*State*/ advance(){
	/*this.segments.add(this.buffer.toString())*/;
	this.buffer = /*new StringBuilder()*/;
	/*return this*/;
}
/*public Optional<Tuple<State,*/ /*Character>>*/ pop(){
	/*if (this*/.index > = this.input.length()) return Optional.empty();
	/*final char next*/ = this.input.charAt(this.index);
	/*this.index++*/;
	/*return Optional.of(new Tuple<State, Character>(this, next))*/;
}
/*public Optional<Tuple<State,*/ /*Character>>*/ popAndAppendToTuple(){
	/*return this.pop().map(tuple -> new Tuple<State, Character>(tuple.left.append(tuple.right), tuple.right))*/;
}
/*public*/ Optional</*State*/> popAndAppendToOption(){
	/*return this.popAndAppendToTuple().map(tuple -> tuple.left)*/;
}
template <typeparam A, typeparam B>
struct Tuple {
	/*A*/ left;
	/*B*/ right;
};
struct Definition(Optional<String> beforeType, String type, String name) implements Definable {
};
Definition new_Definition(char* typechar* name){
	Definition(Optional<String> beforeType, String type, String name) implements Definable this;
	/*this(Optional.empty(), type, name)*/;
	return this;
}
/*@Override
		public*/ char* generate(){
	/*return this.beforeType.map(Main::wrap).map(value -> value + " ").orElse("") + this.type() + " " + this.name()*/;
}
struct Placeholder(String input) implements Definable {
};
/*@Override
		public*/ char* generate(){
	/*return wrap(this.input)*/;
}
struct Constructor(String name) implements MethodHeader {
};
/*@Override
		public*/ /*Definable*/ toDefinable(){
	/*return new Definition(this.name, "new_" + this.name)*/;
}
/*public static*/ /*void*/ main(/*String[]*/ args){
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
}
/*private static*/ char* compile(char* input){
	/*final String joined*/ = /*compileStatements(input, Main::compileRootSegment)*/;
	/*return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() +
					 "\treturn 0;" + System.lineSeparator() + "}"*/;
}
/*private static*/ char* compileStatements(char* input/**/ Function<String/*String>*/ mapper){
	/*return compileAll(input, Main::foldStatement, mapper)*/;
}
/*private static*/ char* compileAll(char* input/**/ BiFunction<State/**/ Character/*State>*/ folder/**/ Function<String/*String>*/ mapper){
	/*return divide(input, folder).map(mapper).collect(Collectors.joining())*/;
}
/*private static*/ Stream<char*> divide(char* input/**/ BiFunction<State/**/ Character/*State>*/ folder){
	/*State current*/ = /*new State(input)*/;
	/*while (true) {
			final Optional<Tuple<State, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<State, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}*/
	/*return current.advance().stream()*/;
}
/*private static*/ /*State*/ foldEscaped(/*State*/ state/*char*/ next/**/ BiFunction<State/**/ Character/*State>*/ folder){
	/*return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next))
																				.orElseGet(() -> folder.apply(state, next))*/;
}
/*private static*/ Optional</*State*/> foldSingleQuotes(/*State*/ state/*char*/ next){
	/*if (next !*/ = /*'\'') return Optional*/.empty();
	/*final State appended*/ = state.append(next);
	/*return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(State::popAndAppendToOption)*/;
}
/*private static*/ Optional</*State*/> foldEscaped(/*Tuple<State*//*Character>*/ tuple){
	/*if (tuple*/.right = /*= '\\') return tuple*/.left.popAndAppendToOption();
	/*else return Optional.of(tuple.left)*/;
}
/*private static*/ Optional</*State*/> foldDoubleQuotes(/*State*/ state/*char*/ next){
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
}
/*private static*/ /*State*/ foldStatement(/*State*/ state/*char*/ c){
	/*final State appended*/ = state.append(c);
	/*if (c*/ = /*= ';' && appended*/.isLevel()) return appended.advance();
	/*if (c*/ = /*= '}' && appended*/.isShallow()) return appended.advance().exit();
	/*if (c*/ = /*= '{') return appended*/.enter();
	/*if (c*/ = /*= '}') return appended*/.exit();
	/*return appended*/;
}
/*private static*/ char* compileRootSegment(char* input){
	/*final String stripped*/ = input.strip();
	/*if (stripped.startsWith("package ") || stripped.startsWith("import ")) return ""*/;
	/*return compileStructure(stripped, "class").map(Tuple::right).orElseGet(() -> wrap(stripped))*/;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileStructure(char* inputchar* type){
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
}
/*private static*/ char* compileValues(char* input/**/ Function<String/*String>*/ mapper){
	/*return compileAll(input, Main::foldValue, mapper)*/;
}
/*private static*/ char* compileParameter(char* input1){
	/*if (input1.isEmpty()) return ""*/;
	/*return generateField(input1)*/;
}
/*private static*/ char* generateField(char* input){
	/*return generateStatement(compileDefinition(input).map(Definable::generate).orElseGet(() -> wrap(input)))*/;
}
/*private static*/ char* generateStatement(char* content){
	/*return System.lineSeparator() + "\t" + content + ";"*/;
}
/*private static*/ /*State*/ foldValue(/*State*/ state/*char*/ next){
	/*if (next*/ = /*= ',' && state*/.isLevel()) return state.advance();
	/*else return state.append(next)*/;
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegment(char* inputchar* name){
	/*final String stripped*/ = input.strip();
	/*if (stripped.isEmpty()) return new Tuple<String, String>("", "")*/;
	/*return compileClassSegmentValue(stripped, name)*/;
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegmentValue(char* inputchar* name){
	/*return compileStructure(input, "class").or(() -> compileStructure(input, "record"))
																					 .or(() -> compileStructure(input, "interface"))
																					 .or(() -> compileField(input))
																					 .or(() -> compileMethod(input, name))
																					 .orElseGet(() -> new Tuple<String, String>(
																							 wrap(input) + System.lineSeparator(), ""))*/;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileMethod(char* inputchar* name){
	/*final int paramStart*/ = input.indexOf("(");
	/*if (paramStart < 0) return Optional.empty()*/;
	/*final String beforeParams*/ = input.substring(0, paramStart).strip();
	/*final String withParams*/ = input.substring(paramStart + 1);
	/*final int paramEnd*/ = withParams.indexOf(")");
	/*if (paramEnd < 0) return Optional.empty()*/;
	/*final MethodHeader methodHeader*/ = /*compileMethodHeader(beforeParams)*/;
	/*final String inputParams*/ = withParams.substring(0, paramEnd);
	/*final String withBraces*/ = withParams.substring(paramEnd + 1).strip();
	/*if (!withBraces.startsWith("{") || !withBraces.endsWith("}")) return Optional.empty()*/;
	/*final String inputBody*/ = withBraces.substring(1, withBraces.length() - 1);
	/*final String outputParams*/ = /*compileParameters(inputParams)*/;
	/*final String compiledBody*/ = /*compileStatements(inputBody, Main::compileMethodSegment)*/;
	/*String outputBody*/;
	/*if (Objects*/.requireNonNull(methodHeader) instanceof Constructor)
			outputBody = /*generateStatement(name + " this") + compiledBody + generateStatement("return this")*/;
	/*else outputBody*/ = compiledBody;
	/*final String generated*/ = methodHeader.toDefinable().generate() + "(" + outputParams + "){" + outputBody + System.lineSeparator() + "}" +
				System.lineSeparator();
	/*return Optional.of(new Tuple<String, String>("", generated))*/;
}
/*private static*/ /*MethodHeader*/ compileMethodHeader(char* beforeParams){
	/*return compileDefinition(beforeParams).<MethodHeader>map(definable -> definable)
																					.or(() -> compileConstructor(beforeParams))
																					.orElseGet(() -> new Placeholder(beforeParams))*/;
}
/*private static*/ char* compileParameters(char* input){
	/*if (input.isEmpty()) return ""*/;
	/*return compileValues(input, slice -> compileDefinition(slice).map(Definable::generate).orElse(""))*/;
}
/*private static*/ char* compileMethodSegment(char* input){
	/*final String stripped*/ = input.strip();
	/*if (stripped.isEmpty()) return ""*/;
	/*return System.lineSeparator() + "\t" + compileMethodSegmentValue(stripped)*/;
}
/*private static*/ char* compileMethodSegmentValue(char* input){
	/*if (input.endsWith(";")) {
			final String slice = input.substring(0, input.length() - 1);
			return compileMethodStatementValue(slice) + ";";
		}*/
	/*return wrap(input)*/;
}
/*private static*/ char* compileMethodStatementValue(char* input){
	/*final int i*/ = input.indexOf("=");
	/*if (i >= 0) {
			final String destination = input.substring(0, i);
			final String source = input.substring(i + 1);
			return compileExpression(destination) + " = " + compileExpression(source);
		}*/
	/*return wrap(input)*/;
}
/*private static*/ char* compileExpression(char* input){
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
}
/*private static*/ /*boolean*/ isNumber(char* input){
	/*for (int i*/ = 0;
	/*i < input.length()*/;
	/*i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) return false;
		}*/
	/*return true*/;
}
/*private static*/ /*boolean*/ isIdentifier(char* input){
	/*for (int i*/ = 0;
	/*i < input.length()*/;
	/*i++) if (!Character.isLetter(input.charAt(i))) return false*/;
	/*return true*/;
}
/*private static*/ Optional</*MethodHeader*/> compileConstructor(char* beforeParams){
	/*final int separator*/ = beforeParams.lastIndexOf(" ");
	/*if (separator < 0) return Optional.empty()*/;
	/*final String name*/ = beforeParams.substring(separator + " ".length());
	/*return Optional.of(new Constructor(name))*/;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileField(char* input){
	/*if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			return Optional.of(new Tuple<String, String>(generateField(substring), ""));
		}*/
	/*else return Optional.empty()*/;
}
/*private static*/ Optional</*Definable*/> compileDefinition(char* input){
	/*final int index*/ = input.lastIndexOf(" ");
	/*if (index < 0) return Optional.of(new Placeholder(input))*/;
	/*final String beforeName*/ = input.substring(0, index).strip();
	/*final String name*/ = input.substring(index + " ".length()).strip();
	/*final int typeSeparator*/ = beforeName.lastIndexOf(" ");
	/*if (typeSeparator < 0) return compileType(beforeName).map(type -> new Definition(type, name))*/;
	/*final String beforeType*/ = beforeName.substring(0, typeSeparator);
	/*final String typeString*/ = beforeName.substring(typeSeparator + " ".length());
	/*return compileType(typeString).map(type -> new Definition(Optional.of(beforeType), type, name))*/;
}
/*private static*/ Optional<char*> compileType(char* input){
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
}
/*private static*/ char* wrap(char* input){
	/*final String replaced*/ = input.replace("/*", "start").replace("*/", "end");
	/*return "start" + replaced + "end"*/;
}
/**/int main(){
	main_Main();
	return 0;
}