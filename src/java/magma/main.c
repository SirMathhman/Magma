struct IOError {
};
struct Path_ {
	struct Path_ resolveSibling(struct String_ sibling);
	List__struct String listNames();
};
struct Error {
	struct String_ display();
};
struct Context {
	struct String display();
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
struct StringContext {
};
struct CompileError {
};
struct ApplicationError {
};
struct Node {
	Option_struct String maybeType;
	Map__struct String, struct String strings;
	Map__struct String, List__struct Node nodeLists;
};
struct NodeContext {
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
            return other */.get();
	/* }

        @Override
        public Option */ < /* T> filter(Predicate */ < /* T> filter) {
            return new None */ < /* > */();/* 
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
struct String display() {
	return this.value;
}
struct public CompileError(struct String message, struct Context context) {
	this(message, context, Impl.emptyList());
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
	return this.message + ": " + this.context.display() + joined;
}
int __lambda9__() {
	return struct CompileError.computeMaxDepth()
}
int computeMaxDepth() {
	return 1 + this.errors.iter().map(__lambda9__).collect(struct Max()).orElse(0);
}
struct String_ display() {
	return struct String_(this.format(0));
}
struct String display0() {
	return this.error.display().value;
}
struct String_ display() {
	return struct String_(this.display0());
}
struct private Node() {
	this(None_(), Impl.emptyMap(), Impl.emptyMap());
}
struct private Node(Option_struct String maybeType, Map__struct String, struct String strings, Map__struct String, List__struct Node nodeLists) {
	this.maybeType = maybeType;
	this.strings = strings;
	this.nodeLists = nodeLists;
}
struct Node withString(struct String propertyKey, struct String propertyValue) {
	return struct Node(this.maybeType, this.strings.with(propertyKey, propertyValue), this.nodeLists);
}
Option_struct String findString(struct String propertyKey) {
	return this.strings.find(propertyKey);
}
struct Node withNodeList(struct String propertyKey, List__struct Node propertyValues) {
	return struct Node(this.maybeType, this.strings, this.nodeLists.with(propertyKey, propertyValues));
}
Option_List__struct Node findNodeList(struct String propertyKey) {
	return this.nodeLists.find(propertyKey);
}
struct Node merge(struct Node other) {
	return struct Node(this.maybeType, this.strings.withAll(other.strings), this.nodeLists.withAll(other.nodeLists));
}
struct String display() {
	return this.toString();
}
int __lambda10__(int inner) {
	return inner.equals(type);
}
struct boolean is(struct String type) {
	return this.maybeType.filter(__lambda10__).isPresent();
}
struct Node retype(struct String type) {
	return struct Node(Some_(type), this.strings, this.nodeLists);
}
struct String display() {
	return this.node.display();
}
int __lambda11__() {
	return struct ApplicationError.new()
}
int __lambda12__(int input) {
	return compileAndWrite(input, source);
}
int __lambda13__() {
	return struct Some.new()
}
int __lambda14__(int error) {
	return System.err.println(error.display().value);
}
struct void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).mapErr(__lambda11__).match(__lambda12__, __lambda13__).ifPresent(__lambda14__);
}
int __lambda15__() {
	return struct ApplicationError.new()
}
int __lambda16__() {
	return struct ApplicationError.new()
}
int __lambda17__(int output) {
	return Impl.writeString(target, output).map(__lambda16__);
}
int __lambda18__() {
	return struct Some.new()
}
Option_struct ApplicationError compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling(struct String_("main.c"));
	return compile(input).mapErr(__lambda15__).match(__lambda17__, __lambda18__);
}
int __lambda19__() {
	return struct Main.divideStatementChar()
}
int __lambda20__() {
	return struct Main.unwrapAll()
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
	List__struct String segments = divide(input, __lambda19__);
	return parseAll(segments, wrapToNode(createRootSegmentCompiler())).mapValue(__lambda20__).mapValue(__lambda21__).mapValue(__lambda23__);
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
int __lambda26__() {
	return struct Main.wrapToNode()
}
int __lambda27__() {
	return struct StringContext.new()
}
Function_struct String, Result_struct String, struct CompileError createRootSegmentCompiler() {
	return unwrapFromNode(createOrRule(Impl.listOf(createTypeRule("whitespace", createWhitespaceRule()), createTypeRule("package", wrapToResult(__lambda24__)), createTypeRule("import", wrapToResult(__lambda25__)), createClassRule(Impl.emptyList())).iter().map(__lambda26__).collect(ListCollector_()), __lambda27__));
}
int __lambda28__(int err) {
	struct String format = "Cannot assign type to '%s'";
	struct String message = format.formatted(type);
	return struct CompileError(message, struct StringContext(input), Impl.listOf(err));
}
int __lambda29__(int input) {
	return childRule.apply(input).mapErr(__lambda28__);
}
Function_struct String, Result_struct String, struct CompileError createTypeRule(struct String type, Function_struct String, Result_struct String, struct CompileError childRule) {
	return __lambda29__;
}
Function_struct String, Result_struct String, struct CompileError createWhitespaceRule() {/* 
        return input -> {
            if (input.isBlank()) {
                return new Ok<>("");
            }
            return new Err<>(new CompileError("Not blank", new StringContext(input)));
        } *//* ; */
}
int __lambda30__() {
	return struct state.withValue()
}
int __lambda31__() {
	return struct state.withError()
}
int __lambda32__(int state, int rule) {
	return rule.apply(input).match(__lambda30__, __lambda31__);
}
int __lambda33__(int children) {
	return struct CompileError("No valid combination", contextFactory.apply(input), children);
}
int __lambda34__(int input) {
	return rules.iter().fold(OrState_struct R(), __lambda32__).toResult().mapErr(__lambda33__);
}
<T, R> Function_T, Result_R, struct CompileError createOrRule(List__Function_struct T, Result_struct R, struct CompileError rules, Function_struct T, struct Context contextFactory) {
	return __lambda34__;
}
Option_struct String compileImport(struct String input) {
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			List__struct String split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Impl.equalsList(split.slice(0, 3), Impl.listOf("java", "util", "function"), java.lang.String::equals)) {
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
int __lambda35__() {
	return struct Main.mergeStatements()
}
struct String generateStatements(List__struct Node compiled) {
	return unwrapAndMergeAll(compiled, __lambda35__);
}
int __lambda36__() {
	return struct Main.divideStatementChar()
}
Result_List__struct Node, struct CompileError parseStatements(struct String input, Function_struct String, Result_struct Node, struct CompileError rule) {
	return parseAll(divide(input, __lambda36__), rule);
}
struct String unwrapAndMergeAll(List__struct Node compiled, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return mergeAll(unwrapAll(compiled), merger);
}
struct String mergeAll(List__struct String compiled, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
Function_struct String, Result_struct Node, struct CompileError wrapToNode(Function_struct String, Result_struct String, struct CompileError mapper) {/* 
        return s -> {
            return mapper.apply(s).mapValue(value -> new Node().withString("value", value));
        } *//* ; */
}
int __lambda37__() {
	return struct Main.unwrap()
}
List__struct String unwrapAll(List__struct Node result) {
	return result.iter().map(__lambda37__).collect(ListCollector_());
}
struct String unwrap(struct Node node) {
	return node.findString("value").orElse("");
}
int __lambda38__() {
	return struct allCompiled.add()
}
int __lambda39__(int allCompiled) {
	return wrapped.apply(segment).mapValue(__lambda38__);
}
int __lambda40__(int maybeCompiled, int segment) {
	return maybeCompiled.flatMapValue(__lambda39__);
}
Result_List__struct Node, struct CompileError parseAll(List__struct String segments, Function_struct String, Result_struct Node, struct CompileError wrapped) {
	return segments.iter().<Result<List_<Node>, CompileError>>fold(Ok_(Impl.emptyList()), __lambda40__);
}
int __lambda41__() {
	return struct Ok.new()
}
int __lambda42__() {
	return Err_(struct CompileError("Invalid value", struct StringContext(input)));
}
int __lambda43__(int input) {
	return compiler.apply(input).<Result<String, CompileError>>map(__lambda41__).orElseGet(__lambda42__);
}
Function_struct String, Result_struct String, struct CompileError wrapToResult(Function_struct String, Option_struct String compiler) {
	return __lambda43__;
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
int __lambda44__(int input1) {
	return createClassMemberRule(typeParams).apply(input1);
}
int __lambda45__() {
	return struct Main.generateStatements()
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
                return new Err<>(new CompileError("Not a symbol", new StringContext(withoutParams)));
            }

            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (!withEnd.endsWith("}")) {
                return new Err<>(new CompileError("Suffix '}' not present", new StringContext(withEnd)));
            }

            String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
            return parseStatements(inputContent, wrapToNode(__lambda44__)).mapValue(__lambda45__).mapValue(outputContent -> {
                structs.add("struct " + withoutParams + " {" + outputContent + "\n};\n");
                return "";
            });
        });
}
<T> Result_T, struct CompileError createInfixErr(struct String input, struct String infix) {
	return Err_(struct CompileError("Infix '" + infix + "' not present", struct StringContext(input)));
}
int __lambda46__(int input) {
	return compileGlobalInitialization(input, typeParams);
}
int __lambda47__() {
	return struct Main.compileDefinitionStatement()
}
int __lambda48__() {
	return struct Main.wrapToNode()
}
int __lambda49__() {
	return struct StringContext.new()
}
Function_struct String, Result_struct String, struct CompileError createClassMemberRule(List__struct String typeParams) {
	return unwrapFromNode(createOrRule(Impl.listOf(createWhitespaceRule(), createCompileToStructRule("interface", "interface ", typeParams), createCompileToStructRule("record", "record ", typeParams), createClassRule(typeParams), wrapToResult(__lambda46__), wrapToResult(__lambda47__), createMethodRule(typeParams)).iter().map(__lambda48__).collect(ListCollector_()), __lambda49__));
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
                return parseValues(params, wrapToNode(createParameterRule())).mapValue(Main::generateValues).flatMapValue(outputParams -> {
                    String substring = withParams.substring(paramEnd + ")".length());
                    return assembleMethodBody(typeParams, outputDefinition, outputParams, substring.strip());
                });
            });
        } *//* ; */
}
Function_struct String, Result_struct String, struct CompileError createClassRule(List__struct String typeParams) {
	return createCompileToStructRule("class", "class ", typeParams);
}
int __lambda50__(int result) {
	return "\n\t" + result + ";";
}
Option_struct String compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return createDefinitionRule().apply(content).findValue().map(__lambda50__);
	}
	return None_();
}
int __lambda51__(int generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_struct String compileGlobalInitialization(struct String input, List__struct String typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda51__);
}
int __lambda52__(int outputValue) {
	return outputDefinition + " = " + outputValue;
}
int __lambda53__(int outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda52__);
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
	return createDefinitionRule().apply(definition).findValue().flatMap(__lambda53__);
}
Option_struct String compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
int __lambda54__(int input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
int __lambda55__() {
	return struct Main.generateStatements()
}
int __lambda56__(int outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Ok_("");
}
Result_struct String, struct CompileError assembleMethodBody(List__struct String typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return parseStatements(inputContent, wrapToNode(wrapToResult(__lambda54__))).mapValue(__lambda55__).flatMapValue(__lambda56__);
	}
	return Ok_("\n\t" + header + ";");
}
int __lambda57__() {
	return struct Main.compileWhitespace()
}
int __lambda58__() {
	return struct Main.wrapToNode()
}
int __lambda59__() {
	return struct StringContext.new()
}
Function_struct String, Result_struct String, struct CompileError createParameterRule() {
	return unwrapFromNode(createOrRule(Impl.listOf(wrapToResult(__lambda57__), createDefinitionRule()).iter().map(__lambda58__).collect(ListCollector_()), __lambda59__));
}
int __lambda60__() {
	return struct Main.mergeValues()
}
struct String generateValues(List__struct Node compiled) {
	return unwrapAndMergeAll(compiled, __lambda60__);
}
Result_List__struct Node, struct CompileError parseValues(struct String input, Function_struct String, Result_struct Node, struct CompileError rule) {
	return parseAll(divideValues(input), rule);
}
int __lambda61__() {
	return struct Main.divideValueChar()
}
List__struct String divideValues(struct String input) {
	return divide(input, __lambda61__);
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
int __lambda62__() {
	return compileKeywordStatement(input, depth, "continue");
}
int __lambda63__() {
	return compileKeywordStatement(input, depth, "break");
}
int __lambda64__() {
	return compileConditional(input, typeParams, "if ", depth);
}
int __lambda65__() {
	return compileConditional(input, typeParams, "while ", depth);
}
int __lambda66__() {
	return compileElse(input, typeParams, depth);
}
int __lambda67__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
int __lambda68__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
int __lambda69__(int result) {
	return formatStatement(depth, result);
}
int __lambda70__() {
	return compileReturn(input, typeParams, depth).map(__lambda69__);
}
int __lambda71__(int result) {
	return formatStatement(depth, result);
}
int __lambda72__() {
	return compileInitialization(input, typeParams, depth).map(__lambda71__);
}
int __lambda73__(int result) {
	return formatStatement(depth, result);
}
int __lambda74__() {
	return compileAssignment(input, typeParams, depth).map(__lambda73__);
}
int __lambda75__(int result) {
	return formatStatement(depth, result);
}
int __lambda76__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda75__);
}
int __lambda77__() {
	return compileDefinitionStatement(input);
}
int __lambda78__() {
	return generatePlaceholder(input);
}
Option_struct String compileStatementOrBlock(struct String input, List__struct String typeParams, int depth) {
	return compileWhitespace(input).or(__lambda62__).or(__lambda63__).or(__lambda64__).or(__lambda65__).or(__lambda66__).or(__lambda67__).or(__lambda68__).or(__lambda70__).or(__lambda72__).or(__lambda74__).or(__lambda76__).or(__lambda77__).or(__lambda78__);
}
int __lambda79__(int value) {
	return value + operator + ";";
}
Option_struct String compilePostOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda79__);
	}
	else {
		return None_();
	}
}
int __lambda80__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
int __lambda81__() {
	return struct Main.generateStatements()
}
int __lambda82__(int result) {
	return indent + "else {" + result + indent + "}";
}
int __lambda83__(int result) {
	return "else " + result;
}
Option_struct String compileElse(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return parseStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapToNode(wrapToResult(__lambda80__))).mapValue(__lambda81__).findValue().map(__lambda82__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda83__);
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
int __lambda84__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
int __lambda85__() {
	return struct Main.generateStatements()
}
int __lambda86__(int statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
int __lambda87__(int result) {
		return withCondition + " " + result;
}
int __lambda88__(int newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return parseStatements(content, wrapToNode(wrapToResult(__lambda84__))).mapValue(__lambda85__).findValue().map(__lambda86__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda87__);
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
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda88__);
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
int __lambda89__(int newSource) {
			return newDest + " = " + newSource;
}
int __lambda90__(int newDest) {
			return compileValue(source, typeParams, depth).map(__lambda89__);
}
Option_struct String compileAssignment(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda90__);
		}
	}
	return None_();
}
int __lambda91__(int result) {
	return "return " + result;
}
Option_struct String compileReturn(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda91__);
		}
	}
	return None_();
}
int __lambda92__() {
	return struct Main.generateType()
}
int __lambda93__(int value) {
	return outputType + value;
}
int __lambda94__(int outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda93__);
}
int __lambda95__(int result) {
	return "!" + result;
}
int __lambda96__() {
	return struct Main.generateType()
}
int __lambda97__(int compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
int __lambda98__(int compiled) {
	return compiled + "." + property;
}
int __lambda99__() {
	return compileOperator(input, typeParams, depth, "<");
}
int __lambda100__() {
	return compileOperator(input, typeParams, depth, "+");
}
int __lambda101__() {
	return compileOperator(input, typeParams, depth, ">=");
}
int __lambda102__() {
	return compileOperator(input, typeParams, depth, "&&");
}
int __lambda103__() {
	return compileOperator(input, typeParams, depth, "==");
}
int __lambda104__() {
	return compileOperator(input, typeParams, depth, "!=");
}
int __lambda105__() {
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
				return (/* (Function */ < /* String, Result */ < /* String, CompileError>>) s -> parseType */(typeParams, s).flatMapValue(__lambda92__)).apply(type).findValue().flatMap(__lambda94__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda95__);
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
			return (/* (Function */ < /* String, Result */ < /* String, CompileError>>) s -> parseType */(typeParams, s).flatMapValue(__lambda96__)).apply(type).findValue().flatMap(__lambda97__);
		}
	}
	int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda98__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda99__).or(__lambda100__).or(__lambda101__).or(__lambda102__).or(__lambda103__).or(__lambda104__).or(__lambda105__);
}
int __lambda106__(int rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
int __lambda107__(int leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda106__);
}
Option_struct String compileOperator(struct String input, List__struct String typeParams, int depth, struct String operator) {
	int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda107__);
}
int __lambda108__(int value) {
	return !value.isEmpty();
}
int __lambda109__(int statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
int __lambda110__() {
	return struct Main.generateStatements()
}
int __lambda111__(int result) {
		return generateLambdaWithReturn(paramNames, result);
}
int __lambda112__(int newValue) {
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
		paramNames = splitByDelimiter(inner, ',').iter().map(java.lang.String::strip).filter(__lambda108__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return parseStatements(slice, wrapToNode(wrapToResult(__lambda109__))).mapValue(__lambda110__).findValue().flatMap(__lambda111__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda112__);
}
int __lambda113__(int name) {
	return "int " + name;
}
Option_struct String generateLambdaWithReturn(List__struct String paramNames, struct String returnValue) {
	int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda113__).collect(struct Joiner(", ")).orElse("");
	methods.add("int " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
int __lambda114__(int tuple) {
	int index = tuple.left;
	char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
struct boolean isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda114__);
}
int __lambda115__(int value) {
	return caller + value;
}
int __lambda116__(int caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda115__);
}
Option_struct String compileInvocation(struct String input, List__struct String typeParams, int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda116__);
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
int __lambda117__() {
	return compileValue(arg, typeParams, depth);
}
int __lambda118__(int arg) {
	return compileWhitespace(arg).or(__lambda117__);
}
int __lambda119__() {
	return struct Main.generateValues()
}
int __lambda120__(int args) {
	return "(" + args + ")";
}
Option_struct String compileArgs(struct String argsString, List__struct String typeParams, int depth) {
	return parseValues(argsString, wrapToNode(wrapToResult(__lambda118__))).mapValue(__lambda119__).findValue().map(__lambda120__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
int __lambda121__(int input) {
	return /* {
            String stripped = input */.strip();
            int nameSeparator = stripped.lastIndexOf(" ");
            if (nameSeparator < 0) {
                return createInfixErr(stripped, " ");
            }

            String beforeName = stripped.substring(0, nameSeparator).strip();
            String name = stripped.substring(nameSeparator + " ".length()).strip();
            if (!isSymbol(name)) {
                return new Err<>(new CompileError("Not a symbol", new StringContext(name)));
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
int __lambda122__(int value) {
	return !value.isEmpty();
}
int __lambda123__(int value) {
	return !value.isEmpty();
}
int __lambda124__() {
	return struct Main.generateType()
}
int __lambda125__() {
	return struct Main.generateType()
}
Function_struct String, Result_struct String, struct CompileError createDefinitionRule() {
	return /* createTypeRule("definition", input -> {
            String stripped = input */.strip();
            int nameSeparator = stripped.lastIndexOf(" ");
            if (nameSeparator < 0) {
                return createInfixErr(stripped, " ");
            }

            String beforeName = stripped.substring(0, nameSeparator).strip();
            String name = stripped.substring(nameSeparator + " ".length()).strip();
            if (!isSymbol(name)) {
                return new Err<>(new CompileError("Not a symbol", new StringContext(name)));
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
                        beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                        String substring = withoutEnd.substring(typeParamStart + 1);
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

                boolean allSymbols = splitByDelimiter(modifiersString, ' ').iter().map(java.lang.String::strip).filter(__lambda123__).allMatch(Main::isSymbol);

                if (!allSymbols) {
                    return new Err<>(new CompileError("Not all modifiers are strings", new StringContext(modifiersString)));
                }

                String inputType = beforeName.substring(typeSeparator + " ".length());
                return parseType(typeParams, inputType).flatMapValue(__lambda124__).flatMapValue(outputType -> new Ok<String, CompileError>(generateDefinition(typeParams, outputType, name)));
            }
            else {
                List_<String> typeParams = Impl.emptyList();
                return parseType(typeParams, beforeName).flatMapValue(__lambda125__).flatMapValue(outputType -> new Ok<String, CompileError>(generateDefinition(Impl.emptyList(), outputType, name)));
            }
        });
}
Result_struct Node, struct CompileError parseType(List__struct String typeParams, struct String s) {
	return createTypeRule(typeParams).apply(s);
}
Result_struct String, struct CompileError generateType(struct Node result) {
	if (result.is("generic")) {
		return generateGeneric(result);
	}
	else {
		return generateDefaultValue(result);
	}
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
int __lambda126__(int param) {
	return !param.isEmpty();
}
List__struct String splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(java.lang.String::strip).filter(__lambda126__).collect(ListCollector_());
}
int __lambda127__(int inner) {
	return "<" + inner + "> ";
}
struct String generateDefinition(List__struct String maybeTypeParams, struct String type, struct String name) {
	struct String typeParamsString = maybeTypeParams.iter().collect(struct Joiner(", ")).map(__lambda127__).orElse("");
	return typeParamsString + type + " " + name;
}
int __lambda128__(int input) {
	return parseGeneric(input, typeParams);
}
int __lambda129__() {
	return struct StringContext.new()
}
Function_struct String, Result_struct Node, struct CompileError createTypeRule(List__struct String typeParams) {
	return Main.createOrRule(Impl.listOf(getStringResultFunction(), createArrayRule(typeParams), createSymbolRule(typeParams), __lambda128__), __lambda129__);
}
int __lambda130__(int input) {
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
Function_struct String, Result_struct Node, struct CompileError getStringResultFunction() {
	return wrapToNode(wrapToResult(__lambda130__));
}
int __lambda131__() {
	return struct Main.generateDefaultValue()
}
int __lambda132__(int input) {
	return mapper.apply(input).flatMapValue(__lambda131__);
}
Function_struct String, Result_struct String, struct CompileError unwrapFromNode(Function_struct String, Result_struct Node, struct CompileError mapper) {
	return __lambda132__;
}
Result_struct String, struct CompileError generateDefaultValue(struct Node result) {
	return generateString(result, "value");
}
int __lambda133__() {
	return struct Ok.new()
}
int __lambda134__() {
	return Err_(struct CompileError("String '" + propertyKey + "' not present", struct NodeContext(node)));
}
Result_struct String, struct CompileError generateString(struct Node node, struct String propertyKey) {
	return node.findString(propertyKey).<Result<String, CompileError>>map(__lambda133__).orElseGet(__lambda134__);
}
int __lambda135__() {
	return struct Main.generateType()
}
int __lambda136__(int s) {
	return parseType(typeParams, s).flatMapValue(__lambda135__);
}
int __lambda137__() {
	return struct Main.wrapToNode()
}
int __lambda138__() {
	return struct StringContext.new()
}
int __lambda139__(int args) {
	return withBase.merge(struct Node(/* ) */.withNodeList("args", args));
}
int __lambda140__(int node) {
	return node.retype("generic");
}
Result_struct Node, struct CompileError parseGeneric(struct String input, List__struct String typeParams) {
	if (!input.endsWith(">")) {
		return createSuffixErr(input, ">");
	}
	struct String slice = input.substring(0, input.length() - ">".length());
	int argsStart = slice.indexOf("<");
	if (argsStart < 0) {
		return createInfixErr(slice, "<");
	}
	struct String base = slice.substring(0, argsStart).strip();
	struct Node withBase = struct Node(/* ) */.withString("base", base);
	struct String params = slice.substring(argsStart + " < ".length()).strip();
	Function_struct String, Result_struct Node, struct CompileError orRule = createOrRule(Impl.listOf(createWhitespaceRule(), __lambda136__).iter().map(__lambda137__).collect(ListCollector_()), __lambda138__);
	return parseValues(params, orRule).mapValue(__lambda139__).mapValue(__lambda140__);
}
Result_struct String, struct CompileError generateGeneric(struct Node node) {
	struct String base = node.findString("base").orElse("");
	List__struct Node paramNodes = node.findNodeList("args").orElse(Impl.emptyList());
	struct String joined = generateValues(paramNodes);
	return Ok_(base + "_" + joined);
}
<T> Result_T, struct CompileError createSuffixErr(struct String input, struct String suffix) {
	return Err_(struct CompileError("Suffix '" + suffix + "' not present", struct StringContext(input)));
}
int __lambda141__(int input) {
	return compileSymbol(input, typeParams);
}
Function_struct String, Result_struct Node, struct CompileError createSymbolRule(List__struct String typeParams) {
	return wrapToNode(wrapToResult(__lambda141__));
}
int __lambda142__(int input) {
	return compileArray(input, typeParams);
}
Function_struct String, Result_struct Node, struct CompileError createArrayRule(List__struct String typeParams) {
	return wrapToNode(wrapToResult(__lambda142__));
}
int __lambda143__() {
	return struct Main.generateType()
}
int __lambda144__(int value) {
	return value + "*";
}
Option_struct String compileArray(struct String input, List__struct String typeParams) {
	if (input.endsWith("[]")) {
		return (/* (Function */ < /* String, Result */ < /* String, CompileError>>) s -> parseType */(typeParams, s).flatMapValue(__lambda143__)).apply(input.substring(0, input.length() - "[]".length())).findValue().map(__lambda144__);
	}
	return None_();
}
Option_struct String compileSymbol(struct String input, List__struct String typeParams) {
	if (isSymbol(input.strip())) {
		if (Impl.contains(typeParams, input.strip(), java.lang.String::equals)) {
			return Some_(input.strip());
		}
		return Some_("struct " + input.strip());
	}
	return None_();
}
int __lambda145__(int tuple) {
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
	return Iterators.fromStringWithIndices(input).allMatch(__lambda145__);
}
Option_struct String generatePlaceholder(struct String input) {
	return Some_("/* " + input + " */");
}
