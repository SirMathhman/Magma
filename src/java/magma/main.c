struct IOError {
	struct String display();
};
struct Path_ {
	struct Path_ resolveSibling(struct String sibling);
	List__struct String listNames();
};
struct Error {
	struct String display();
};
struct State {
	List__struct Character queue;
	List__struct String segments;
	struct StringBuilder buffer;
	struct int depth;
};
struct Iterators {
};
struct Main {
	<T> {
        <R> Option_struct R map(Function_struct T, struct R mapper);
	<T> {
        <R> struct R fold(struct R initial, BiFunction_struct R, struct T, struct R folder);
	<T, X> {
        <R> struct R match(Function_struct T, struct R whenOk, Function_struct X, struct R whenErr);
	struct record Joiner(struct String delimiter);
	struct record CompileError(struct String message, struct String context, List__struct CompileError errors);
	struct record ApplicationError(struct Error error);
};
List__struct String imports = Impl.emptyList();
List__struct String structs = Impl.emptyList();
List__struct String globals = Impl.emptyList();
List__struct String methods = Impl.emptyList();
struct int counter = 0;
struct private State(List__struct Character queue, List__struct String segments, struct StringBuilder buffer, struct int depth) {
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
struct State append(struct char c) {
	this.buffer.append(c);
	return this;
}
struct boolean isLevel() {
	return this.depth == 0;
}
struct char pop() {
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
struct char peek() {
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
Option_struct T next() {
	return None_();/* 
        }
     */
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
}
auto __lambda0__() {
	return struct Tuple.right()
}
Iterator_struct Character fromString(struct String string) {
	return fromStringWithIndices(string).map(__lambda0__);
}
Iterator_Tuple_struct Integer, struct Character fromStringWithIndices(struct String string) {
	return HeadedIterator_(struct RangeHead(string.length())).map(index -> new Tuple<>(index, string.charAt(index)));
}
auto __lambda1__() {
	return struct Tuple.right()
}
auto __lambda2__(auto index) {
	return Tuple_(index, string.charAt(index));
}
<T> Iterator_T empty() {
	return HeadedIterator_(EmptyHead_());
	/* }

        public static Iterator */ < /* Character> fromString(String string) {
            return fromStringWithIndices */(string).map(__lambda1__);
	/* }

        public static Iterator */ < Tuple < /* Integer, Character>> fromStringWithIndices(String string) {
            return new HeadedIterator */ < /* > */(struct RangeHead(string.length())).map(__lambda2__);/* 
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
auto __lambda3__(auto inner) {
	return /* inner > element ? inner : element */;
}
Option_struct Integer createInitial() {
	return None_();
	/* }

        @Override
        public Option */ < /* Integer> fold(Option */ < /* Integer> current, Integer element) {
            return new Some */ < /* > */(current.map(__lambda3__).orElse(element));/* 
        }
     */
}
struct record OrState(Option_struct String maybeValue, List__struct CompileError errors) {/* 
        public OrState() {
            this(new None<>(), Impl.emptyList());
        } *//* 

        public OrState withValue(String value) {
            return new OrState(new Some<>(value), this.errors);
        } *//* 

        public OrState withError(CompileError error) {
            return new OrState(this.maybeValue, this.errors.add(error));
        } *//* 

        public Result<String, List_<CompileError>> toResult() {
            return this.maybeValue.<Result<String, List_<CompileError>>>map(Ok::new)
                    .orElseGet(() -> new Err<>(this.errors));
        } */
}
auto __lambda4__() {
	return struct ApplicationError.new()
}
auto __lambda5__(auto input) {
	return compileAndWrite(input, source);
}
auto __lambda6__() {
	return struct Some.new()
}
auto __lambda7__(auto error) {
	return System.err.println(error.display());
}
struct void main(struct String* args) {
	struct Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
	Impl.readString(source).mapErr(__lambda4__).match(__lambda5__, __lambda6__).ifPresent(__lambda7__);
}
auto __lambda8__() {
	return struct ApplicationError.new()
}
auto __lambda9__() {
	return struct ApplicationError.new()
}
auto __lambda10__(auto output) {
	return Impl.writeString(target, output).map(__lambda9__);
}
auto __lambda11__() {
	return struct Some.new()
}
Option_struct ApplicationError compileAndWrite(struct String input, struct Path_ source) {
	struct Path_ target = source.resolveSibling("main.c");
	return compile(input).mapErr(__lambda8__).match(__lambda10__, __lambda11__);
}
auto __lambda12__() {
	return struct Main.divideStatementChar()
}
auto __lambda13__() {
	return struct Main.assembleChildren()
}
auto __lambda14__() {
	return struct Main.mergeStatements()
}
auto __lambda15__(auto compiled) {
	return mergeAll(compiled, __lambda14__);
}
Result_struct String, struct CompileError compile(struct String input) {
	return parseAll(divide(input, __lambda12__), createRootSegmentCompiler()).mapValue(__lambda13__).mapValue(__lambda15__);
}
List__struct String assembleChildren(List__struct String rootChildren) {
	return Impl.<String>emptyList().addAll(imports).addAll(structs).addAll(globals).addAll(methods).addAll(rootChildren);
}
auto __lambda16__() {
	return struct Main.compilePackage()
}
auto __lambda17__() {
	return struct Main.compileImport()
}
Function_struct String, Result_struct String, struct CompileError createRootSegmentCompiler() {
	return createOrRule(Impl.listOf(createTypeRule("whitespace", createWhitespaceRule()), createTypeRule("package", wrap(__lambda16__)), createTypeRule("import", wrap(__lambda17__)), createClassRule(Impl.emptyList())));
}
auto __lambda18__(auto err) {
	struct String format = "Cannot assign type to '%s'";
	struct String message = format.formatted(type);
	return struct CompileError(message, input, Impl.listOf(err));
}
auto __lambda19__(auto input) {
	return childRule.apply(input).mapErr(__lambda18__);
}
Function_struct String, Result_struct String, struct CompileError createTypeRule(struct String type, Function_struct String, Result_struct String, struct CompileError childRule) {
	return __lambda19__;
}
Function_struct String, Result_struct String, struct CompileError createWhitespaceRule() {/* 
        return input -> {
            if (input.isBlank()) {
                return new Ok<>("");
            }
            return new Err<>(new CompileError("Not blank", input));
        } *//* ; */
}
auto __lambda20__() {
	return struct state.withValue()
}
auto __lambda21__() {
	return struct state.withError()
}
auto __lambda22__(auto state, auto rule) {
	return rule.apply(input).match(__lambda20__, __lambda21__);
}
auto __lambda23__(auto children) {
	return struct CompileError("No valid combination", input, children);
}
auto __lambda24__(auto input) {
	return aClass.iter().fold(struct OrState(), __lambda22__).toResult().mapErr(__lambda23__);
}
Function_struct String, Result_struct String, struct CompileError createOrRule(List__Function_struct String, Result_struct String, struct CompileError aClass) {
	return __lambda24__;
}
auto __lambda25__() {
	return struct String.equals()
}
Option_struct String compileImport(struct String input) {
	struct String stripped = input.strip();
	if (stripped.startsWith("import ")) {
		struct String right = stripped.substring("import ".length());
		if (right.endsWith(";")) {
			struct String content = right.substring(0, right.length() - ";".length());
			List__struct String split = splitByDelimiter(content, '.');
			if (split.size() >= 3 && Impl.equalsList(split.slice(0, 3), Impl.listOf("java", "util", "function"), __lambda25__)) {
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
auto __lambda26__() {
	return struct Main.divideStatementChar()
}
auto __lambda27__() {
	return struct Main.mergeStatements()
}
Result_struct String, struct CompileError compileStatements(struct String input, Function_struct String, Result_struct String, struct CompileError compiler) {
	return compileAndMerge(divide(input, __lambda26__), compiler, __lambda27__);
}
auto __lambda28__(auto compiled) {
	return mergeAll(compiled, merger);
}
Result_struct String, struct CompileError compileAndMerge(List__struct String segments, Function_struct String, Result_struct String, struct CompileError compiler, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return parseAll(segments, compiler).mapValue(__lambda28__);
}
struct String mergeAll(List__struct String compiled, BiFunction_struct StringBuilder, struct String, struct StringBuilder merger) {
	return compiled.iter().fold(struct StringBuilder(), merger).toString();
}
auto __lambda29__() {
	return struct allCompiled.add()
}
auto __lambda30__(auto allCompiled) {
	return compiler.apply(segment).mapValue(__lambda29__);
}
auto __lambda31__(auto maybeCompiled, auto segment) {
	return maybeCompiled.flatMapValue(__lambda30__);
}
Result_List__struct String, struct CompileError parseAll(List__struct String segments, Function_struct String, Result_struct String, struct CompileError compiler) {
	return segments.iter().<Result<List_<String>, CompileError>>fold(Ok_(Impl.emptyList()), __lambda31__);
}
auto __lambda32__() {
	return struct Ok.new()
}
auto __lambda33__() {
	return Err_(struct CompileError("Invalid value", input));
}
auto __lambda34__(auto input) {
	return compiler.apply(input).<Result<String, CompileError>>map(__lambda32__).orElseGet(__lambda33__);
}
Function_struct String, Result_struct String, struct CompileError wrap(Function_struct String, Option_struct String compiler) {
	return __lambda34__;
}
struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return output.append(compiled);
}
List__struct String divide(struct String input, BiFunction_struct State, struct Character, struct State divider) {
	List__struct Character queue = Iterators.fromString(input).collect(ListCollector_());
	struct State state = struct State(queue);
	while (state.hasElements()) {
		struct char c = state.pop();
		if (c == '\'') {
			state.append(c);
			struct char maybeSlash = state.pop();
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
				struct char next = state.pop();
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
struct State divideStatementChar(struct State state, struct char c) {
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
List__struct String splitByDelimiter(struct String content, struct char delimiter) {
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
auto __lambda35__(auto input1) {
	return createClassMemberRule(typeParams).apply(input1);
}
auto __lambda36__(auto outputContent) {
	structs.add("struct " + withoutExtends + " {" + outputContent + "\n};\n");
	return "";
}
auto __lambda37__(auto input) {
	struct int classIndex = input.indexOf(infix);
	if (classIndex < 0) {
		return createInfixErr(input, infix);
	}
	struct String afterKeyword = input.substring(classIndex + infix.length());
	struct int contentStart = afterKeyword.indexOf("{");
	if (contentStart < 0) {
		return createInfixErr(afterKeyword, "{");
	}
	struct String beforeContent = afterKeyword.substring(0, contentStart).strip();
	struct int extendsIndex = beforeContent.indexOf(" extends ");
	struct String withoutExtends = extendsIndex >= /*  0
                    ? beforeContent */.substring(0, extendsIndex)
                    : beforeContent;
	struct int typeParamsStart = withoutExtends.indexOf("<");
	if (typeParamsStart >= 0) {
		return Ok_("");
	}
	if (!isSymbol(withoutExtends)) {
		return Err_(struct CompileError("Not a symbol", withoutExtends));
	}
	struct String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
	if (!withEnd.endsWith("}")) {
		return Err_(struct CompileError("Suffix '}' not present", withEnd));
	}
	struct String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
	return compileStatements(inputContent, __lambda35__).mapValue(__lambda36__);
}
Function_struct String, Result_struct String, struct CompileError createCompileToStructRule(struct String type, struct String infix, List__struct String typeParams) {
	return createTypeRule(type, __lambda37__);
}
Err_struct String, struct CompileError createInfixErr(struct String input, struct String infix) {
	return Err_(struct CompileError("Infix '" + infix + "' not present", input));
}
auto __lambda38__(auto input) {
	return compileGlobalInitialization(input, typeParams);
}
auto __lambda39__() {
	return struct Main.compileDefinitionStatement()
}
auto __lambda40__(auto input) {
	return compileMethod(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createClassMemberRule(List__struct String typeParams) {
	return createOrRule(Impl.listOf(createWhitespaceRule(), createCompileToStructRule("interface", "interface ", typeParams), createCompileToStructRule("record", "record ", typeParams), createClassRule(typeParams), wrap(__lambda38__), wrap(__lambda39__), wrap(__lambda40__)));
}
Function_struct String, Result_struct String, struct CompileError createClassRule(List__struct String typeParams) {
	return createCompileToStructRule("class", "class ", typeParams);
}
auto __lambda41__(auto result) {
	return "\n\t" + result + ";";
}
Option_struct String compileDefinitionStatement(struct String input) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String content = stripped.substring(0, stripped.length() - ";".length());
		return compileDefinition(content).map(__lambda41__);
	}
	return None_();
}
auto __lambda42__(auto generated) {
	globals.add(generated + ";\n");
	return "";
}
Option_struct String compileGlobalInitialization(struct String input, List__struct String typeParams) {
	return compileInitialization(input, typeParams, 0).map(__lambda42__);
}
auto __lambda43__(auto outputValue) {
	return outputDefinition + " = " + outputValue;
}
auto __lambda44__(auto outputDefinition) {
	return compileValue(value, typeParams, depth).map(__lambda43__);
}
Option_struct String compileInitialization(struct String input, List__struct String typeParams, struct int depth) {
	if (!input.endsWith(";")) {
		return None_();
	}
	struct String withoutEnd = input.substring(0, input.length() - ";".length());
	struct int valueSeparator = withoutEnd.indexOf("=");
	if (valueSeparator < 0) {
		return None_();
	}
	struct String definition = withoutEnd.substring(0, valueSeparator).strip();
	struct String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
	return compileDefinition(definition).flatMap(__lambda44__);
}
Option_struct String compileWhitespace(struct String input) {
	if (input.isBlank()) {
		return Some_("");
	}
	return None_();
}
auto __lambda45__(auto definition) {
	return createParameterRule().apply(definition).findValue();
}
Option_struct String compileMethod(struct String input, List__struct String typeParams) {
	struct int paramStart = input.indexOf("(");
	if (paramStart < 0) {
		return None_();
	}
	struct String inputDefinition = input.substring(0, paramStart).strip();
	struct String withParams = input.substring(paramStart + "(".length());
	return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, __lambda45__).flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
}
auto __lambda46__(auto input1) {
	return compileStatementOrBlock(input1, typeParams, 1);
}
auto __lambda47__(auto outputContent) {
		methods.add(header + " {" + outputContent + "\n}\n");
		return Some_("");
}
Option_struct String assembleMethodBody(List__struct String typeParams, struct String definition, struct String params, struct String body) {
	struct String header = "\t".repeat(0) + definition + "(" + params + ")";
	if (body.startsWith("{") && body.endsWith("}")) {
		struct String inputContent = body.substring("{".length(), body.length() - "}".length());
		return compileStatements(inputContent, wrap(__lambda46__)).findValue().flatMap(__lambda47__);
	}
	return Some_("\n\t" + header + ";");
}
auto __lambda48__() {
	return struct Main.compileWhitespace()
}
auto __lambda49__() {
	return struct Main.compileDefinition()
}
Function_struct String, Result_struct String, struct CompileError createParameterRule() {
	return createOrRule(Impl.listOf(wrap(__lambda48__), wrap(__lambda49__)));
}
auto __lambda50__() {
	return struct Main.divideValueChar()
}
Option_struct String compileValues(struct String input, Function_struct String, Option_struct String compiler) {
	List__struct String divided = divide(input, __lambda50__);
	return compileValues(divided, compiler);
}
struct State divideValueChar(struct State state, struct char c) {
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
auto __lambda51__() {
	return struct Main.mergeValues()
}
Option_struct String compileValues(List__struct String params, Function_struct String, Option_struct String compiler) {
	return compileAndMerge(params, wrap(compiler), __lambda51__).findValue();
}
auto __lambda52__() {
	return compileKeywordStatement(input, depth, "continue");
}
auto __lambda53__() {
	return compileKeywordStatement(input, depth, "break");
}
auto __lambda54__() {
	return compileConditional(input, typeParams, "if ", depth);
}
auto __lambda55__() {
	return compileConditional(input, typeParams, "while ", depth);
}
auto __lambda56__() {
	return compileElse(input, typeParams, depth);
}
auto __lambda57__() {
	return compilePostOperator(input, typeParams, depth, "++");
}
auto __lambda58__() {
	return compilePostOperator(input, typeParams, depth, "--");
}
auto __lambda59__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda60__() {
	return compileReturn(input, typeParams, depth).map(__lambda59__);
}
auto __lambda61__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda62__() {
	return compileInitialization(input, typeParams, depth).map(__lambda61__);
}
auto __lambda63__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda64__() {
	return compileAssignment(input, typeParams, depth).map(__lambda63__);
}
auto __lambda65__(auto result) {
	return formatStatement(depth, result);
}
auto __lambda66__() {
	return compileInvocationStatement(input, typeParams, depth).map(__lambda65__);
}
auto __lambda67__() {
	return compileDefinitionStatement(input);
}
auto __lambda68__() {
	return generatePlaceholder(input);
}
Option_struct String compileStatementOrBlock(struct String input, List__struct String typeParams, struct int depth) {
	return compileWhitespace(input).or(__lambda52__).or(__lambda53__).or(__lambda54__).or(__lambda55__).or(__lambda56__).or(__lambda57__).or(__lambda58__).or(__lambda60__).or(__lambda62__).or(__lambda64__).or(__lambda66__).or(__lambda67__).or(__lambda68__);
}
auto __lambda69__(auto value) {
	return value + operator + ";";
}
Option_struct String compilePostOperator(struct String input, List__struct String typeParams, struct int depth, struct String operator) {
	struct String stripped = input.strip();
	if (stripped.endsWith(operator + ";")) {
		struct String slice = stripped.substring(0, stripped.length() -(operator + ";").length());
		return compileValue(slice, typeParams, depth).map(__lambda69__);
	}
	else {
		return None_();
	}
}
auto __lambda70__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda71__(auto result) {
	return indent + "else {" + result + indent + "}";
}
auto __lambda72__(auto result) {
	return "else " + result;
}
Option_struct String compileElse(struct String input, List__struct String typeParams, struct int depth) {
	struct String stripped = input.strip();
	if (stripped.startsWith("else ")) {
		struct String withoutKeyword = stripped.substring("else ".length()).strip();
		if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
			struct String indent = createIndent(depth);
			return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrap(__lambda70__)).findValue().map(__lambda71__);
		}
		else {
			return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(__lambda72__);
		}
	}
	return None_();
}
Option_struct String compileKeywordStatement(struct String input, struct int depth, struct String keyword) {
	if (input.strip().equals(keyword + ";")) {
		return Some_(formatStatement(depth, keyword));
	}
	else {
		return None_();
	}
}
struct String formatStatement(struct int depth, struct String value) {
	return createIndent(depth) + value + ";";
}
struct String createIndent(struct int depth) {
	return "\n" + "\t".repeat(depth);
}
auto __lambda73__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth + 1);
}
auto __lambda74__(auto statements) {
		return withCondition + " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
}
auto __lambda75__(auto result) {
		return withCondition + " " + result;
}
auto __lambda76__(auto newCondition) {
	struct String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";
	if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
		struct String content = withBraces.substring(1, withBraces.length() - 1);
		return compileStatements(content, wrap(__lambda73__)).findValue().map(__lambda74__);
	}
	else {
		return compileStatementOrBlock(withBraces, typeParams, depth).map(__lambda75__);
	}
}
Option_struct String compileConditional(struct String input, List__struct String typeParams, struct String prefix, struct int depth) {
	struct String stripped = input.strip();
	if (!stripped.startsWith(prefix)) {
		return None_();
	}
	struct String afterKeyword = stripped.substring(prefix.length()).strip();
	if (!afterKeyword.startsWith("(")) {
		return None_();
	}
	struct String withoutConditionStart = afterKeyword.substring(1);
	struct int conditionEnd = findConditionEnd(withoutConditionStart);
	if (conditionEnd < 0) {
		return None_();
	}
	struct String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
	struct String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();
	return compileValue(oldCondition, typeParams, depth).flatMap(__lambda76__);
}
struct int findConditionEnd(struct String input) {
	struct int conditionEnd = -1;
	struct int depth0 = 0;
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
Option_struct String compileInvocationStatement(struct String input, List__struct String typeParams, struct int depth) {
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
auto __lambda77__(auto newSource) {
			return newDest + " = " + newSource;
}
auto __lambda78__(auto newDest) {
			return compileValue(source, typeParams, depth).map(__lambda77__);
}
Option_struct String compileAssignment(struct String input, List__struct String typeParams, struct int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		struct int valueSeparator = withoutEnd.indexOf("=");
		if (valueSeparator >= 0) {
			struct String destination = withoutEnd.substring(0, valueSeparator).strip();
			struct String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
			return compileValue(destination, typeParams, depth).flatMap(__lambda78__);
		}
	}
	return None_();
}
auto __lambda79__(auto result) {
	return "return " + result;
}
Option_struct String compileReturn(struct String input, List__struct String typeParams, struct int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(";")) {
		struct String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
		if (withoutEnd.startsWith("return ")) {
			return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(__lambda79__);
		}
	}
	return None_();
}
auto __lambda80__(auto value) {
	return outputType + value;
}
auto __lambda81__(auto outputType) {
	return compileArgs(argsString, typeParams, depth).map(__lambda80__);
}
auto __lambda82__(auto result) {
	return "!" + result;
}
auto __lambda83__(auto compiled) {
			return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
}
auto __lambda84__(auto compiled) {
	return compiled + "." + property;
}
auto __lambda85__() {
	return compileOperator(input, typeParams, depth, "<");
}
auto __lambda86__() {
	return compileOperator(input, typeParams, depth, "+");
}
auto __lambda87__() {
	return compileOperator(input, typeParams, depth, ">=");
}
auto __lambda88__() {
	return compileOperator(input, typeParams, depth, "&&");
}
auto __lambda89__() {
	return compileOperator(input, typeParams, depth, "==");
}
auto __lambda90__() {
	return compileOperator(input, typeParams, depth, "!=");
}
auto __lambda91__() {
	return generatePlaceholder(input);
}
Option_struct String compileValue(struct String input, List__struct String typeParams, struct int depth) {
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
		struct int argsStart = slice.indexOf("(");
		if (argsStart >= 0) {
			struct String type = slice.substring(0, argsStart);
			struct String withEnd = slice.substring(argsStart + "(".length()).strip();
			if (withEnd.endsWith(")")) {
				struct String argsString = withEnd.substring(0, withEnd.length() - ")".length());
				return createTypeRule(typeParams).apply(type).findValue().flatMap(__lambda81__);
			}
		}
	}
	if (stripped.startsWith("!")) {
		return compileValue(stripped.substring(1), typeParams, depth).map(__lambda82__);
	}
	Option_struct String value = compileLambda(stripped, typeParams, depth);
	if (value.isPresent()) {
		return value;
	}
	Option_struct String invocation = compileInvocation(input, typeParams, depth);
	if (invocation.isPresent()) {
		return invocation;
	}
	struct int methodIndex = stripped.lastIndexOf("::");
	if (methodIndex >= 0) {
		struct String type = stripped.substring(0, methodIndex).strip();
		struct String property = stripped.substring(methodIndex + "::".length()).strip();
		if (isSymbol(property)) {
			return createTypeRule(typeParams).apply(type).findValue().flatMap(__lambda83__);
		}
	}
	struct int separator = input.lastIndexOf(".");
	if (separator >= 0) {
		struct String object = input.substring(0, separator).strip();
		struct String property = input.substring(separator + ".".length()).strip();
		return compileValue(object, typeParams, depth).map(__lambda84__);
	}
	return compileOperator(input, typeParams, depth, "||").or(__lambda85__).or(__lambda86__).or(__lambda87__).or(__lambda88__).or(__lambda89__).or(__lambda90__).or(__lambda91__);
}
auto __lambda92__(auto rightResult) {
	return leftResult + " " + operator + " " + rightResult;
}
auto __lambda93__(auto leftResult) {
	return compileValue(right, typeParams, depth).map(__lambda92__);
}
Option_struct String compileOperator(struct String input, List__struct String typeParams, struct int depth, struct String operator) {
	struct int operatorIndex = input.indexOf(operator);
	if (operatorIndex < 0) {
		return None_();
	}
	struct String left = input.substring(0, operatorIndex);
	struct String right = input.substring(operatorIndex + operator.length());
	return compileValue(left, typeParams, depth).flatMap(__lambda93__);
}
auto __lambda94__() {
	return struct String.strip()
}
auto __lambda95__(auto value) {
	return !value.isEmpty();
}
auto __lambda96__(auto statement) {
	return compileStatementOrBlock(statement, typeParams, depth);
}
auto __lambda97__(auto result) {
		return generateLambdaWithReturn(paramNames, result);
}
auto __lambda98__(auto newValue) {
	return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
}
Option_struct String compileLambda(struct String input, List__struct String typeParams, struct int depth) {
	struct int arrowIndex = input.indexOf("->");
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
		paramNames = splitByDelimiter(inner, ',').iter().map(__lambda94__).filter(__lambda95__).collect(ListCollector_());
	}
	else {
		return None_();
	}
	struct String value = input.substring(arrowIndex + "->".length()).strip();
	if (value.startsWith("{") && value.endsWith("}")) {
		struct String slice = value.substring(1, value.length() - 1);
		return compileStatements(slice, wrap(__lambda96__)).findValue().flatMap(__lambda97__);
	}
	return compileValue(value, typeParams, depth).flatMap(__lambda98__);
}
auto __lambda99__(auto name) {
	return "auto " + name;
}
Option_struct String generateLambdaWithReturn(List__struct String paramNames, struct String returnValue) {
	struct int current = counter;counter++;
	struct String lambdaName = "__lambda" + current + "__";
	struct String joinedLambdaParams = paramNames.iter().map(__lambda99__).collect(struct Joiner(", ")).orElse("");
	methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
	return Some_(lambdaName);
}
auto __lambda100__(auto tuple) {
	struct int index = tuple.left;
	struct char c = tuple.right;
	return (index == 0 && c == '-') || Character.isDigit(c);
}
struct boolean isNumber(struct String input) {
	return Iterators.fromStringWithIndices(input).allMatch(__lambda100__);
}
auto __lambda101__(auto value) {
	return caller + value;
}
auto __lambda102__(auto caller) {
			return compileArgs(withEnd, typeParams, depth).map(__lambda101__);
}
Option_struct String compileInvocation(struct String input, List__struct String typeParams, struct int depth) {
	struct String stripped = input.strip();
	if (stripped.endsWith(")")) {
		struct String sliced = stripped.substring(0, stripped.length() - ")".length());
		struct int argsStart = findInvocationStart(sliced);
		if (argsStart >= 0) {
			struct String type = sliced.substring(0, argsStart);
			struct String withEnd = sliced.substring(argsStart + "(".length()).strip();
			return compileValue(type, typeParams, depth).flatMap(__lambda102__);
		}
	}
	return None_();
}
struct int findInvocationStart(struct String sliced) {
	struct int argsStart = -1;
	struct int depth0 = 0;
	struct int i = sliced.length() - 1;
	while (i >= 0) {
		struct char c = sliced.charAt(i);
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
auto __lambda103__() {
	return compileValue(arg, typeParams, depth);
}
auto __lambda104__(auto arg) {
	return compileWhitespace(arg).or(__lambda103__);
}
auto __lambda105__(auto args) {
	return "(" + args + ")";
}
Option_struct String compileArgs(struct String argsString, List__struct String typeParams, struct int depth) {
	return compileValues(argsString, __lambda104__).map(__lambda105__);
}
struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {
	if (cache.isEmpty()) {
		return cache.append(element);
	}
	return cache.append(", ").append(element);
}
auto __lambda106__() {
	return struct String.strip()
}
auto __lambda107__(auto value) {
	return !value.isEmpty();
}
auto __lambda108__() {
	return struct Main.isSymbol()
}
auto __lambda109__(auto outputType) {
	return Some_(generateDefinition(typeParams, outputType, name));
}
auto __lambda110__(auto outputType) {
	return Some_(generateDefinition(Impl.emptyList(), outputType, name));
}
Option_struct String compileDefinition(struct String definition) {
	struct String stripped = definition.strip();
	struct int nameSeparator = stripped.lastIndexOf(" ");
	if (nameSeparator < 0) {
		return None_();
	}
	struct String beforeName = stripped.substring(0, nameSeparator).strip();
	struct String name = stripped.substring(nameSeparator + " ".length()).strip();
	if (!isSymbol(name)) {
		return None_();
	}
	struct int typeSeparator = findTypeSeparator(beforeName);
	if (typeSeparator >= 0) {
		struct String beforeType = beforeName.substring(0, typeSeparator).strip();
		struct String beforeTypeParams = beforeType;
	List__struct String typeParams;
		if (beforeType.endsWith(">")) {
			struct String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
			struct int typeParamStart = withoutEnd.indexOf("<");
			if (typeParamStart >= 0) {
				beforeTypeParams = withoutEnd.substring(0, typeParamStart);
				struct String substring = withoutEnd.substring(typeParamStart + 1);
				typeParams = splitValues(substring);
			}
			else {
				typeParams = Impl.emptyList();
			}
		}
		else {
			typeParams = Impl.emptyList();
		}
		struct String strippedBeforeTypeParams = beforeTypeParams.strip();
	struct String modifiersString;
		struct int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
		if (annotationSeparator >= 0) {
			modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
		}
		else {
			modifiersString = strippedBeforeTypeParams;
		}
		struct boolean allSymbols = splitByDelimiter(modifiersString, ' ').iter().map(__lambda106__).filter(__lambda107__).allMatch(__lambda108__);
		if (!allSymbols) {
			return None_();
		}
		struct String inputType = beforeName.substring(typeSeparator + " ".length());
		return createTypeRule(typeParams).apply(inputType).findValue().flatMap(__lambda109__);
	}
	else {
		return createTypeRule(Impl.emptyList()).apply(beforeName).findValue().flatMap(__lambda110__);
	}
}
struct int findTypeSeparator(struct String beforeName) {
	struct int typeSeparator = -1;
	struct int depth = 0;
	struct int i = beforeName.length() - 1;
	while (i >= 0) {
		struct char c = beforeName.charAt(i);
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
auto __lambda111__() {
	return struct String.strip()
}
auto __lambda112__(auto param) {
	return !param.isEmpty();
}
List__struct String splitValues(struct String substring) {
	return splitByDelimiter(substring.strip(), ',').iter().map(__lambda111__).filter(__lambda112__).collect(ListCollector_());
}
auto __lambda113__(auto inner) {
	return "<" + inner + "> ";
}
struct String generateDefinition(List__struct String maybeTypeParams, struct String type, struct String name) {
	struct String typeParamsString = maybeTypeParams.iter().collect(struct Joiner(", ")).map(__lambda113__).orElse("");
	return typeParamsString + type + " " + name;
}
Function_struct String, Result_struct String, struct CompileError createTypeRule(List__struct String typeParams) {
	return createOrRule(Impl.listOf(createPrimitiveRule(), createArrayRule(typeParams), createSymbolRule(typeParams), createGenericRule(typeParams)));
}
auto __lambda114__(auto input) {
	return compileGeneric(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createGenericRule(List__struct String typeParams) {
	return wrap(__lambda114__);
}
auto __lambda115__(auto input) {
	return compileSymbol(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createSymbolRule(List__struct String typeParams) {
	return wrap(__lambda115__);
}
auto __lambda116__(auto input) {
	return compileArray(input, typeParams);
}
Function_struct String, Result_struct String, struct CompileError createArrayRule(List__struct String typeParams) {
	return wrap(__lambda116__);
}
auto __lambda117__(auto input) {
	if (input.equals("void")) {
		return Some_("void");
	}
	if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
		return Some_("int");
	}
	if (input.equals("char") || input.equals("Character")) {
		return Some_("char");
	}
	return None_();
}
Function_struct String, Result_struct String, struct CompileError createPrimitiveRule() {
	return wrap(__lambda117__);
}
auto __lambda118__(auto value) {
	return value + "*";
}
Option_struct String compileArray(struct String input, List__struct String typeParams) {
	if (input.endsWith("[]")) {
		return createTypeRule(typeParams).apply(input.substring(0, input.length() - "[]".length())).findValue().map(__lambda118__);
	}
	return None_();
}
Option_struct String compileSymbol(struct String input, List__struct String typeParams) {
	if (isSymbol(input.strip())) {
		return Impl.contains(typeParams, input.strip(), String::equals) ? new Some<>(input.strip()) : new Some<>("struct " + input.strip());
	}
	return None_();
}
auto __lambda119__() {
	return createTypeRule(typeParams).apply(type).findValue();
}
auto __lambda120__(auto type) {
			return compileWhitespace(type).or(__lambda119__);
}
auto __lambda121__(auto compiled) {
			return base + "_" + compiled;
}
Option_struct String compileGeneric(struct String stripped, List__struct String typeParams) {
	if (stripped.endsWith(">")) {
		struct String slice = stripped.substring(0, stripped.length() - ">".length());
		struct int argsStart = slice.indexOf("<");
		if (argsStart >= 0) {
			struct String base = slice.substring(0, argsStart).strip();
			struct String params = slice.substring(argsStart + " < ".length()).strip();
			return compileValues(params, __lambda120__).map(__lambda121__);
		}
	}
	return None_();
}
auto __lambda122__(auto tuple) {
	struct int index = tuple.left;
	struct char c = tuple.right;
	return c == '_' || Character.isLetter(c) ||(index != 0 && Character.isDigit(c));
}
struct boolean isSymbol(struct String input) {
	if (input.isBlank()) {
		return false;
	}
	return Iterators.fromStringWithIndices(input).allMatch(__lambda122__);
}
Option_struct String generatePlaceholder(struct String input) {
	return Some_("/* " + input + " */");
}
