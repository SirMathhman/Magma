struct Result {
	<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
};
struct Option {
	<R> Option<R> map(Function<T, R> mapper);
	int isPresent();
	T orElse(T other);
	int isEmpty();
	void ifPresent(Consumer<T> consumer);
	Option<T> or(Supplier<Option<T>> other);
	<R> Option<R> flatMap(Function<T, Option<R>> mapper);
};
struct IOError {
	String display();
};
struct List_ {
	List_<T> add(T element);
	List_<T> addAll(List_<T> others);
	Iterator<T> iter();
	int isEmpty();
	T getFirst();
	int size();
	List_<T> slice(int startInclusive, int endExclusive);
	Option<Tuple<T, List_<T>>> popFirst();
	Option<T> peekFirst();
	T get(int index);
};
struct Path_ {
	Path_ resolveSibling(String sibling);
	List_<String> asList();
	Path_ resolveChild(String child);
};
struct Iterator {
	<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);
	void forEach(Consumer<T> consumer);
	<R> Iterator<R> map(Function<T, R> mapper);
	Iterator<T> filter(Predicate<T> predicate);
	Option<T> next();
	Iterator<T> concat(Iterator<T> other);
	<C> C collect(Collector<T, C> collector);
	int allMatch(Predicate<T> predicate);
	<R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder);
};
struct Collector {
	C createInitial();
	C fold(C current, T element);
};
struct Head {
	Option<T> next();
};
struct Divider {
	State fold(State state, char c);
};
struct HeadedIterator {
};
struct None {
};
struct EmptyHead {
};
struct SingleHead {
	T value;
};
struct Some {
};
struct Err {
};
struct Ok {
};
struct State {
	List_<char> queue;
	List_<String> segments;
	StringBuilder buffer;
	int depth;
};
struct Tuple {
};
struct Iterators {
};
struct RangeHead {
	int length;
};
struct ListCollector {
};
struct Joiner {
};
struct DecoratedDivider {
};
struct DelimitedDivider {
};
struct Main {
/* 
        Files.readString(source)
                .match(input -> compileAndWrite(source, input), Some::new)
                .ifPresent(error -> System.err.println(error.display())); */	Option<IOError> compileAndWrite(Path_ source, String input);
	List_<String> mergeStatics(List_<String> list);
	Option<String> compileStatements(String input, Function<String, Option<String>> compiler);
	Option<String> compileAndMerge(List_<String> segments, Function<String, Option<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger);
	String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger);
	Option<List_<String>> parseAll(List_<String> segments, Function<String, Option<String>> compiler);
	StringBuilder mergeStatements(StringBuilder output, String compiled);
	List_<String> divide(String input, Divider divider);
/* 
        while (state.hasElements()) {
            Option<Tuple<Character, State>> maybeNextTuple = state.pop();
            if (maybeNextTuple.isEmpty()) {
                break;
            }

            Tuple<Character, State> nextTuple = maybeNextTuple.orElse(new Tuple<>('\0''',', state));

            char c = nextTuple.left;
            state = nextTuple.right;

            State finalState = state;
            state = divider.fold(state, c);
        } *//* 

        return state.advance().segments(); *//* 
        if (maybeSlashTuple.isEmpty()) {
            return new None<>();
        } *//* 
        return withSlash.flatMap(State::popAndAppend); *//* 
        while (true) {
            Option<Tuple<Character, State>> maybeNextTuple = current.pop();
            if (maybeNextTuple.isEmpty()) {
                break;
            }

            Tuple<Character, State> nextTuple = maybeNextTuple.orElse(null);

            char next = nextTuple.left;
            current = nextTuple.right.append(next);

            if (next == '\\''')') {
                current = current.popAndAppend().orElse(current);
            }
            if (next == '"'"'""')') {
                break;
            }
        } *//* 

        return new Some<>(current); */	State divideStatementChar(State state, char c);
/* 
        if (c == ';';' ' && appended.isLevel()) {
            return appended.advance();
        } *//* 
        if (c == '}'} *//* ' ' && isShallow(appended)) {
            return appended.advance().exit(); */	/* } */ if(/* c == '{'{'{'{' ' ' ' || c == '('('('('' */);
	int isShallow(State state);
	/* } */ if(/* input.startsWith(""""package "" */);
/* 
        }

        String stripped = input.strip();
        if (stripped.startsWith("""import ")) {
            String right = stripped.substring("""import "".length()); *//* 
            if (right.endsWith(""";")) {
                String content = right.substring(0, right.length() - """;"".length());
                List_<String> split = divide(content, new DelimitedDivider('.'.')'));
                if (split.size() < 3 || !Lists.equals(split.slice(0, 3), FUNCTIONAL_NAMESPACE, String::equals)) {
                    String joined = split.iter().collect(new Joiner("""/")).orElse(""""");
                    imports.add("""#include \"./""" + joined + ""\"\n""");
                }

                return new Some<>(""""");
            }
         */};
int retrieved = false;
int counter = 0;
List_<String> FUNCTIONAL_NAMESPACE = Lists.of("java""", ""util""", "function""");
    private static final List_<String> imports = Lists.empty();
    private static final List_<String> structs = Lists.empty();
    private static final List_<String> globals = Lists.empty();
    private static final List_<String> methods = Lists.empty();
    private static int counter = 0;

    public static void main(String[] args) {
        Path_ source = Paths.get("""."""", ""src"""", """java""""", "magma""""", """Main.java""""");
State state = State(queue);
State appended = state.append(c);
Option<Tuple<char, State>> maybeSlashTuple = appended.pop();
Tuple<char, State> slashTuple = maybeSlashTuple.orElse(Tuple<>('\0''''''''''''''',', ',', ',', ',', appended));
var withMaybeSlash = slashTuple.right.append(slashTuple.left);
Option<State> withSlash = slashTuple.left == '\\''' ' ? withMaybeSlash.popAndAppend() : new None<>();
State current = state.append(c);
auto __lambda0__(auto next) {
	return /* > folder */.apply(finalCurrent;
}
<R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
	R current = initial;
	while (true) {
		R finalCurrent = current;
		Option<R> option = this.head.next().map(__lambda0__, /*  next) */);
		if (option.isPresent()) {
			current = option.orElse(finalCurrent);
		}
		else {
			return current;
		}
	}
}
void forEach(Consumer<T> consumer) {
	while (true) {
		Option<T> next = this.head.next();
		if (next.isEmpty()) {
			break;
		}
		next.ifPresent(consumer);
	}
}
auto __lambda1__ {
	return /* > this */.head.next().map(mapper);
}
<R> Iterator<R> map(Function<T, R> mapper) {
	return HeadedIterator<>(__lambda1__);
}
auto __lambda2__(auto value) {
	return /* > new HeadedIterator */ < /* > */(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>());
}
Iterator<T> filter(Predicate<T> predicate) {
	return this.flatMap(__lambda2__);
}
Option<T> next() {
	return this.head.next();
}
auto __lambda3__ {
	return other.next()
}
auto __lambda4__ {
	return /* > this */.head.next().or(__lambda3__);
}
Iterator<T> concat(Iterator<T> other) {
	return HeadedIterator<>(__lambda4__);
}
auto __lambda5__ {
	return collector.fold()
}
<C> C collect(Collector<T, C> collector) {
	return this.foldWithInitial(collector.createInitial(), __lambda5__);
}
auto __lambda6__(auto aBoolean, t) {
	return /* > aBoolean  */ && predicate.test(t);
}
int allMatch(Predicate<T> predicate) {
	return this.foldWithInitial(true, __lambda6__);
}
auto __lambda7__(auto next) {
	return /* > this */.foldWithInitial(next;
}
<R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder) {
	return this.head.next().map(mapper).map(__lambda7__, /*  folder) */);
}
auto __lambda8__ {
	return Iterator.concat()
}
<R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
	return this.map(mapper).foldWithInitial(Iterators.empty(), __lambda8__);
}
<R> Option<R> map(Function<T, R> mapper) {
	return None<>();
}
int isPresent() {
	return false;
}
T orElse(T other) {
	return other;
}
int isEmpty() {
	return true;
}
void ifPresent(Consumer<T> consumer) {
}
Option<T> or(Supplier<Option<T>> other) {
	return other.get();
}
<R> Option<R> flatMap(Function<T, Option<R>> mapper) {
	return None<>();
}
Option<T> next() {
	return None<>();
}
private SingleHead(T value) {
	this.value = value;
}
Option<T> next() {
	if (this.retrieved) {
		return None<>();
	}
	this.retrieved = true;
	return Some<>(this.value);
}
<R> Option<R> map(Function<T, R> mapper) {
	return Some<>(mapper.apply(this.value));
}
int isPresent() {
	return true;
}
T orElse(T other) {
	return this.value;
}
int isEmpty() {
	return false;
}
void ifPresent(Consumer<T> consumer) {
	consumer.accept(this.value);
}
Option<T> or(Supplier<Option<T>> other) {
	return this;
}
<R> Option<R> flatMap(Function<T, Option<R>> mapper) {
	return mapper.apply(this.value);
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenErr.apply(this.error);
}
<R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
	return whenOk.apply(this.value);
}
private State(List_<char> queue, List_<String> segments, StringBuilder buffer, int depth) {
	this.queue = queue;
	this.segments = segments;
	this.buffer = buffer;
	this.depth = depth;
}
public State(List_<char> queue) {
	this(queue, Lists.empty(), StringBuilder(), 0);
}
auto __lambda9__(auto tuple) {
	return /* > tuple */.right.append(tuple.left);
}
Option<State> popAndAppend() {
	return this.pop().map(__lambda9__);
}
State advance() {
	this.segments = this.segments.add(this.buffer.toString());
	this.buffer = StringBuilder();
	return this;
}
State append(char c) {
	this.buffer.append(c);
	return this;
}
int isLevel() {
	return this.depth == 0;
}
auto __lambda10__(auto tuple) {
	return /* > {
                return new Tuple */ < /* >(tuple */.left;
}
Option<Tuple<char, State>> pop() {
	return this.queue.popFirst().map(__lambda10__, /* new State(tuple */.right, this.segments, this.buffer, this.depth));
            });
}
int hasElements() {
	return !this.queue.isEmpty();
}
State exit() {
	this.depth = this.depth - 1;
	return this;
}
State enter() {
	this.depth = this.depth + 1;
	return this;
}
List_<String> segments() {
	return this.segments;
}
Option<char> peek() {
	return this.queue.peekFirst();
}
<T> Iterator<T> fromArray(T* array) {
	return HeadedIterator<>(RangeHead(array.length)).map(index ->> array[index]);
}
<T> Iterator<T> empty() {
	return HeadedIterator<>(EmptyHead<>());
}
<T> Iterator<T> from(/* T... */ array) {
	return HeadedIterator<>(RangeHead(array.length)).map(index ->> array[index]);
}
auto __lambda11__(auto tuple) {
	return /* > tuple */.right;
}
Iterator<char> fromString(String input) {
	return fromStringWithIndices(input).map(__lambda11__);
}
Iterator<Tuple<int, Character>> fromStringWithIndices(String input) {
	return HeadedIterator<>(RangeHead(input.length())).map(index ->> new Tuple<>(index, input.charAt(index)));
}
public RangeHead(int length) {
	this.length = length;
}
Option<int> next() {
	if (this.counter < this.length) {
		int next = this.counter;this.counter++;
		return Some<>(next);
	}
	return None<>();
}
List_<T> createInitial() {
	return Lists.empty();
}
List_<T> fold(List_<T> current, T element) {
	return current.add(element);
}
Option<String> createInitial() {
	return None<>();
}
auto __lambda12__(auto inner) {
	return /* >> inner  */ + this.delimiter + element;
}
Option<String> fold(Option<String> current, String element) {
	return Some<>(current.map(__lambda12__).orElse(element));
}
auto __lambda13__ {
	return /* > divideDoubleQuotes(state */;
}
State fold(State state, char c) {
	return divideSingleQuotes(state, c).or(__lambda13__, /*  c) */).orElse(this.divider().fold(state, c));
}
State fold(State state, char c) {
	if (c == this.delimiter) {
		return state.advance();
	}
	return state.append(c);
}
Option<State> divideSingleQuotes(State state, char c) {
	if (c != '\''''''') 
	/* ')')') {
            return new None */ < /* > */();
}
Option<State> divideDoubleQuotes(State state, char c) {
	/* if (c ! */ = /* '\"'''''')')')') {
            return new None */ < /* > */();
}
/* 

        Option<String> maybeClass = compileToStruct(input, "class "", Lists.empty()); *//* 
        if (maybeClass.isPresent()) {
            return maybeClass;
        } *//* 

        return generatePlaceholder(input); *//* 
    }

    private static Option<String> compileToStruct(String input, String infix, List_<String> typeParams) {
        int classIndex = input.indexOf(infix); *//* 
        if (classIndex < 0) {
            return new None<>();
        } *//* 

        String afterKeyword = input.substring(classIndex + infix.length()); *//* 
        int contentStart = afterKeyword.indexOf("{""); *//* 
        if (contentStart >= 0) {
            String beforeContent = afterKeyword.substring(0, contentStart).strip();
            int implementsIndex = beforeContent.indexOf(" implements "");

            String beforeImplements;
            if (implementsIndex >= 0) {
                beforeImplements = beforeContent.substring(0, implementsIndex);
            }
            else {
                beforeImplements = beforeContent;
            }
            String strippedBeforeImplements = beforeImplements.strip();

            String withoutParams;
            if (strippedBeforeImplements.endsWith(")"")) {
                String withoutEnd = strippedBeforeImplements.substring(0, strippedBeforeImplements.length() - ")"".length());
                int paramStart = withoutEnd.indexOf("("");
                if (paramStart >= 0) {
                    withoutParams = withoutEnd.substring(0, paramStart).strip();
                }
                else {
                    withoutParams = strippedBeforeImplements;
                }
            }
            else {
                withoutParams = strippedBeforeImplements;
            }

            String strippedWithoutParams = withoutParams.strip();

            String name;
            if (strippedWithoutParams.endsWith(">"")) {
                int genStart = strippedWithoutParams.indexOf("<"");
                if (genStart >= 0) {
                    name = strippedWithoutParams.substring(0, genStart).strip();
                }
                else {
                    name = strippedWithoutParams;
                }
            }
            else {
                name = strippedWithoutParams;
            }

            if (isSymbol(name)) {
                String withEnd = afterKeyword.substring(contentStart + "{"".length()).strip();
                if (withEnd.endsWith("}"")) {
                    String inputContent = withEnd.substring(0, withEnd.length() - "}"".length());
                    return compileStatements(inputContent, input1 -> compileClassMember(input1, typeParams)).map(outputContent -> {
                        structs.add("struct "" + name + " {\n"" + outputContent + "};\n"");
                        return """;
                    });
                }
            }
        } *//* 
        return new None<>(); *//* 
    }

    private static Option<String> compileClassMember(String input, List_<String> typeParams) {
        return compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface "", typeParams))
                .or(() -> compileToStruct(input, "record "", typeParams))
                .or(() -> compileToStruct(input, "class "", typeParams))
                .or(() -> compileGlobalInitialization(input, typeParams))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> compileMethod(input, typeParams))
                .or(() -> generatePlaceholder(input)); *//* 
    }

    private static Option<String> compileDefinitionStatement(String input) {
        String stripped = input.strip(); *//* 
        if (stripped.endsWith(";"")) {
            String content = stripped.substring(0, stripped.length() - ";"".length());
            return compileDefinition(content).map(result -> "\t"" + result + ";\n"");
        } *//* 
        return new None<>(); *//* 
    }

    private static Option<String> compileGlobalInitialization(String input, List_<String> typeParams) {
        return compileInitialization(input, typeParams, 0).map(generated -> {
            globals.add(generated + ";\n"");
            return """;
        }); *//* 
    }

    private static Option<String> compileInitialization(String input, List_<String> typeParams, int depth) {
        if (!input.endsWith(";"")) {
            return new None<>();
        } *//* 

        String withoutEnd = input.substring(0, input.length() - ";"".length()); *//* 
        int valueSeparator = withoutEnd.indexOf("=""); *//* 
        if (valueSeparator < 0) {
            return new None<>();
        } *//* 

        String definition = withoutEnd.substring(0, valueSeparator).strip(); *//* 
        String value = withoutEnd.substring(valueSeparator + "="".length()).strip(); *//* 
        return compileDefinition(definition).flatMap(outputDefinition -> compileValue(value, typeParams, depth).map(outputValue -> outputDefinition + " = "" + outputValue)); *//* 
    }

    private static Option<String> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>(""");
        } *//* 
        return new None<>(); *//* 
    }

    private static Option<String> compileMethod(String input, List_<String> typeParams) {
        int paramStart = input.indexOf("(""); *//* 
        if (paramStart < 0) {
            return new None<>();
        } *//* 

        String inputDefinition = input.substring(0, paramStart).strip(); *//* 
        String withParams = input.substring(paramStart + "("".length()); *//* 

        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")"");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, Main::compileParameter)
                    .flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")"".length()).strip()));
        }); *//* 
    }

    private static Option<String> assembleMethodBody(
            List_<String> typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t"".repeat(0) + definition + "("" + params + ")""; *//* 
        if (body.startsWith("{"") && body.endsWith("}"")) {
            String inputContent = body.substring("{"".length(), body.length() - "}"".length());
            return compileStatements(inputContent, input1 -> compileStatementOrBlock(input1, typeParams, 1)).flatMap(outputContent -> {
                methods.add(header + " {"" + outputContent + "\n}\n"");
                return new Some<>(""");
            });
        } *//* 

        return new Some<>("\t"" + header + ";\n""); *//* 
    }

    private static Option<String> compileParameter(String definition) {
        return compileWhitespace(definition)
                .or(() -> compileDefinition(definition))
                .or(() -> generatePlaceholder(definition)); *//* 
    }

    private static Option<String> compileValues(String input, Function<String, Option<String>> compiler) {
        List_<String> divided = divide(input, new DecoratedDivider(Main::divideValueChar)); *//* 
        return compileValues(divided, compiler); *//* 
    }

    private static State divideValueChar(State state, char c) {
        if (c == '-'-')') {
            if (state.peek().orElse('\0'') == '>'>')') {
                state.pop();
                return state.append('-'-')').append('>'>')');
            }
        } *//* 

        if (c == ',',' ' && state.isLevel()) {
            return state.advance();
        } *//* 

        State appended = state.append(c); *//* 
        if (c == '<'<' ' || c == '('(')') {
            return appended.enter();
        }
        if (c == '>'>' ' || c == ')')')') {
            return appended.exit();
        } *//* 
        return appended; *//* 
    }

    private static Option<String> compileValues(List_<String> params, Function<String, Option<String>> compiler) {
        return compileAndMerge(params, compiler, Main::mergeValues); *//* 
    }

    private static Option<String> compileStatementOrBlock(String input, List_<String> typeParams, int depth) {
        return compileWhitespace(input)
                .or(() -> compileKeywordStatement(input, depth, "continue""))
                .or(() -> compileKeywordStatement(input, depth, "break""))
                .or(() -> compileConditional(input, typeParams, "if "", depth))
                .or(() -> compileConditional(input, typeParams, "while "", depth))
                .or(() -> compileElse(input, typeParams, depth))
                .or(() -> compilePostOperator(input, typeParams, depth, "++""))
                .or(() -> compilePostOperator(input, typeParams, depth, "--""))
                .or(() -> compileReturn(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileInitialization(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileAssignment(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileInvocationStatement(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> generatePlaceholder(input)); *//* 
    }

    private static Option<String> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
        String stripped = input.strip(); *//* 
        if (stripped.endsWith(operator + ";"")) {
            String slice = stripped.substring(0, stripped.length() - (operator + ";"").length());
            return compileValue(slice, typeParams, depth).map(value -> value + operator + ";"");
        } *//* 
        else {
            return new None<>();
        } *//* 
    }

    private static Option<String> compileElse(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip(); *//* 
        if (stripped.startsWith("else "")) {
            String withoutKeyword = stripped.substring("else "".length()).strip();
            if (withoutKeyword.startsWith("{"") && withoutKeyword.endsWith("}"")) {
                String indent = createIndent(depth);
                return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1),
                        statement -> compileStatementOrBlock(statement, typeParams, depth + 1))
                        .map(result -> indent + "else {"" + result + indent + "}"");
            }
            else {
                return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(result -> "else "" + result);
            }
        } *//* 

        return new None<>(); *//* 
    }

    private static Option<String> compileKeywordStatement(String input, int depth, String keyword) {
        if (input.strip().equals(keyword + ";"")) {
            return new Some<>(formatStatement(depth, keyword));
        } *//* 
        else {
            return new None<>();
        } *//* 
    }

    private static String formatStatement(int depth, String value) {
        return createIndent(depth) + value + ";""; *//* 
    }

    private static String createIndent(int depth) {
        return "\n"" + "\t"".repeat(depth); *//* 
    }

    private static Option<String> compileConditional(String input, List_<String> typeParams, String prefix, int depth) {
        String stripped = input.strip(); *//* 
        if (!stripped.startsWith(prefix)) {
            return new None<>();
        } *//* 

        String afterKeyword = stripped.substring(prefix.length()).strip(); *//* 
        if (!afterKeyword.startsWith("("")) {
            return new None<>();
        } *//* 

        String withoutConditionStart = afterKeyword.substring(1); *//* 
        int conditionEnd = findConditionEnd(withoutConditionStart); *//* 

        if (conditionEnd < 0) {
            return new None<>();
        } *//* 
        String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip(); *//* 
        String withBraces = withoutConditionStart.substring(conditionEnd + ")"".length()).strip(); *//* 

        return compileValue(oldCondition, typeParams, depth).flatMap(newCondition -> {
            String withCondition = createIndent(depth) + prefix + "("" + newCondition + ")"";

            if (withBraces.startsWith("{"") && withBraces.endsWith("}"")) {
                String content = withBraces.substring(1, withBraces.length() - 1);
                return compileStatements(content, statement -> compileStatementOrBlock(statement, typeParams, depth + 1)).map(statements -> withCondition +
                        " {"" + statements + "\n"" +
                        "\t"".repeat(depth) +
                        "}"");
            }
            else {
                return compileStatementOrBlock(withBraces, typeParams, depth).map(result -> withCondition + " "" + result);
            }
        }); *//* 

    }

    private static int findConditionEnd(String input) {
        int conditionEnd = -1; *//* 
        int depth0 = 0; *//* 

        List_<Tuple<Integer, Character>> queue = Iterators.fromStringWithIndices(input).collect(new ListCollector<>()); *//* 

        while (!queue.isEmpty()) {
            Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>> tupleListTuple = queue.popFirst().orElse(null);
            Tuple<Integer, Character> pair = tupleListTuple.left;
            queue = tupleListTuple.right;

            Integer i = pair.left;
            Character c = pair.right;

            if (c == '\''') {
                if (queue.popFirst().orElse(null).left.right == '\\'') {
                    queue.popFirst().orElse(null);
                }

                queue.popFirst().orElse(null);
                continue;
            }

            if (c == '"'"') {
                while (!queue.isEmpty()) {
                    Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>> tupleListTuple1 = queue.popFirst().orElse(null);
                    Tuple<Integer, Character> next = tupleListTuple1.left;
                    queue = tupleListTuple1.right;

                    if (next.right == '\\') {
                        queue.popFirst().orElse(null);
                    }
                    if (next.right == '""')') {
                        break;
                    }
                } *//* 

                continue; *//* 
            }

            if (c == ')')' ' && depth0 == 0) {
                conditionEnd = i;
                break;
            }

            if (c == '('(')') {
                depth0++; *//* 
            }
            if (c == ')')')') {
                depth0--;
            }
        }
        return conditionEnd;
    }

    private static Option<String> compileInvocationStatement(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";"")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";"".length());
            Option<String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
            if (maybeInvocation.isPresent()) {
                return maybeInvocation;
            }
        }
        return new None<>();
    }

    private static Option<String> compileAssignment(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";"")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";"".length());
            int valueSeparator = withoutEnd.indexOf("="");
            if (valueSeparator >= 0) {
                String destination = withoutEnd.substring(0, valueSeparator).strip();
                String source = withoutEnd.substring(valueSeparator + "="".length()).strip();
                return compileValue(destination, typeParams, depth).flatMap(newDest -> compileValue(source, typeParams, depth).map(newSource -> newDest + " = "" + newSource));
            }
        }
        return new None<>();
    }

    private static Option<String> compileReturn(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";"")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";"".length());
            if (withoutEnd.startsWith("return "")) {
                return compileValue(withoutEnd.substring("return "".length()), typeParams, depth).map(result -> "return "" + result);
            }
        }

        return new None<>();
    }

    private static Option<String> compileValue(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("\""") && stripped.endsWith("\""")) {
            return new Some<>(stripped);
        }
        if (stripped.startsWith("'"") && stripped.endsWith("'"")) {
            return new Some<>(stripped);
        }

        if (isSymbol(stripped) || isNumber(stripped)) {
            return new Some<>(stripped);
        }

        if (stripped.startsWith("new "")) {
            String slice = stripped.substring("new "".length());
            int argsStart = slice.indexOf("("");
            if (argsStart >= 0) {
                String type = slice.substring(0, argsStart);
                String withEnd = slice.substring(argsStart + "("".length()).strip();
                if (withEnd.endsWith(")"")) {
                    String argsString = withEnd.substring(0, withEnd.length() - ")"".length()); *//* 
                    return compileType(type, typeParams).flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value)); *//* 
                }
            }
        }

        if (stripped.startsWith("!"")) {
            return compileValue(stripped.substring(1), typeParams, depth).map(result -> "!"" + result);
        }

        Option<String> value = compileLambda(stripped, typeParams, depth);
        if (value.isPresent()) {
            return value;
        }

        Option<String> invocation = compileInvocation(input, typeParams, depth);
        if (invocation.isPresent()) {
            return invocation;
        }

        int methodIndex = stripped.lastIndexOf("::"");
        if (methodIndex >= 0) {
            String type = stripped.substring(0, methodIndex).strip();
            String property = stripped.substring(methodIndex + "::"".length()).strip();

            if (isSymbol(property)) {
                return compileType(type, typeParams).flatMap(compiled -> generateLambdaWithReturn(Lists.empty(), "\n\treturn "" + compiled + "."" + property + "()""));
            }
        }

        int separator = input.lastIndexOf("."");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + "."".length()).strip();
            return compileValue(object, typeParams, depth).map(compiled -> compiled + "."" + property);
        }

        return compileOperator(input, typeParams, depth, "||"")
                .or(() -> compileOperator(input, typeParams, depth, "<""))
                .or(() -> compileOperator(input, typeParams, depth, "+""))
                .or(() -> compileOperator(input, typeParams, depth, ">=""))
                .or(() -> compileOperator(input, typeParams, depth, "&&""))
                .or(() -> compileOperator(input, typeParams, depth, "==""))
                .or(() -> compileOperator(input, typeParams, depth, "!=""))
                .or(() -> generatePlaceholder(input));
    }

    private static Option<String> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
        int operatorIndex = input.indexOf(operator);
        if (operatorIndex < 0) {
            return new None<>();
        }

        String left = input.substring(0, operatorIndex);
        String right = input.substring(operatorIndex + operator.length());

        return compileValue(left, typeParams, depth).flatMap(leftResult -> compileValue(right, typeParams, depth).map(rightResult -> leftResult + " "" + operator + " "" + rightResult));
    }

    private static Option<String> compileLambda(String input, List_<String> typeParams, int depth) {
        int arrowIndex = input.indexOf("->"");
        if (arrowIndex < 0) {
            return new None<>();
        }

        String beforeArrow = input.substring(0, arrowIndex).strip();
        List_<String> paramNames;
        if (isSymbol(beforeArrow)) {
            paramNames = Lists.of(beforeArrow);
        }
        else if (beforeArrow.startsWith("("") && beforeArrow.endsWith(")"")) {
            String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
            paramNames = divide(inner, new DelimitedDivider('.'.')'))
                    .iter()
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .collect(new ListCollector<>());
        }
        else {
            return new None<>();
        }

        String value = input.substring(arrowIndex + "->"".length()).strip();
        if (value.startsWith("{"") && value.endsWith("}"")) {
            String slice = value.substring(1, value.length() - 1);
            return compileStatements(slice, statement -> compileStatementOrBlock(statement, typeParams, depth)).flatMap(result -> generateLambdaWithReturn(paramNames, result));
        }

        return compileValue(value, typeParams, depth).flatMap(newValue -> generateLambdaWithReturn(paramNames, "\n\treturn "" + newValue + ";""));
    }

    private static Option<String> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
        int current = counter;
        counter++;
        String lambdaName = "__lambda"" + current + "__"";

        String joined = paramNames.iter()
                .map(name -> "auto "" + name)
                .collect(new Joiner(", ""))
                .map(value -> "("" + value + ")"")
                .orElse(""");

        methods.add("auto "" + lambdaName + joined + " {"" + returnValue + "\n}\n"");
        return new Some<>(lambdaName);
    }

    private static boolean isNumber(String input) {
        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            int index = tuple.left;
            char c = tuple.right;
            return (index == 0 && c == '-'-')') || Character.isDigit(c);
        });
    }

    private static Option<String> compileInvocation(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(")"")) {
            String sliced = stripped.substring(0, stripped.length() - ")"".length());

            int argsStart = findInvocationStart(sliced);

            if (argsStart >= 0) {
                String type = sliced.substring(0, argsStart);
                String withEnd = sliced.substring(argsStart + "("".length()).strip();
                return compileValue(type, typeParams, depth).flatMap(caller -> compileArgs(withEnd, typeParams, depth).map(value -> caller + value));
            }
        }
        return new None<>();
    }

    private static int findInvocationStart(String sliced) {
        int argsStart = -1;
        int depth0 = 0;
        int i = sliced.length() - 1;
        while (i >= 0) {
            char c = sliced.charAt(i);
            if (c == '('(' ' && depth0 == 0) {
                argsStart = i; *//* 
                break; *//* 
            }

            if (c == ')')')') {
                depth0++;
            }
            if (c == '('(')') {
                depth0--; *//* 
            }
            i--;
        }
        return argsStart;
    }

    private static Option<String> compileArgs(String argsString, List_<String> typeParams, int depth) {
        return compileValues(argsString, arg -> compileWhitespace(arg).or(() -> compileValue(arg, typeParams, depth))).map(args -> "("" + args + ")"");
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", "").append(element);
    }

    private static Option<String> compileDefinition(String definition) {
        String stripped = definition.strip();
        int nameSeparator = stripped.lastIndexOf(" "");
        if (nameSeparator < 0) {
            return new None<>();
        }

        String beforeName = stripped.substring(0, nameSeparator).strip();
        String name = stripped.substring(nameSeparator + " "".length()).strip();
        if (!isSymbol(name)) {
            return new None<>();
        }

        int typeSeparator = -1;
        int depth = 0;
        int i = beforeName.length() - 1;
        while (i >= 0) {
            char c = beforeName.charAt(i);
            if (c == ' ' ' ' && depth == 0) {
                typeSeparator = i; *//* 
                break; *//* 
            }
            else {
                if (c == '>'>')') {
                    depth++;
                } *//* 
                if (c == '<'<')') {
                    depth--;
                } *//* 
            }
            i--;
        }

        if (typeSeparator >= 0) {
            String beforeType = beforeName.substring(0, typeSeparator).strip();

            String beforeTypeParams = beforeType;
            List_<String> typeParams;
            if (beforeType.endsWith(">"")) {
                String withoutEnd = beforeType.substring(0, beforeType.length() - ">"".length()); *//* 
                int typeParamStart = withoutEnd.indexOf("<""); *//* 
                if (typeParamStart >= 0) {
                    beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                    String substring = withoutEnd.substring(typeParamStart + 1);
                    typeParams = splitValues(substring);
                } *//* 
                else {
                    typeParams = Lists.empty();
                } *//* 
            }
            else {
                typeParams = Lists.empty(); *//* 
            }

            String strippedBeforeTypeParams = beforeTypeParams.strip();

            String modifiersString;
            int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n"");
            if (annotationSeparator >= 0) {
                modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n"".length()); *//* 
            }
            else {
                modifiersString = strippedBeforeTypeParams; *//* 
            }

            boolean allSymbols = divide(modifiersString, new DelimitedDivider(' ' ')'))
                    .iter()
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .allMatch(Main::isSymbol);

            if (!allSymbols) {
                return new None<>(); *//* 
            }

            String inputType = beforeName.substring(typeSeparator + " "".length());
            return compileType(inputType, typeParams).flatMap(outputType -> new Some<>(generateDefinition(typeParams, outputType, name)));
        }
        else {
            return compileType(beforeName, Lists.empty()).flatMap(outputType -> new Some<>(generateDefinition(Lists.empty(), outputType, name)));
        }
    }

    private static List_<String> splitValues(String substring) {
        String stripped = substring.strip();
        return divide(stripped, new DelimitedDivider(',',')'))
                .iter()
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateDefinition(List_<String> maybeTypeParams, String type, String name) {
        return generateTypeParams(maybeTypeParams) + type + " "" + name;
    }

    private static String generateTypeParams(List_<String> maybeTypeParams) {
        if (maybeTypeParams.isEmpty()) {
            return """;
        }
        return maybeTypeParams.iter()
                .collect(new Joiner(", ""))
                .map(result -> "<"" + result + "> "")
                .orElse(""");
    }

    private static Option<String> compileType(String input, List_<String> typeParams) {
        if (input.equals("void"")) {
            return new Some<>("void"");
        }

        if (input.equals("int"") || input.equals("Integer"") || input.equals("boolean"") || input.equals("Boolean"")) {
            return new Some<>("int"");
        }

        if (input.equals("char"") || input.equals("Character"")) {
            return new Some<>("char"");
        }

        if (input.endsWith("[]"")) {
            return compileType(input.substring(0, input.length() - "[]"".length()), typeParams)
                    .map(value -> value + "*"");
        }

        String stripped = input.strip();
        if (isSymbol(stripped)) {
            if (Lists.contains(typeParams, stripped, String::equals)) {
                return new Some<>(stripped); *//* 
            }
            else {
                return new Some<>("struct "" + stripped); *//* 
            }
        }

        if (stripped.endsWith(">"")) {
            String slice = stripped.substring(0, stripped.length() - ">"".length());
            int argsStart = slice.indexOf("<"");
            if (argsStart >= 0) {
                String base = slice.substring(0, argsStart).strip(); *//* 
                String params = slice.substring(argsStart + "<"".length()).strip(); *//* 
                return compileValues(params, type -> compileWhitespace(type).or(() -> compileType(type, typeParams)))
                        .map(compiled -> base + "<"" + compiled + ">""); *//* 
            }
        }

        return generatePlaceholder(input);
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
            return false;
        }

        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            Integer index = tuple.left; *//* 
            char c = tuple.right; *//* 
            return c == '_'_' ' || Character.isLetter(c) || (index != 0 && Character.isDigit(c)); *//* 
        });
    }

    private static Option<String> generatePlaceholder(String input) {
        return new Some<>("/* "" + input + " */"");
    }
}
 */