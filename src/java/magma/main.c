struct IOError {
};
struct Path_ {
	struct Path_ resolveSibling(struct String_ sibling);
	List__struct String listNames();
};
struct Error {
	struct String_ display();
};
struct String_ {
};
struct State {
	List__struct Character queue;
	List__struct String segments;
	struct StringBuilder buffer;
	int depth;
};
struct Joiner {
};
struct RangeHead {
	int length;
};
struct Iterators {
};
struct Max {
};
struct CompileError {
};
struct ApplicationError {
};
struct OrState {
};
struct Main {
	<T> {
        <R> Option_struct R map(Function_struct T, struct R mapper);
	<T> {
        <R> struct R fold(struct R initial, BiFunction_struct R, struct T, struct R folder);
	<T, X> {
        <R> struct R match(Function_struct T, struct R whenOk, Function_struct X, struct R whenErr);
};
int counter = 0;
List__struct String imports = Impl.emptyList();
List__struct String structs = Impl.emptyList();
List__struct String globals = Impl.emptyList();
List__struct String methods = Impl.emptyList();
int counter = 0;
struct private State(List__struct Character queue, List__struct String segments, struct StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
struct public State(List__struct Character queue) {
	this(queue, Impl.emptyList(), struct StringBuilder(), 0);
}
struct State advance() {
	this.segments.add(this.buffer.toString());
	this.buffer = struct StringBuilder();
	return this;
}
struct State append(char c) {
	this.buffer.append(c);
	return this;
}
struct boolean isLevel() {
	return this.depth == 0;
}
char pop() {
	return this.queue.pop();
}
struct boolean hasElements() {
	return !this.queue.isEmpty();
}
struct State exit() {
	this.depth = this.depth - 1;
	return this;
}
struct State enter() {
	this.depth = this.depth + 1;
	return this;
}
List__struct String segments() {
	return this.segments;
}
char peek() {
	return this.queue.peek();
}
<T> implements Option<T> {
        @Override
        public <R> Option_struct R map(Function_struct T, struct R mapper) {
	return None_();/* 
        }

        @Override
        public T orElse(T other) {
            return other; *//* 
        }

        @Override
        public boolean isPresent() {
            return false; *//* 
        }

        @Override
        public boolean isEmpty() {
            return true; */
	/* }

        @Override
        public void ifPresent(Consumer */ < /* T> consumer) {
        }

        @Override
        public Option */ < /* T> or(Supplier */ < Option < /* T>> supplier) {
            return supplier */.get();
	/* }

        @Override
        public  */ < /* R> Option */ < /* R> flatMap(Function */ < /* T, Option */ < /* R>> mapper) {
            return new None */ < /* > */();
	/* }

        @Override
        public T orElseGet(Supplier */ < /* T> other) {
            return other */.get();/* 
        }
     */
}
Option_struct String createInitial() {
	return None_();
}
int __lambda0__(int inner) {
	return inner + this.delimiter + element;
}
Option_struct String fold(Option_struct String current, struct String element) {
	return Some_(current.map(__lambda0__).orElse(element));
}
struct public RangeHead(int length) {
	this.length = length;
}
Option_struct Integer next() {
	if (this.counter >= this.length) {
		return None_();
	}
	int value = this.counter;this.counter++;
	return Some_(value);
}
Option_struct T next() {
	return None_();/* 
        }
     */
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
}
int __lambda1__() {
	return struct Tuple.right()
}
Iterator_struct Character fromString(struct String string) {
	return fromStringWithIndices(string).map(__lambda1__);
}
Iterator_Tuple_struct Integer, struct Character fromStringWithIndices(struct String string) {
	return HeadedIterator_(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
int __lambda2__() {
	return struct Tuple.right()
}
int __lambda3__(int index) {
	return Tuple_(index, string.charAt(index));
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
	/* }

        public static Iterator */ < /* Character> fromString(String string) {
            return fromStringWithIndices */(string).map(__lambda2__);
	/* }

        public static Iterator */ < Tuple < /* Integer, Character>> fromStringWithIndices(String string) {
            return new HeadedIterator */ < /* > */(struct RangeHead(string.length())).map(__lambda3__);/* 
        }
     */
}
List__struct T createInitial() {
	return Impl.emptyList();
	/* }

        @Override
        public List_ */ < /* T> fold(List_ */ < /* T> current, T element) {
            return current */.add(element);/* 
        }
     */
}
Option_struct Integer createInitial() {
	return None_();
}
int __lambda4__(int inner) {
	return /* inner > element ? inner : element */;
}
Option_struct Integer fold(Option_struct Integer current, struct Integer element) {
	return Some_(current.map(__lambda4__).orElse(element));
}
int __lambda5__(int inner) {
	return /* inner > element ? inner : element */;
}
Option_struct Integer createInitial() {
	return None_();
	/* }

        @Override
        public Option */ < /* Integer> fold(Option */ < /* Integer> current, Integer element) {
            return new Some */ < /* > */(current.map(__lambda5__).orElse(element));/* 
        }
     */
}
struct public CompileError(struct String message, struct String context) {
	this(message, context, Impl.emptyList());
}
struct String display0() {
	return this.format(0);
}
int __lambda6__(int first, int second) {
	return first.computeMaxDepth() - second.computeMaxDepth();
}
int __lambda7__(int compileError) {
	return compileError.format(depth + 1);
}
int __lambda8__(int display) {
	return "\n" + "\t".repeat(depth + 1) + display;
}
struct String format(int depth) {
	struct String joined = this.errors.sort(__lambda6__).iter().map(__lambda7__).map(__lambda8__).collect(struct Joiner("")).orElse("");
	return this.message + ": " + this.context + joined;
}
int __lambda9__() {
	return struct CompileError.computeMaxDepth()
}
int computeMaxDepth() {
	return 1 + this.errors.iter().map(__lambda9__).collect(struct Max()).orElse(0);
}
struct String_ display() {
	return struct String_(this.display0());
}
struct String display0() {
	return this.error.display().value;
}
struct String_ display() {
	return struct String_(this.display0());
}
struct public OrState() {
	this(None_(), Impl.emptyList());
}
struct OrState withValue(struct String value) {
	return struct OrState(Some_(value), this.errors);
}
struct OrState withError(struct CompileError error) {
	return struct OrState(this.maybeValue, this.errors.add(error));
}
int __lambda10__() {
	return struct Ok.new()
}
int __lambda11__() {
	return Err_(this.errors);
}
Result_struct String, List__struct CompileError toResult() {
	return this.maybeValue.<Result<String, List_<CompileError>>>map(__lambda10__).orElseGet(__lambda11__);
}
int __lambda12__() {
	return struct ApplicationError.new()
}
int __lambda13__(int input) {
	return compileAndWrite(input, source);
}
int __lambda14__() {
	return struct Some.new()
}
int __lambda15__(int error) {
	return System.err.println(error.display().value);
}
struct void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).mapErr(__lambda12__).match(__lambda13__, __lambda14__).ifPresent(__lambda15__);
}
int __lambda16__() {
	return struct ApplicationError.new()
}
int __lambda17__() {
	return struct ApplicationError.new()
}
int __lambda18__(int output) {
	return Impl.writeString(target, output).map(__lambda17__);
}
int __lambda19__() {
	return struct Some.new()
}
Option_struct ApplicationError compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling(struct String_("main.c"));
	return compile(input).mapErr(__lambda16__).match(__lambda18__, __lambda19__);
}
int __lambda20__() {
	return struct Main.divideStatementChar()
}
int __lambda21__() {
	return struct Main.assembleChildren()
}
int __lambda22__() {
	return struct Main.mergeStatements()
}
int __lambda23__(int compiled) {
	return mergeAll(compiled, __lambda22__);
}
Result_struct String, struct CompileError compile(struct String input) {
	return parseAll(divide(input, __lambda20__), createRootSegmentCompiler()).mapValue(__lambda21__).mapValue(__lambda23__);
}
List__struct String assembleChildren(List__struct String rootChildren) {
	return Impl.<String>emptyList().addAll(imports).addAll(structs).addAll(globals).addAll(methods).addAll(rootChildren);
}
int __lambda24__() {
	return struct Main.compilePackage()
}
int __lambda25__() {
	return struct Main.compileImport()
}
Function_struct String, Result_struct String, struct CompileError createRootSegmentCompiler() {
	return createOrRule(Impl.listOf(createTypeRule("whitespace", createWhitespaceRule()), createTypeRule("package", wrap(__lambda24__)), createTypeRule("import", wrap(__lambda25__)), createClassRule(Impl.emptyList())));
}
int __lambda26__(int err) {
	struct String format = "Cannot assign type to '%s'";
	struct String message = format.formatted(type);
	return struct CompileError(message, input, Impl.listOf(err));
}
int __lambda27__(int input) {
	return childRule.apply(input).mapErr(__lambda26__);
}
Function_struct String, Result_struct String, struct CompileError createTypeRule(struct String type, Function_struct String, Result_struct String, struct CompileError childRule) {
	return __lambda27__;
}
Function_struct String, Result_struct String, struct CompileError createWhitespaceRule() {/* 
        return input -> {
            if (input.isBlank()) {
                return new Ok<>("");
            }
            return new Err<>(new CompileError("Not blank", input));
        } *//* ; */
}
int __lambda28__() {
	return struct state.withValue()
}
int __lambda29__() {
	return struct state.withError()
}
int __lambda30__(int state, int rule) {
	return rule.apply(input).match(__lambda28__, __lambda29__);
}
int __lambda31__(int children) {
	return struct CompileError("No valid combination", input, children);
}
int __lambda32__(int input) {
	return aClass.iter().fold(struct OrState(), __lambda30__).toResult().mapErr(__lambda31__);
}
Function_struct String, Result_struct String, struct CompileError createOrRule(List__Function_struct String, Result_struct String, struct CompileError aClass) {
	return __lambda32__;
}
int __lambda33__() {
	return struct String.equals()
}
Option_struct String compileImport(struct String input) {
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			List__struct String split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Impl.equalsList(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda33__)) {
				return Some_("");
			}
			struct String joined = split.iter().collect(struct Joiner("/")).orElse("");
			imports.add("#include \"./" + joined + "\"\n");
			return Some_("");
		}
	}
	return None_();
}
Option_struct String compilePackage(struct String input) {
	if (input.startsWith("package ")) {
		return Some_("");
	}
	return None_();
}
int __lambda34__() {
	return struct Main.divideStatementChar()
}
int __lambda35__() {
	return struct Main.mergeStatements()
}
Result_struct String, struct CompileError compileStatements(struct String input, Function_struct String, Result_struct String, struct CompileError compiler) {
	return compileAndMerge(divide(input, __lambda34__), compiler, __lambda35__);
}
int __lambda36__(int compiled) {
	return mergeAll(compiled, merger);
}
Result_struct String, struct CompileError compileAndMerge(List__struct String segments, Function_struct String, Result_struct String, struct CompileError compiler, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return parseAll(segments, compiler).mapValue(__lambda36__);
}
struct String mergeAll(List__struct String compiled, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
int __lambda37__() {
	return struct allCompiled.add()
}
int __lambda38__(int allCompiled) {
	return compiler.apply(segment).mapValue(__lambda37__);
}
int __lambda39__(int maybeCompiled, int segment) {
	return maybeCompiled.flatMapValue(__lambda38__);
}
Result_List__struct String, struct CompileError parseAll(List__struct String segments, Function_struct String, Result_struct String, struct CompileError compiler) {
	return segments.iter().<Result<List_<String>, CompileError>>fold(Ok_(Impl.emptyList()), __lambda39__);
}
int __lambda40__() {
	return struct Ok.new()
}
int __lambda41__() {
	return Err_(struct CompileError("Invalid value", input));
}
int __lambda42__(int input) {
	return compiler.apply(input).<Result<String, CompileError>>map(__lambda40__).orElseGet(__lambda41__);
}
Function_struct String, Result_struct String, struct CompileError wrap(Function_struct String, Option_struct String compiler) {
	return __lambda42__;
}
struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return output.append(compiled);
}
List__struct String divide(struct String input, BiFunction_struct State, struct Character, struct State divider) {
	List__struct Character queue = Iterators.fromString(input).collect(ListCollector_());
	struct State state = struct State(queue);
	while (state.hasElements()) {
		char c = state.pop();
		if (c == '\'') {
			state.append(c);
			char maybeSlash = state.pop();
			state.append(maybeSlash);
			if (maybeSlash == '\\') {
				state.append(state.pop());
			}
			state.append(state.pop());
			continue;
		}
		if (c == '\"') {
			state.append(c);
			while (state.hasElements()) {
				char next = state.pop();
				state.append(next);
				if (next == '\\') {
					state.append(state.pop());
				}
				if (next == '"') {
					break;
				}
			}
			continue;
		}
		state = divider.apply(state, c);
	}
	return state.advance().segments();
}
struct State divideStatementChar(struct State state, char c) {
	struct State appended = state.append(c);
	if (c == ';' && appended.isLevel()) {
		return appended.advance();
	}
	if (c == '}' && isShallow(appended)) {
		return appended.advance().exit();
	}
	if (c == '{' || c == '(') {
		return appended.enter();
	}
	if (c == '}' || c == ')') {
		return appended.exit();
	}
	return appended;
}
struct boolean isShallow(struct State state) {
	return state.depth == 1;
}
List__struct String splitByDelimiter(struct String content, char delimiter) {
	List__struct String segments = Impl.emptyList();
	struct StringBuilder buffer = struct StringBuilder();/* 
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == delimiter) {
                segments = segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else {
                buffer.append(c);
            }
        } */
	return segments.add(buffer.toString());
}
int __lambda43__(int input1) {
	return createClassMemberRule(typeParams).apply(input1);
}
Function_struct String, Result_struct String, struct CompileError createCompileToStructRule(struct String type, struct String infix, List__struct String typeParams) {
	return /* createTypeRule(type, input -> {
            int classIndex = input */.indexOf(infix);
            if (classIndex < 0) {
                return createInfixErr(input, infix);
            }

            String afterKeyword = input.substring(classIndex + infix.length());
            int contentStart = afterKeyword.indexOf("{");
            if (contentStart < 0) {
                return createInfixErr(afterKeyword, "{");
            }

            String beforeContent = afterKeyword.substring(0, contentStart).strip();

            int implementsIndex = beforeContent.indexOf(" implements ");
            String withoutImplements = implementsIndex >= 0
                    ? beforeContent.substring(0, implementsIndex)
                    : beforeContent;

            int extendsIndex = withoutImplements.indexOf(" extends ");
            String withoutExtends = extendsIndex >= 0
                    ? withoutImplements.substring(0, extendsIndex)
                    : withoutImplements;

            int paramStart = withoutExtends.indexOf("(");
            String withoutParams = paramStart >= 0
                    ? withoutExtends.substring(0, paramStart).strip()
                    : withoutExtends;

            int typeParamsStart = withoutParams.indexOf("<");
            if (typeParamsStart >= 0) {
                return new Ok<>("");
            }

            if (!isSymbol(withoutParams)) {
                return new Err<>(new CompileError("Not a symbol", withoutParams));
            }

            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (!withEnd.endsWith("}")) {
                return new Err<>(new CompileError("Suffix '}' not present", withEnd));
            }

            String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
            return compileStatements(inputContent, __lambda43__).mapValue(outputContent -> {
                structs.add("struct " + withoutParams + " {" + outputContent + "\n};\n");
                return "";
            });
        });
}
Err_struct String, struct CompileError createInfixErr(struct String input, struct String infix) {
	return Err_(struct CompileError("Infix '" + infix + "' not present", input));
}
int __lambda44__(int input) {
	return compileGlobalInitialization(input, typeParams);
}
int __lambda45__() {
	return struct Main.compileDefinitionStatement()
}
Function_struct String, Result_struct String, struct CompileError createClassMemberRule(List__struct String typeParams) {
	return createOrRule(Impl.listOf(createWhitespaceRule(), createCompileToStructRule("interface", "interface ", typeParams), createCompileToStructRule("record", "record ", typeParams), createClassRule(typeParams), wrap(__lambda44__), wrap(__lambda45__), createMethodRule(typeParams)));
}
Function_struct String, Result_struct String, struct CompileError createMethodRule(List__struct String typeParams) {/* 
        return input -> {
            int paramStart = input.indexOf("(");
            if (paramStart < 0) {
                return createInfixErr(input, "(");
            }

            String inputDefinition = input.substring(0, paramStart).strip();
            String withParams = input.substring(paramStart + "(".length());

            return createDefinitionRule().apply(inputDefinition).flatMapValue(outputDefinition -> {
                int paramEnd = withParams.indexOf(")");
                if (paramEnd < 0) {
                    return createInfixErr(withParams, ")");
                }

                String params = withParams.substring(0, paramEnd);
                return compileValues(params, createParameterRule()).flatMapValue(outputParams -> {
                    String substring = withParams.substring(paramEnd + ")".length());
                    return assembleMethodBody(typeParams, outputDefinition, outputParams, substring.strip());
                });
            });
        } *//* ; */
}
Function_struct String, Result_struct String, struct CompileError createClassRule(List__struct String typeParams) {
	return createCompileToStructRule("class", "class ", typeParams);
}
int __lambda46__(int result) {
	return "\n\t" + result + ";";
}
Option_struct String compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return createDefinitionRule().apply(content).findValue().map(__lambda46__);
	}
	return None_();
}
int __lambda47__(int generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_struct String compileGlobalInitialization(struct String input, List__struct String typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda47__);
}
int __lambda48__(int outputValue) {
	return outputDefinition + " = " + outputValue;
}
int __lambda49__(int outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda48__);
}
Option_struct String compileInitialization(struct String input, List__struct String typeParams, int depth) {
	if (!input.endsWith(";")) {
		return None_();
	}
	struct String withoutEnd = input.substring(0, input.length() - ";".length());
	int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return None_();
	}
	struct String definition = withoutEnd.substring(0, valueSeparator).strip();
	struct String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return createDefinitionRule().apply(definition).findValue().flatMap(__lambda49__);
}
Option_struct String compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
int __lambda50__(int input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
int __lambda51__(int outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Ok_("");
}
Result_struct String, struct CompileError assembleMethodBody(List__struct String typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, wrap(__lambda50__)).flatMapValue(__lambda51__);
	}
	return Ok_("\n\t" + header + ";");
}
int __lambda52__() {
	return struct Main.compileWhitespace()
}
Function_struct String, Result_struct String, struct CompileError createParameterRule() {
	return createOrRule(Impl.listOf(wrap(__lambda52__), createDefinitionRule()));
}
int __lambda53__() {
	return struct Main.divideValueChar()
}
Result_struct String, struct CompileError compileValues(struct String input, Function_struct String, Result_struct String, struct CompileError compiler) {
	return compileValues(divide(input, __lambda53__), compiler);
}
struct State divideValueChar(struct State state, char c) {
	if (c == '-') {
		if (state.peek() == '>') {
			state.pop();
			return state.append('-').append('>');
		}
	}
	if (c == ',' && state.isLevel()) {
		return state.advance();
	}
	struct State appended = state.append(c);
	if (c == ' < ' || c == '(') {
		return appended.enter();
	}
	if (c == '>' || c == ')') {
		return appended.exit();
	}
	return appended;
}
int __lambda54__() {
	return struct Main.mergeValues()
}
Result_struct String, struct CompileError compileValues(List__struct String params, Function_struct String, Result_struct String, struct CompileError compiler) {
	return compileAndMerge(params, compiler, __lambda54__);
}
int __lambda55__() {
	return compileKeywordStatement(input, depth, "continue");
}
int __lambda56__() {
	return compileKeywordStatement(input, depth, "break");
}
int __lambda57__() {
	return compileConditional(input, typeParams, "if ", depth);
}
int __lambda58__() {
	return compileConditional(input, typeParams, "while ", depth);
}
int __lambda59__() {
	return compileElse(input, typeParams, depth);
}
int __lambda60__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
int __lambda61__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
int __lambda62__(int result) {
	return formatStatement(depth, result);
}
int __lambda63__() {
	return compileReturn(input, typeParams, depth).map(__lambda62__);
}
int __lambda64__(int result) {
	return formatStatement(depth, result);
}
int __lambda65__() {
	return compileInitialization(input, typeParams, depth).map(__lambda64__);
}
int __lambda66__(int result) {
	return formatStatement(depth, result);
}
int __lambda67__() {
	return compileAssignment(input, typeParams, depth).map(__lambda66__);
}
int __lambda68__(int result) {
	return formatStatement(depth, result);
}
int __lambda69__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda68__);
}
int __lambda70__() {
	return compileDefinitionStatement(input);
}
int __lambda71__() {
	return generatePlaceholder(input);
}
Option_struct String compileStatementOrBlock(struct String input, List__struct String typeParams, int depth) {
	return compileWhitespace(input).or(__lambda55__).or(__lambda56__).or(__lambda57__).or(__lambda58__).or(__lambda59__).or(__lambda60__).or(__lambda61__).or(__lambda63__).or(__lambda65__).or(__lambda67__).or(__lambda69__).or(__lambda70__).or(__lambda71__);
}
int __lambda72__(int value) {
	return value + operator + ";";
}
Option_struct String compilePostOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda72__);
	}
	else {
		return None_();
	}
}
int __lambda73__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
int __lambda74__(int result) {
	return indent + "else {" + result + indent + "}";
}
int __lambda75__(int result) {
	return "else " + result;
}
Option_struct String compileElse(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrap(__lambda73__)).findValue().map(__lambda74__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda75__);
		}
	}
	return None_();
}
Option_struct String compileKeywordStatement(struct String input, int depth, struct String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Some_(formatStatement(depth, keyword));
	}
	else {
		return None_();
	}
}
struct String formatStatement(int depth, struct String value) {
	return createIndent(depth) + value + ";";
}
struct String createIndent(int depth) {
	return "\n" + "\t".repeat(depth);
}
int __lambda76__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
int __lambda77__(int statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
int __lambda78__(int result) {
		return withCondition + " " + result;
}
int __lambda79__(int newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, wrap(__lambda76__)).findValue().map(__lambda77__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda78__);
	}
}
Option_struct String compileConditional(struct String input, List__struct String typeParams, struct String prefix, int depth) {
	struct String stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return None_();
	}
	struct String afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return None_();
	}
	struct String withoutConditionStart = afterKeyword.substring(1);
	int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return None_();
	}
	struct String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	struct String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda79__);
}
int findConditionEnd(struct String input) {
	int conditionEnd = -1;
	int depth0 = 0;
	List__Tuple_struct Integer, struct Character queue = Iterators.fromStringWithIndices(input).collect(ListCollector_());
	while (!queue.isEmpty()) {
		Tuple_struct Integer, struct Character pair = queue.pop();
		struct Integer i = pair.left;
		struct Character c = pair.right;
		if (c == '\'') {
			if (queue.pop().right == '\\') {
				queue.pop();
			}
			queue.pop();
			continue;
		}
		if (c == '"') {
			while (!queue.isEmpty()) {
				Tuple_struct Integer, struct Character next = queue.pop();
				if (next.right == '\\') {
					queue.pop();
				}
				if (next.right == '"') {
					break;
				}
			}
			continue;
		}
		if (c == ')' && depth0 == 0) {
			conditionEnd = i;
			break;
		}
		if (c == '(') {depth0++;
		}
		if (c == ')') {depth0--;
		}
	}
	return conditionEnd;
}
Option_struct String compileInvocationStatement(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		Option_struct String maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
		if (maybeInvocation.isPresent()) {
			return maybeInvocation;
		}
	}
	return None_();
}
int __lambda80__(int newSource) {
			return newDest + " = " + newSource;
}
int __lambda81__(int newDest) {
			return compileValue(source, typeParams, depth).map(__lambda80__);
}
Option_struct String compileAssignment(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda81__);
		}
	}
	return None_();
}
int __lambda82__(int result) {
	return "return " + result;
}
Option_struct String compileReturn(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda82__);
		}
	}
	return None_();
}
int __lambda83__(int value) {
	return outputType + value;
}
int __lambda84__(int outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda83__);
}
int __lambda85__(int result) {
	return "!" + result;
}
int __lambda86__(int compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
int __lambda87__(int compiled) {
	return compiled + "." + property;
}
int __lambda88__() {
	return compileOperator(input, typeParams, depth, "<");
}
int __lambda89__() {
	return compileOperator(input, typeParams, depth, "+");
}
int __lambda90__() {
	return compileOperator(input, typeParams, depth, ">=");
}
int __lambda91__() {
	return compileOperator(input, typeParams, depth, "&&");
}
int __lambda92__() {
	return compileOperator(input, typeParams, depth, "==");
}
int __lambda93__() {
	return compileOperator(input, typeParams, depth, "!=");
}
int __lambda94__() {
	return generatePlaceholder(input);
}
Option_struct String compileValue(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
		return Some_(stripped);
	}
	if (stripped.startsWith("'") && stripped.endsWith("'")) {
		return Some_(stripped);
	}
	if (isSymbol(stripped) || isNumber(stripped)) {
		return Some_(stripped);
	}
	if (stripped.startsWith("new ")) {
		struct String slice = stripped.substring("new ".length());
		int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			struct String type = slice.substring(0, argsStart);
			struct String withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				struct String argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return createTypeRule(typeParams).apply(type).findValue().flatMap(__lambda84__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda85__);
	}
	Option_struct String value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Option_struct String invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		struct String type = stripped.substring(0, methodIndex).strip();
		struct String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return createTypeRule(typeParams).apply(type).findValue().flatMap(__lambda86__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda87__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda88__).or(__lambda89__).or(__lambda90__).or(__lambda91__).or(__lambda92__).or(__lambda93__).or(__lambda94__);
}
int __lambda95__(int rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
int __lambda96__(int leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda95__);
}
Option_struct String compileOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda96__);
}
int __lambda97__() {
	return struct String.strip()
}
int __lambda98__(int value) {
	return !value.isEmpty();
}
int __lambda99__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
int __lambda100__(int result) {
		return generateLambdaWithReturn(paramNames, result);
}
int __lambda101__(int newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option_struct String compileLambda(struct String input, List__struct String typeParams, int depth) {
	int arrowIndex = input.indexOf("->");
	if (arrowIndex < 0) {
		return None_();
	}
	struct String beforeArrow = input.substring(0, arrowIndex).strip();
	List__struct String paramNames;
	if (isSymbol(beforeArrow)) {
		paramNames = Impl.listOf(beforeArrow);
	}else 
	if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
		struct String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda97__).filter(__lambda98__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, wrap(__lambda99__)).findValue().flatMap(__lambda100__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda101__);
}
int __lambda102__(int name) {
	return "int " + name;
}
Option_struct String generateLambdaWithReturn(List__struct String paramNames, struct String returnValue) {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda102__).collect(struct Joiner(", ")).orElse("");
	methods.add("int " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
int __lambda103__(int tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
struct boolean isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda103__);
}
int __lambda104__(int value) {
	return caller + value;
}
int __lambda105__(int caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda104__);
}
Option_struct String compileInvocation(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda105__);
		}
	}
	return None_();
}
int findInvocationStart(struct String sliced) {
	int argsStart = -1;
	int depth0 = 0;
	int i = sliced.length() - 1;
	while (i >= 0) {
		char c = sliced.charAt(i);
		if (c == '(' && depth0 == 0) {
			argsStart = i;
			break;
		}
		if (c == ')') {depth0++;
		}
		if (c == '(') {depth0--;
		}i--;
	}
	return argsStart;
}
int __lambda106__() {
	return compileValue(arg, typeParams, depth);
}
int __lambda107__(int arg) {
	return compileWhitespace(arg).or(__lambda106__);
}
int __lambda108__(int args) {
	return "(" + args + ")";
}
Option_struct String compileArgs(struct String argsString, List__struct String typeParams, int depth) {
	return compileValues(argsString, wrap(__lambda107__)).findValue().map(__lambda108__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
int __lambda109__(int input) {
	return /* {
            String stripped = input */.strip();
            int nameSeparator = stripped.lastIndexOf(" ");
            if (nameSeparator < 0) {
                return createInfixErr(stripped, " ");
            }

            String beforeName = stripped.substring(0, nameSeparator).strip();
            String name = stripped.substring(nameSeparator + " ".length()).strip();
            if (!isSymbol(name)) {
                return new Err<>(new CompileError("Not a symbol", name));
            }

            int typeSeparator = findTypeSeparator(beforeName);
            if (typeSeparator >= 0) {
                String beforeType = beforeName.substring(0, typeSeparator).strip();

                String beforeTypeParams = beforeType;
                List_<String> typeParams;
                if (beforeType.endsWith(">")) {
                    String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                    int typeParamStart = withoutEnd.indexOf("<");
                    if (typeParamStart >= 0) {
                        beforeTypeParams = withoutEnd.substring(0;
}
int __lambda110__() {
	return struct String.strip()
}
int __lambda111__(int value) {
	return !value.isEmpty();
}
Function_struct String, Result_struct String, struct CompileError createDefinitionRule() {
	return createTypeRule("definition", __lambda109__, /* typeParamStart);
                        String substring = withoutEnd */.substring(typeParamStart + 1);
                        typeParams = splitValues(substring);
                    }
                    else {
                        typeParams = Impl.emptyList();
                    }
                }
                else {
                    typeParams = Impl.emptyList();
                }

                String strippedBeforeTypeParams = beforeTypeParams.strip();

                String modifiersString;
                int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
                if (annotationSeparator >= 0) {
                    modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
                }
                else {
                    modifiersString = strippedBeforeTypeParams;
                }

                boolean allSymbols = splitByDelimiter(modifiersString, ' ').iter().map(__lambda110__).filter(__lambda111__).allMatch(Main::isSymbol);

                if (!allSymbols) {
                    return new Err<>(new CompileError("Not all modifiers are strings", /* modifiersString));
                }

                String inputType = beforeName */.substring(typeSeparator + " ".length());
                return createTypeRule(typeParams).apply(inputType).flatMapValue(outputType -> new Ok<String, /* CompileError>(generateDefinition(typeParams, outputType, name)));
            }
            else {
                return createTypeRule */(Impl.emptyList()).apply(beforeName).flatMapValue(outputType -> new Ok<String, /* CompileError>(generateDefinition(Impl */.emptyList(), outputType, name)));
            }
        });
}
int findTypeSeparator(struct String beforeName) {
	int typeSeparator = -1;
	int depth = 0;
	int i = beforeName.length() - 1;
	while (i >= 0) {
		char c = beforeName.charAt(i);
		if (c == ' ' && depth == 0) {
			typeSeparator = i;
			break;
		}
		else {
			if (c == '>') {depth++;
			}
			if (c == ' < ') {depth--;
			}
		}i--;
	}
	return typeSeparator;
}
int __lambda112__() {
	return struct String.strip()
}
int __lambda113__(int param) {
	return !param.isEmpty();
}
List__struct String splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda112__).filter(__lambda113__).collect(ListCollector_());
}
int __lambda114__(int inner) {
	return "<" + inner + "> ";
}
struct String generateDefinition(List__struct String maybeTypeParams, struct String type, struct String name) {
	struct String typeParamsString = maybeTypeParams.iter().collect(struct Joiner(", ")).map(__lambda114__).orElse("");
	return typeParamsString + type + " " + name;
}
Function_struct String, Result_struct String, struct CompileError createTypeRule(List__struct String typeParams) {
	return createOrRule(Impl.listOf(createPrimitiveRule(), createArrayRule(typeParams), createSymbolRule(typeParams), createGenericRule(typeParams)));
}
int __lambda115__(int input) {
	return compileGeneric(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createGenericRule(List__struct String typeParams) {
	return wrap(__lambda115__);
}
int __lambda116__(int input) {
	return compileSymbol(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createSymbolRule(List__struct String typeParams) {
	return wrap(__lambda116__);
}
int __lambda117__(int input) {
	return compileArray(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createArrayRule(List__struct String typeParams) {
	return wrap(__lambda117__);
}
int __lambda118__(int input) {
	struct String stripped = input.strip();
	if (stripped.equals("void")) {
		return Some_("void");
	}
	if (stripped.equals("int") || stripped.equals("Integer") || stripped.equals("boolean") || stripped.equals("Boolean")) {
		return Some_("int");
	}
	if (stripped.equals("char") || stripped.equals("Character")) {
		return Some_("char");
	}
	return None_();
}
Function_struct String, Result_struct String, struct CompileError createPrimitiveRule() {
	return wrap(__lambda118__);
}
int __lambda119__(int value) {
	return value + "*";
}
Option_struct String compileArray(struct String input, List__struct String typeParams) {
	if (input.endsWith("[]")) {
		return createTypeRule(typeParams).apply(input.substring(0, input.length() - "[]".length())).findValue().map(__lambda119__);
	}
	return None_();
}
int __lambda120__() {
	return struct String.equals()
}
Option_struct String compileSymbol(struct String input, List__struct String typeParams) {
	if (isSymbol(input.strip())) {
		if (Impl.contains(typeParams, input.strip(), __lambda120__)) {
			return Some_(input.strip());
		}
		return Some_("struct " + input.strip());
	}
	return None_();
}
int __lambda121__() {
	return createTypeRule(typeParams).apply(type).findValue();
}
int __lambda122__(int type) {
			return compileWhitespace(type).or(__lambda121__);
}
int __lambda123__(int compiled) {
			return base + "_" + compiled;
}
Option_struct String compileGeneric(struct String stripped, List__struct String typeParams) {
	if (stripped.endsWith(">")) {
		struct String slice = stripped.substring(0, stripped.length() - ">".length());
		int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			struct String base = slice.substring(0, argsStart).strip();
			struct String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, wrap(__lambda122__)).findValue().map(__lambda123__);
		}
	}
	return None_();
}
int __lambda124__(int tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
struct boolean isSymbol(struct String input) {
	if (input.isBlank()) {
		return false;
	}
	if (input.equals("record") || input.equals("int") || input.equals("char")) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda124__);
}
Option_struct String generatePlaceholder(struct String input) {
	return Some_("/* " + input + " */");
}
