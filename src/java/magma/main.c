/* import java.io.IOException; */
/* import java.nio.file.Files; */
/* import java.nio.file.Path; */
/* import java.nio.file.Paths; */
/* import java.util.Arrays; */
/* import java.util.Deque; */
/* import java.util.HashMap; */
/* import java.util.LinkedList; */
/* import java.util.Map; */
/* import java.util.function.BiFunction; */
/* import java.util.function.Consumer; */
/* import java.util.function.Function; */
/* import java.util.function.Supplier; */
/* import java.util.stream.Collectors; */
/* import java.util.stream.IntStream; */
/*  */
typedef struct {/* private final Deque<Character> queue; */
/* private final List_<String> segments; */
/* private String buffer; */
/* private int depth; */
/* private State(Deque<Character> queue, List_<String> segments, String buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        } */
/* public State(Deque<Character> queue) {
            this(queue, Lists.empty(), "", 0);
        } */
/* private State exit() {
            this.depth = this.depth - 1;
            return this;
        } */
/* private State enter() {
            this.depth = this.depth + 1;
            return this;
        } */
/* private State advance() {
            this.segments.add(this.buffer);
            this.buffer = "";
            return this;
        } */
/* private State append(char c) {
            this.buffer = this.buffer + c;
            return this;
        } */
/*  */

} State;
typedef struct {/*  */

} Joiner;
typedef struct {/* private final Map<String, String> strings; */
/* private Node() {
            this(new HashMap<>());
        } */
/* private Node(Map<String, String> strings) {
            this.strings = strings;
        } */
/* private Node withString(String propertyKey, String propertyValue) {
            this.strings.put(propertyKey, propertyValue);
            return this;
        } */
/*  */

} Node;
typedef struct {/* private static final Map<String, Function<List_<String>, Option<String>>> expandables = new HashMap<>(); */
/* private static final List_<Tuple<String, List_<String>>> visited = Lists.empty(); */
/* private static final List_<String> structs = Lists.empty(); */
/* private static final Map<String, String> methods = new HashMap<>(); */
/* private static List_<Tuple<String, List_<String>>> toExpand = Lists.empty(); */
/* private static String compile(String input) {
        List_<String> segments = divideAll(input, Main::foldStatementChar);
        return compileAll(segments, s -> new Some<>(compileRootSegment(s)))
                .map(Main::assemble)
                .map(compiled -> mergeAll(compiled, Main::mergeStatements)).orElse("");
    } */
/* private static List_<String> assemble(List_<String> compiled) {
        assembleGenerics(compiled);
        compiled.addAll(structs);
        compiled.addAll(Lists.fromNativeCollection(methods.values()));
        return compiled;
    } */
/* private static Option<List_<String>> compileAll(List_<String> segments, Function<String, Option<String>> compiler) {
        Option<List_<String>> maybeCompiled = new Some<>(Lists.empty());
        return segments.iter().fold(maybeCompiled, (current, element) -> current.flatMap(output -> {
            return compiler.apply(element).map(compiled -> {
                output.add(compiled);
                return output;
            });
        }));
    } */
/* private static List_<String> divideAll(String input, BiFunction<State, Character, State> folder) {
        LinkedList<Character> queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        State current = new State(queue);
        while (current.hasNext()) {
            char c = current.pop();

            State finalCurrent = current;
            current = foldSingleQuotes(current, c)
                    .or(() -> foldDoubleQuotes(finalCurrent, c))
                    .orElseGet(() -> folder.apply(finalCurrent, c));
        }

        return current.advance().segments;
    } */
/* private static Option<State> foldSingleQuotes(State current, char c) {
        if (c != '\'') {
            return new None<>();
        }

        State current1 = current.append(c);
        char popped = current1.pop();
        State appended = current1.append(popped);
        State state = popped == '\\' ? appended.append(appended.pop()) : appended;
        return new Some<>(state.append(state.pop()));
    } */
/* private static State foldStatementChar(State state, char c) {
        State appended = state.append(c);
        if (c == ';' && state.isLevel()) {
            return appended.advance();
        }
        if (c == '}' && state.isShallow()) {
            return state.advance().exit();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended;
    } */
/* private static Option<String> compileToStruct(String input, String infix) {
        int classIndex = input.indexOf(infix);
        if (classIndex < 0) {
            return new None<>();
        }
        boolean beforeKeyword = Arrays.stream(input.substring(0, classIndex).split(" "))
                .map(String::strip)
                .filter(modifier -> !modifier.isEmpty())
                .allMatch(Main::isSymbol);

        if (!beforeKeyword) {
            return new None<>();
        }

        String afterKeyword = input.substring(classIndex + infix.length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }
        String beforeContent = afterKeyword.substring(0, contentStart).strip();

        String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());

        int permitsIndex = beforeContent.lastIndexOf(" permits ");
        String withoutPermits = permitsIndex >= 0
                ? beforeContent.substring(0, permitsIndex).strip()
                : beforeContent;

        int paramStart = withoutPermits.indexOf("(");
        String withoutParams = paramStart >= 0
                ? withoutPermits.substring(0, paramStart).strip()
                : withoutPermits;

        int typeParamStart = withoutParams.indexOf("<");
        if (typeParamStart >= 0) {
            var name = withoutParams.substring(0, typeParamStart).strip();
            String withTypeEnd = withoutParams.substring(typeParamStart + "<".length()).strip();
            if (withTypeEnd.endsWith(">")) {
                String inputTypeParams = withTypeEnd.substring(0, withTypeEnd.length() - ">".length());
                List_<String> outputTypeParams = divideValues(inputTypeParams)
                        .iter()
                        .map(String::strip)
                        .collect(new ListCollector<>());

                expandables.put(name, typeArguments -> assembleStruct(name, inputContent, outputTypeParams, typeArguments));
                return new Some<>("");
            }
        }

        if (isSymbol(withoutParams)) {
            return assembleStruct(withoutParams, inputContent, Lists.empty(), Lists.empty());
        }

        return new None<>();
    } */
/* private static Option<String> assembleStruct(String name, String inputContent, List_<String> typeParams, List_<String> typeArguments) {
        String realName = typeArguments.isEmpty() ? name : stringify(name, typeArguments);

        String outputContent = compileStatements(inputContent, definition -> new Some<>(compileClassSegment(definition, realName, typeParams, typeArguments))).orElse("");
        String struct = "typedef struct {" +
                outputContent +
                "\n} " +
                realName +
                ";\n";

        structs.add(struct);
        return new Some<>("");
    } */
/* private static boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '_' || Character.isLetter(c)) {
                continue;
            }
            return false;
        }
        return true;
    } */
/* private static Option<String> compileMethod(String input, String structName, List_<String> typeParams, List_<String> typeArguments) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + 1);

        return compileDefinition(inputDefinition, Lists.of(structName), typeParams, typeArguments).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                String inputParams = withParams.substring(0, paramEnd).strip();
                String withEnd = withParams.substring(paramEnd + ")".length()).strip();

                Option<String> maybeOutputParams;
                maybeOutputParams = inputParams.isEmpty() ? new Some<>("") : compileValues(inputParams, definition -> compileDefinition(definition, Lists.empty(), typeParams, typeArguments));

                return maybeOutputParams.flatMap(outputParams -> {
                    if (withEnd.startsWith("{") && withEnd.endsWith("}")) {
                        String inputContent = withEnd.substring("{".length(), withEnd.length() - "}".length());
                        return compileStatements(inputContent, Main::compileStatementOrBlock).map(outputContent -> {
                            String value = outputDefinition + "(" +
                                    outputParams +
                                    "){" + outputContent + "\n}\n";

                            //methods.add(value);
                            return "";
                        });
                    }
                    else {
                        return new None<>();
                    }
                });
            }
            else {
                return new None<>();
            }
        });
    } */
/* private static Option<String> compileStatementOrBlock(String input) {
        String stripped = input.strip();
        if (stripped.isEmpty()) {
            return new Some<>("");
        }

        if (stripped.endsWith(";")) {
            String withEnd = stripped.substring(0, stripped.length() - ";".length()).strip();
            return compileStatement(withEnd).map(generated -> generated + ";");
        }

        return new Some<>(generatePlaceholder(stripped));
    } */
/* private static Option<String> compileValue(String input) {
        String stripped = input.strip();
        if (isSymbol(stripped)) {
            return new Some<>(stripped);
        }

        if (stripped.endsWith(")")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            int argStart = withoutEnd.indexOf("(");
            if (argStart >= 0) {
                String inputCaller = withoutEnd.substring(0, argStart).strip();
                return compileValue(inputCaller).map(outputCaller -> outputCaller + "()");
            }
        }

        int operatorIndex = stripped.indexOf("+");
        if (operatorIndex >= 0) {
            String left = stripped.substring(0, operatorIndex);
            String right = stripped.substring(operatorIndex + "+".length());

            return compileValue(left).and(() -> compileValue(right)).map(tuple -> {
                return tuple.left + " + " + tuple.right;
            });
        }

        return new Some<>(generatePlaceholder(stripped));
    } */
/* private static State foldValueChar(State state, char c) {
        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        State appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    } */
/* private static Option<String> compileDefinition(String definition, List_<String> stack, List_<String> typeParams, List_<String> typeArguments) {
        int nameSeparator = definition.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        String beforeName = definition.substring(0, nameSeparator).strip();
        String oldName = definition.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(oldName)) {
            return new None<>();
        }

        String newName;
        if (oldName.equals("main")) {
            newName = "__main__";
        }
        else {
            String joined = stack.iter()
                    .collect(new Joiner("_"))
                    .map(value -> value + "_")
                    .orElse("");

            newName = joined + oldName;
        }

        int typeSeparator = findTypeSeparator(beforeName);
        String inputType;
        if (typeSeparator >= 0) {
            String beforeType = beforeName.substring(0, typeSeparator).strip();
            if (beforeType.endsWith(">")) {
                return new None<>();
            }

            inputType = beforeName.substring(typeSeparator + " ".length()).strip();
        }
        else {
            inputType = beforeName;
        }

        return compileType(inputType, new Some<>(newName), typeParams, typeArguments);
    } */
/* private static int findTypeSeparator(String input) {
        int depth = 0;
        for (int i = input.length() - 1; i >= 0; i--) {
            char c = input.charAt(i);
            if (c == ' ' && depth == 0) {
                return i;
            }

            if (c == '>') {
                depth++;
            }
            if (c == '<') {
                depth--;
            }
        }
        return -1;
    } */
/* private static Option<String> compileType(String input, Option<String> maybeName, List_<String> typeParams, List_<String> typeArguments) {
        String stripped = input.strip();
        int index = typeParams.indexOf(stripped);
        Node withName = maybeName.map(name -> new Node().withString("name", name))
                .orElse(new Node());

        if (index >= 0) {
            String type = typeArguments.get(index);
            return new Some<>(generateSimpleDefinition(withName.withString("type", type)));
        }

        if (stripped.equals("new") || stripped.equals("private") || stripped.equals("public")) {
            return new None<>();
        }

        if (stripped.equals("void")) {
            return new Some<>(generateSimpleDefinition(withName.withString("type", "void")));
        }

        if (stripped.equals("char") || stripped.equals("Character")) {
            return new Some<>(generateSimpleDefinition(withName.withString("type", "char")));
        }

        if (stripped.equals("int") || stripped.equals("Integer") || stripped.equals("boolean") || stripped.equals("Boolean")) {
            return new Some<>(generateSimpleDefinition(withName.withString("type", "int")));
        }

        if (stripped.equals("String")) {
            return new Some<>(generateSimpleDefinition(withName.withString("type", "char*")));
        }

        if (stripped.endsWith("[]")) {
            return compileType(stripped.substring(0, stripped.length() - "[]".length()), new None<>(), typeParams, typeArguments)
                    .map(value -> {
                        return generateSimpleDefinition(withName.withString("type", value + "*"));
                    });
        }

        if (stripped.endsWith(">")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = withoutEnd.indexOf("<");
            if (argsStart >= 0) {
                String base = withoutEnd.substring(0, argsStart).strip();
                if (isSymbol(base)) {
                    String inputArgs = withoutEnd.substring(argsStart + "<".length());
                    List_<String> segments = divideValues(inputArgs);
                    return compileAll(segments, arg -> compileType(arg, new None<>(), typeParams, typeArguments)).map(arguments -> {
                        if (base.equals("Supplier")) {
                            return generateFunctionalDefinition(maybeName, Lists.of(), arguments.get(0));
                        }

                        if (base.equals("Function")) {
                            return generateFunctionalDefinition(maybeName, Lists.of(arguments.get(0)), arguments.get(1));
                        }

                        if (base.equals("BiFunction")) {
                            return generateFunctionalDefinition(maybeName, Lists.of(arguments.get(0), arguments.get(1)), arguments.get(2));
                        }

                        if (base.equals("Consumer")) {
                            return generateFunctionalDefinition(maybeName, Lists.of(arguments.get(0)), "void");
                        }

                        Tuple<String, List_<String>> entry = new Tuple<>(base, arguments);
                        if (!toExpand.contains(entry)) {
                            toExpand.add(entry);
                        }

                        String type = stringify(base, arguments);
                        return generateSimpleDefinition(withName.withString("type", type));
                    });
                }
            }
        }

        if (isSymbol(stripped)) {
            return new Some<>(generateSimpleDefinition(withName.withString("type", stripped)));
        }

        return new None<>();
    } */
/* private static String stringify(String base, List_<String> arguments) {
        String merged = mergeAll(arguments, (builder, element) -> mergeDelimited(builder, element, "_"))
                .replace("*", "_ref");

        return base + "_" + merged;
    } */
/* private static String generateFunctionalDefinition(Option<String> maybeName, List_<String> paramTypes, String returnType) {
        String joined = paramTypes.iter().collect(new Joiner(", ")).orElse("");

        return returnType + " (*" + maybeName.orElse("") + ")(" +
                joined +
                ")";
    } */
/* private static String generatePlaceholder(String input) {
        String replaced = input.strip()
                .replace("<cmt-start>", "<cmt-start>")
                .replace("<cmt-end>", "<cmt-end>");

        return "<cmt-start>" + replaced + "<cmt-end>";
    } */
/*  */

} Main;
typedef struct {/* T orElse(T other); */
/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/* Option<T> or(Supplier<Option<T>> other); */
/* T orElseGet(Supplier<T> other); */
/* void ifPresent(Consumer<T> consumer); */
/* boolean isPresent(); */
/* <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other); */
/*  */

} Option_char_ref;
typedef struct {/* List_<T> add(T element); */
/* List_<T> addAll(List_<T> elements); */
/* boolean isEmpty(); */
/* List_<T> copy(); */
/* boolean contains(T element); */
/* Iterator<T> iter(); */
/* int indexOf(T element); */
/* T get(int index); */
/*  */

} List__char_ref;
typedef struct {/*  */

} Tuple_char_ref_List__char_ref;
typedef struct {/* T orElse(T other); */
/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/* Option<T> or(Supplier<Option<T>> other); */
/* T orElseGet(Supplier<T> other); */
/* void ifPresent(Consumer<T> consumer); */
/* boolean isPresent(); */
/* <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other); */
/*  */

} Option_List__char_ref;
typedef struct {/* T orElse(T other); */
/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/* Option<T> or(Supplier<Option<T>> other); */
/* T orElseGet(Supplier<T> other); */
/* void ifPresent(Consumer<T> consumer); */
/* boolean isPresent(); */
/* <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other); */
/*  */

} Option_State;
typedef struct {/* <R> R fold(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Option<R> option = this.head.next().map(next -> folder.apply(finalCurrent, next));
                if (option.isPresent()) {
                    current = option.orElse(null);
                }
                else {
                    return current;
                }
            }
        } */
/* <R> Iterator<R> map(Function<T, R> mapper) {
            return new Iterator<>(() -> this.head.next().map(mapper));
        } */
/* <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        } */
/*  */

} Iterator_char_ref;
int main(int argc, char **argv){
	__main__(argv);
	return 0;
}