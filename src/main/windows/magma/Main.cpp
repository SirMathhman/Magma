// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
struct Main {
};
struct Definable extends JMethodHeader permits Definition, Placeholder {
};
char* generate_Definable extends JMethodHeader permits Definition, Placeholder();
/*@Override
		default*/ /*Definable*/ toDefinable_Definable extends JMethodHeader permits Definition, Placeholder(){
	return this;
}
struct JMethodHeader permits JConstructor, Definable {
};
/*Definable*/ toDefinable_JMethodHeader permits JConstructor, Definable();
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
	this.buffer = /*new StringBuilder*/();
	this.depth = 0;
	this.segments = /*new ArrayList<String>*/();
	this.index = 0;
	return this;
}
/*private*/ Stream<char*> stream_State(){
	return this.segments.stream();
}
/*private*/ /*State*/ enter_State(){
	this.depth = this.depth + 1;
	return this;
}
/*private*/ /*State*/ exit_State(){
	this.depth = /*this.depth - 1*/;
	return this;
}
/*private*/ /*boolean*/ isShallow_State(){
	return /*this.depth == 1*/;
}
/*private*/ /*boolean*/ isLevel_State(){
	return /*this.depth == 0*/;
}
/*private*/ /*State*/ append_State(/*char*/ c){
	/*this.buffer.append(c)*/;
	return this;
}
/*private*/ /*State*/ advance_State(){
	/*this.segments.add(this.buffer.toString())*/;
	this.buffer = /*new StringBuilder*/();
	return this;
}
/*public Optional<Tuple<State,*/ /*Character>>*/ pop_State(){
	if (/*this.index >= this*/.input.length()) /*(this.index >*/ = this.input.length(/*)) return Optional.empty(*/);
	/*final*/ /*char*/ next = this.input.charAt(this.index);
	/*this.index++*/;
	return Optional.of(/*new Tuple<State*/, /*Character>(this*/, /*next)*/);
}
/*public Optional<Tuple<State,*/ /*Character>>*/ popAndAppendToTuple_State(){
	return this.pop().map(/*tuple -> new Tuple<State*/, /*Character>*/(/*tuple.left.append(tuple*/.right), /*tuple.right)*/);
}
/*public*/ Optional</*State*/> popAndAppendToOption_State(){
	return this.popAndAppendToTuple().map(/*tuple -> tuple*/.left);
}
template <typeparam A, typeparam B>
struct Tuple {
	/*A*/ left;, 
	/*B*/ right;
};
struct Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable {
};
Definition new_Definition(char* type, char* name){
	Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable this;
	/*this(Optional.empty(), type, name)*/;
	return this;
}
/*@Override
		public*/ char* generate_Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable(){
	return /*this.maybeBeforeType.map(Main::wrap).map(value -> value*/ + /*" ").orElse("")*/ + this.type() + " " + this.name();
}
struct Placeholder(String input) implements Definable {
};
/*@Override
		public*/ char* generate_Placeholder(String input) implements Definable(){
	return wrap(this.input);
}
struct JConstructor(String name) implements JMethodHeader {
};
/*@Override
		public*/ /*Definable*/ toDefinable_JConstructor(String name) implements JMethodHeader(){
	return /*new Definition(this.name, "new_"*/ + /*this.name)*/;
}
/*public static*/ /*void*/ main_Main(/*String[]*/ args){
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
/*private static*/ char* compile_Main(char* input){
	/*final*/ char* joined = compileStatements(input, /*Main::compileRootSegment*/);
	return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
}
/*private static*/ char* compileStatements_Main(char* input, , /*String>*/ mapper){
	return compileAll(input, /*Main::foldStatement*/, mapper, /*""*/);
}
/*private static*/ char* compileAll_Main(char* input, , , /*State>*/ folder, , /*String>*/ mapper, char* delimiter){
	return divide(input, folder).map(mapper).collect(Collectors.joining(delimiter));
}
/*private static*/ Stream<char*> divide_Main(char* input, , , /*State>*/ folder){
	/*State*/ current = /*new State*/(input);
	/*while (true) {
			final Optional<Tuple<State, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<State, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}*/
	return current.advance().stream();
}
/*private static*/ /*State*/ foldEscaped_Main(/*State*/ state, /*char*/ next, , , /*State>*/ folder){
	return foldSingleQuotes(state, next).or(/*() -> foldDoubleQuotes(state*/, /*next)*/).orElseGet(/*() -> folder.apply(state*/, /*next)*/);
}
/*private static*/ Optional</*State*/> foldSingleQuotes_Main(/*State*/ state, /*char*/ next){
	if (/*next != '\''*/) /*(next !*/ = /*'\'') return Optional.empty()*/;
	/*final*/ /*State*/ appended = state.append(next);
	return appended.popAndAppendToTuple().flatMap(/*Main::foldEscaped*/).flatMap(/*State::popAndAppendToOption*/);
}
/*private static*/ Optional</*State*/> foldEscaped_Main(, /*Character>*/ tuple){
	if (/*tuple.right == '\\'*/) /*(tuple*/.right = /*= '\\') return tuple.left.popAndAppendToOption()*/;
	/*else return Optional.of(tuple.left)*/;
}
/*private static*/ Optional</*State*/> foldDoubleQuotes_Main(/*State*/ state, /*char*/ next){
	if (/*next != '\"'*/) /*(next !*/ = /*'\"') return Optional.empty()*/;
	/*State*/ appended = state.append(next);
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
	return Optional.of(appended);
}
/*private static*/ /*State*/ foldStatement_Main(/*State*/ state, /*char*/ c){
	/*final*/ /*State*/ appended = state.append(c);
	if (/*c == ';' && appended*/.isLevel()) /*(c*/ = /*= ';' && appended*/.isLevel(/*)) return appended.advance(*/);
	if (/*c == '}' && appended*/.isShallow()) /*(c*/ = /*= '}' && appended*/.isShallow(/*)) return appended.advance().exit(*/);
	if (/*c == '{'*/) /*(c*/ = /*= '{') return appended.enter()*/;
	if (/*c == '}'*/) /*(c*/ = /*= '}') return appended.exit()*/;
	return appended;
}
/*private static*/ char* compileRootSegment_Main(char* input){
	/*final*/ char* stripped = input.strip();
	if (/*stripped.startsWith("package ") || stripped*/.startsWith("import ")) /* (stripped.startsWith("package ") || stripped.startsWith("import ")) return ""*/;
	return compileStructure(stripped, "class").map(/*Tuple::right*/).orElseGet(/*() -> wrap*/(stripped));
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileStructure_Main(char* input, char* type){
	/*final*/ int i = /*input.indexOf(type*/ + /*" ")*/;
	if (/*i < 0*/) /* (i < 0) return Optional.empty()*/;
	/*final*/ char* afterKeyword = /*input.substring(i*/ + /*(type*/ + /*" ").length())*/;
	/*final*/ int contentStart = afterKeyword.indexOf("{");
	if (/*contentStart < 0*/) /* (contentStart < 0) return Optional.empty()*/;
	/*final*/ char* beforeContent = afterKeyword.substring(0, contentStart).strip();
	/*// if (!isIdentifier(beforeContent)) return Optional.empty()*/;
	char* beforeMaybeParams = beforeContent;
	char* recordFields = /*""*/;
	if (/*beforeContent.endsWith(")"*/) /* (beforeContent.endsWith(")")) {
			final String slice = beforeContent.substring(0, beforeContent.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter);
			}
		}*/
	char* name = beforeMaybeParams;
	List<char*> typeArguments = Collections.emptyList();
	if (beforeMaybeParams.endsWith(">")) /* (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1);
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeArguments = divide(arguments, Main::foldValue).map(String::strip).toList();
			}
		}*/
	/*final*/ char* afterContent = /*afterKeyword.substring(contentStart*/ + "{".length(/*)).strip(*/);
	if (/*!afterContent*/.endsWith("}")) /* (!afterContent.endsWith("}")) return Optional.empty()*/;
	/*final*/ char* content = afterContent.substring(0, /*afterContent.length() - "}"*/.length());
	/*final*/ List<char*> segments = divide(content, /*Main::foldStatement*/).toList();
	/*StringBuilder*/ inner = /*new StringBuilder*/();
	/*final*/ /*StringBuilder*/ outer = /*new StringBuilder*/();
	/*for (String segment : segments) {
			Tuple<String, String> compiled = compileClassSegment(segment, name);
			inner.append(compiled.left);
			outer.append(compiled.right);
		}*/
	/*String beforeStruct*/;
	if (typeArguments.isEmpty()) /*(typeArguments.isEmpty())*/ beforeStruct = /*""*/;
	/*else {
			final String templateValues =
					typeArguments.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">")) +
					System.lineSeparator();

			beforeStruct = "template " + templateValues;
		}*/
	return /*Optional.of(new Tuple<String, String>("",
																								 beforeStruct*/ + "struct " + name + " {" + recordFields + inner + System.lineSeparator() + "};" + System.lineSeparator() + /*outer))*/;
}
/*private static*/ char* compileValues_Main(char* input, , /*String>*/ mapper){
	return compileAll(input, /*Main::foldValue*/, mapper, ", ");
}
/*private static*/ char* compileParameter_Main(){
	if (/*input1*/.isEmpty()) /* (input1.isEmpty()) return ""*/;
	return generateField(/*input1*/).orElseGet(/*() -> wrap*/(/*input1*/));
}
/*private static*/ Optional<char*> generateField_Main(char* input){
	return compileDefinition(input).map(/*Definable::generate*/).map(/*Main::generateStatement*/);
}
/*private static*/ char* generateStatement_Main(char* content){
	return /*generateSegment(content*/ + /*";")*/;
}
/*private static*/ char* generateSegment_Main(char* s){
	return System.lineSeparator() + "\t" + s;
}
/*private static*/ /*State*/ foldValue_Main(/*State*/ state, /*char*/ next){
	if (/*next == ',' && state*/.isLevel()) /*(next*/ = /*= ',' && state*/.isLevel(/*)) return state.advance(*/);
	/*else return state.append(next)*/;
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegment_Main(char* input, char* name){
	/*final*/ char* stripped = input.strip();
	if (stripped.isEmpty()) /* (stripped.isEmpty()) return new Tuple<String, String>("", "")*/;
	return compileClassSegmentValue(stripped, name);
}
/*private static Tuple<String,*/ /*String>*/ compileClassSegmentValue_Main(char* input, char* name){
	if (input.isEmpty()) /* (input.isEmpty()) return new Tuple<>("", "")*/;
	/*return compileStructure(input, "class").or(() -> compileStructure(input, "record"))
																					 .or(() -> compileStructure(input, "interface"))
																					 .or(() -> compileField(input))
																					 .or(() -> compileMethod(input, name))
																					 .orElseGet(() -> {
																						 final String generated = generateSegment(wrap(input));
																						 return new Tuple<String, String>(generated, "");
																					 }*/
	/*)*/;
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileMethod_Main(char* input, char* name){
	/*final*/ int paramStart = input.indexOf("(");
	if (/*paramStart < 0*/) /* (paramStart < 0) return Optional.empty()*/;
	/*final*/ char* beforeParams = input.substring(0, paramStart).strip();
	/*final*/ char* withParams = /*input.substring(paramStart*/ + /*1)*/;
	/*final*/ int paramEnd = withParams.indexOf(")");
	if (/*paramEnd < 0*/) /* (paramEnd < 0) return Optional.empty()*/;
	/*final*/ /*JMethodHeader*/ methodHeader = compileMethodHeader(beforeParams);
	/*final*/ char* inputParams = withParams.substring(0, paramEnd);
	/*final*/ char* withBraces = /*withParams.substring(paramEnd*/ + /*1).strip()*/;
	/*final*/ char* outputParams = compileParameters(inputParams);
	/*final*/ char* outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";
	/*final String outputBodyWithBraces*/;
	if (/*withBraces.startsWith("{") && withBraces*/.endsWith("}")) /* (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);
			final String compiledBody = compileStatements(inputBody, Main::compileMethodSegment);

			String outputBody;
			if (Objects.requireNonNull(methodHeader) instanceof JConstructor)
				outputBody = generateStatement(name + " this") + compiledBody + generateStatement("return this");
			else outputBody = compiledBody;

			outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
		}*/
	/*else if*/ /*(withBraces.equals(";"))*/ outputBodyWithBraces = ";";
	/*else return Optional.empty()*/;
	/*final*/ char* generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
	return Optional.of(/*new Tuple<String*/, /*String>(""*/, /*generated)*/);
}
/*private static*/ /*Definable*/ transformMethodHeader_Main(/*JMethodHeader*/ methodHeader, char* name){
	/*return switch (methodHeader) {
			case JConstructor constructor -> new Definition(constructor.name, "new_" + constructor.name);
			case Definition definition ->
					new Definition(definition.maybeBeforeType, definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		}*/
	/**/;
}
/*private static*/ /*JMethodHeader*/ compileMethodHeader_Main(char* beforeParams){
	return /*compileDefinition(beforeParams).<JMethodHeader>map*/(/*definable -> definable*/).or(/*() -> compileConstructor*/(beforeParams)).orElseGet(/*() -> new Placeholder*/(beforeParams));
}
/*private static*/ char* compileParameters_Main(char* input){
	if (input.isEmpty()) /* (input.isEmpty()) return ""*/;
	return compileValues(input, /*slice -> compileDefinition*/(slice).map(/*Definable::generate*/).orElse(/*""*/));
}
/*private static*/ char* compileMethodSegment_Main(char* input){
	/*final*/ char* stripped = input.strip();
	if (stripped.isEmpty()) /* (stripped.isEmpty()) return ""*/;
	return generateSegment(compileMethodSegmentValue(stripped));
}
/*private static*/ char* compileMethodSegmentValue_Main(char* input){
	if (input.startsWith("if")) /* (input.startsWith("if")) {
			final String withoutPrefix = input.substring(2);
			int conditionEnd = -1;
			int depth = 0;
			for (int i = 0; i < withoutPrefix.length(); i++) {
				final char c = withoutPrefix.charAt(i);
				if (c == ')') {
					depth--;
					if (depth == 0) {
						conditionEnd = i;
						break;
					}
				}
				if (c == '(') depth++;
			}

			if (conditionEnd >= 0) {
				final String substring1 = withoutPrefix.substring(0, conditionEnd).strip();
				final String body = withoutPrefix;
				if (substring1.startsWith("(")) {
					final String expression = substring1.substring(1);
					final String condition = compileExpression(expression);
					final String compiledBody = compileMethodSegmentValue(body);
					return "if (" + condition + ") " + compiledBody;
				}
			}
		}*/
	if (input.endsWith(";")) /* (input.endsWith(";")) {
			final String slice = input.substring(0, input.length() - 1);
			return compileMethodStatementValue(slice) + ";";
		}*/
	return wrap(input);
}
/*private static*/ char* compileMethodStatementValue_Main(char* input){
	if (input.startsWith("return ")) /* (input.startsWith("return ")) return "return " + compileExpression(input.substring("return ".length()))*/;
	/*final*/ int i = input.indexOf("=");
	if (/*i >= 0*/) /* (i >= 0) {
			final String destinationString = input.substring(0, i);
			final String source = input.substring(i + 1);
			final String destination = compileDefinition(destinationString).map(Definition::generate)
																																		 .orElseGet(() -> compileExpression(
																																				 destinationString));

			return destination + " = " + compileExpression(source);
		}*/
	return wrap(input);
}
/*private static*/ char* compileExpression_Main(char* input){
	/*final*/ char* stripped = input.strip();
	if (isString(stripped)) /* (isString(stripped)) return stripped*/;
	/*final int i1*/ = /*stripped.indexOf("*/ + /*")*/;
	if (/*i1 >= 0*/) /* (i1 >= 0) {
			final String left = stripped.substring(0, i1);
			final String right = stripped.substring(i1 + "+".length());
			return compileExpression(left) + " + " + compileExpression(right);
		}*/
	if (/*stripped.endsWith(")"*/) /* (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - 1);

			int index = -1;
			int depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final char c = slice.charAt(i);
				if (c == '(') {
					depth++;
					if (depth == 1) index = i;
				}
				if (c == ')') depth--;
			}

			if (index >= 0) {
				final String caller = slice.substring(0, index);
				final String arguments = slice.substring(index + 1);
				return compileExpression(caller) + "(" + compileValues(arguments, Main::compileExpression) + ")";
			}
		}*/
	/*final*/ int i = stripped.lastIndexOf(".");
	if (/*i >= 0*/) /* (i >= 0) {
			final String substring = stripped.substring(0, i);
			final String name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) return compileExpression(substring) + "." + name;
		}*/
	if (isIdentifier(stripped)) /* (isIdentifier(stripped)) return stripped*/;
	if (isNumber(stripped)) /* (isNumber(stripped)) return stripped*/;
	return wrap(stripped);
}
/*private static*/ /*boolean*/ isString_Main(char* stripped){
	if (/*stripped.length() <= 2*/) /*(stripped.length() <*/ = /*2) return false*/;
	/*final*/ /*boolean*/ hasDoubleQuotes = /*stripped.startsWith("\"") && stripped*/.endsWith(/*"\""*/);
	if (/*!hasDoubleQuotes*/) /* (!hasDoubleQuotes) return false*/;
	return /*!stripped*/.substring(1, /*stripped.length() - 1*/).contains(/*"\""*/);
}
/*private static*/ /*boolean*/ isNumber_Main(char* input){
	/*for*/ /*(int*/ i = 0;
	/*i < input.length()*/;
	/*i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) return false;
		}*/
	return true;
}
/*private static*/ /*boolean*/ isIdentifier_Main(char* input){
	/*for*/ /*(int*/ i = 0;
	/*i < input.length()*/;
	/*i++) if (!Character.isLetter(input.charAt(i))) return false*/;
	return true;
}
/*private static*/ Optional</*JMethodHeader*/> compileConstructor_Main(char* beforeParams){
	/*final*/ int separator = beforeParams.lastIndexOf(" ");
	if (/*separator < 0*/) /* (separator < 0) return Optional.empty()*/;
	/*final*/ char* name = /*beforeParams.substring(separator*/ + " ".length(/*)*/);
	return Optional.of(/*new JConstructor*/(name));
}
/*private static Optional<Tuple<String,*/ /*String>>*/ compileField_Main(char* input){
	if (input.endsWith(";")) /* (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			final Optional<String> s = generateField(substring);
			if (s.isPresent()) return Optional.of(new Tuple<String, String>(s.get(), ""));
		}*/
	return Optional.empty();
}
/*private static*/ Optional</*Definition*/> compileDefinition_Main(char* input){
	/*final*/ char* stripped = input.strip();
	/*final*/ int index = stripped.lastIndexOf(" ");
	if (/*index < 0*/) /* (index < 0) return Optional.empty()*/;
	/*final*/ char* beforeName = stripped.substring(0, index).strip();
	/*final*/ char* name = /*stripped.substring(index*/ + " ".length(/*)).strip(*/);
	if (/*!isIdentifier*/(name)) /* (!isIdentifier(name)) return Optional.empty()*/;
	/*final*/ int typeSeparator = beforeName.lastIndexOf(" ");
	if (/*typeSeparator < 0*/) /* (typeSeparator < 0) return compileType(beforeName).map(type -> new Definition(type, name))*/;
	/*final*/ char* beforeType = beforeName.substring(0, typeSeparator);
	/*final*/ char* typeString = /*beforeName.substring(typeSeparator*/ + " ".length(/*)*/);
	return compileType(typeString).map(/*type -> new Definition*/(/*Optional.of(beforeType*/), type, /*name)*/);
}
/*private static*/ Optional<char*> compileType_Main(char* input){
	/*final*/ char* stripped = input.strip();
	if (stripped.equals("public")) /* (stripped.equals("public")) return Optional.empty()*/;
	if (stripped.endsWith(">")) /* (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String arguments = withoutEnd.substring(argumentStart + "<".length());
				return Optional.of(base + "<" + compileType(arguments).orElse("") + ">");
			}
		}*/
	if (stripped.equals("String")) /* (stripped.equals("String")) return Optional.of("char*")*/;
	if (stripped.equals("int")) /* (stripped.equals("int")) return Optional.of("int")*/;
	return Optional.of(wrap(stripped));
}
/*private static*/ char* wrap_Main(char* input){
	/*final*/ char* replaced = input.replace("/*", "start").replace("*/", "end");
	return "/*" + replaced + "*/";
}
/**/int main(){
	main_Main();
	return 0;
}