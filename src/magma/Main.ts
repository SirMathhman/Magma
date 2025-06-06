interface Option<T> {
	map<R>(mapper: (param0 : T) => R): Option<R>;
	orElseGet(other: () => T): T;
	isPresent(): boolean;
	get(): T;
	orElse(other: T): T;
	or(other: () => Option<T>): Option<T>;
	isEmpty(): boolean;
}
interface Parameter {
	generate(): string;
}
interface Collector<T, C> {
	createInitial(): C;
	fold(current: C, element: T): C;
}
interface Head<T> {
	next(): Option<T>;
}
interface Iterator<T> {
	map<R>(mapper: (param0 : T) => R): Iterator<R>;
	fold<R>(initial: R, folder: (param0 : R, param1 : T) => R): R;
	collect<C>(collector: Collector<T, C>): C;
	flatMap<R>(mapper: (param0 : T) => Iterator<R>): Iterator<R>;
	next(): Option<T>;
}
interface List<T> {
	add(element: T): List<T>;
	iter(): Iterator<T>;
	addAll(elements: List<T>): List<T>;
	popLast(): Option<Tuple<List<T>, T>>;
	isEmpty(): boolean;
	get(index: int): T;
	iterWithIndex(): Iterator<Tuple<Integer, T>>;
}
class Some<T>(T value) implements Option<T> {
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Option<R>/*{
            return new Some<>(mapper.apply(value));
        }*/
	/*@Override
        public*/ orElseGet(other: () => T): T/*{
            return value;
        }*/
	/*@Override
        public*/ isPresent(): boolean/*{
            return true;
        }*/
	/*@Override
        public*/ get(): T/*{
            return value;
        }*/
	/*@Override
        public*/ orElse(other: T): T/*{
            return value;
        }*/
	/*@Override
        public*/ or(other: () => Option<T>): Option<T>/*{
            return this;
        }*/
	/*@Override
        public*/ isEmpty(): boolean/*{
            return false;
        }*/
}
class None<T> implements Option<T> {
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Option<R>/*{
            return new None<>();
        }*/
	/*@Override
        public*/ orElseGet(other: () => T): T/*{
            return other.get();
        }*/
	/*@Override
        public*/ isPresent(): boolean/*{
            return false;
        }*/
	/*@Override
        public*/ get(): T/*{
            return null;
        }*/
	/*@Override
        public*/ orElse(other: T): T/*{
            return other;
        }*/
	/*@Override
        public*/ or(other: () => Option<T>): Option<T>/*{
            return other.get();
        }*/
	/*@Override
        public*/ isEmpty(): boolean/*{
            return true;
        }*/
}
class Lists {
	/*public static */ empty<T>(): List<T>/*{
            return new JavaList<>();
        }*/
	/*@SafeVarargs
        public static */ of<T>(elements: /*T...*/): List<T>/*{
            return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
        }*/
}
class Iterators {
	/*public static */ fromOptional<T>(option: Option<T>): Iterator<T>/*{
            return new HeadedIterator<>(option
                    .<Head<T>>map(SingleHead::new)
                    .orElseGet(EmptyHead::new));
        }*/
}
class RangeHead implements Head<Integer> {
	/*private final*/ length: int;/*
        private int counter = 0;*/
	RangeHead(length: int): public/*{
            this.length = length;
        }*/
	/*@Override
        public*/ next(): Option<Integer>/*{
            if (counter >= length) {
                return new None<>();
            }

            final var value = counter;
            counter++;
            return new Some<>(value);
        }*/
}
class JavaList<T>(java.util.List<T> elements) implements List<T> {
	JavaList(): public/*{
            this(new ArrayList<>());
        }*/
	/*@Override
        public*/ add(element: T): List<T>/*{
            elements.add(element);
            return this;
        }*/
	/*@Override
        public*/ iter(): Iterator<T>/*{
            return createIteratorFromSize().map(elements::get);
        }*/
	/*private*/ createIteratorFromSize(): Iterator<Integer>/*{
            return new HeadedIterator<>(new RangeHead(elements.size()));
        }*/
	/*@Override
        public*/ addAll(elements: List<T>): List<T>/*{
            return elements.iter().<List<T>>fold(this, List::add);
        }*/
	/*@Override
        public*/ popLast(): Option<Tuple<List<T>, T>>/*{
            if (elements.isEmpty()) {
                return new None<>();
            }

            final var last = elements.removeLast();
            return new Some<>(new Tuple<>(this, last));
        }*/
	/*@Override
        public*/ isEmpty(): boolean/*{
            return elements.isEmpty();
        }*/
	/*@Override
        public*/ get(index: int): T/*{
            return elements.get(index);
        }*/
	/*@Override
        public*/ iterWithIndex(): Iterator<Tuple<Integer, T>>/*{
            return createIteratorFromSize().map(index -> new Tuple<>(index, elements.get(index)));
        }*/
}
class EmptyHead<T> implements Head<T> {
	/*@Override
        public*/ next(): Option<T>/*{
            return new None<>();
        }*/
}
class SingleHead<T> implements Head<T> {
	/*private final*/ element: T;
	/*private boolean retrieved*/ false: /*=*/;
	SingleHead(element: T): public/*{
            this.element = element;
        }*/
	/*@Override
        public*/ next(): Option<T>/*{
            if (retrieved) {
                return new None<>();
            }

            retrieved = true;
            return new Some<>(element);
        }*/
}
class FlatMapHead<T, R> implements Head<R> {
	/*private final*/ head: Head<T>;
	/*private final*/ mapper: (param0 : T) => Iterator<R>;
	/*private*/ current: Iterator<R>;
	FlatMapHead(initial: Iterator<R>, head: Head<T>, mapper: (param0 : T) => Iterator<R>): public/*{
            this.current = initial;
            this.head = head;
            this.mapper = mapper;
        }*/
	/*@Override
        public*/ next(): Option<R>/*{
            while (true) {
                final var maybeNext = current.next();
                if (maybeNext.isPresent()) {
                    return maybeNext;
                }

                final var maybeNextIter = head.next().map(mapper);
                if (maybeNextIter.isPresent()) {
                    current = maybeNextIter.get();
                }
                else {
                    return new None<>();
                }
            }
        }*/
}
class HeadedIterator<T>(Head<T> head) implements Iterator<T> {
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Iterator<R>/*{
            return new HeadedIterator<>(() -> head.next().map(mapper));
        }*/
	/*@Override
        public */ fold<R>(initial: R, folder: (param0 : R, param1 : T) => R): R/*{
            var current = initial;
            while (true) {
                R finalCurrent = current;
                final var maybeNext = head.next().map(next -> folder.apply(finalCurrent, next));
                if (maybeNext.isPresent()) {
                    current = maybeNext.get();
                }
                else {
                    return current;
                }
            }
        }*/
	/*@Override
        public */ collect<C>(collector: Collector<T, C>): C/*{
            return fold(collector.createInitial(), collector::fold);
        }*/
	/*@Override
        public */ flatMap<R>(mapper: (param0 : T) => Iterator<R>): Iterator<R>/*{
            final var head = this.head.next()
                    .map(mapper)
                    .<Head<R>>map(initial -> new FlatMapHead<>(initial, this.head, mapper))
                    .orElseGet(EmptyHead::new);

            return new HeadedIterator<>(head);
        }*/
	/*@Override
        public*/ next(): Option<T>/*{
            return head.next();
        }*/
}
class Tuple<L, R> {
	left: L;
	right: R;
	constructor (left: L, right: R) {
		this.left = left;
		this.right = right;
	}
}
class State {
	/*private*/ segments: List<string>;
	/*private*/ buffer: StringBuilder;
	/*private*/ depth: int;
	State(segments: List<string>, buffer: StringBuilder, depth: int): public/*{
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }*/
	State(): public/*{
            this(Lists.empty(), new StringBuilder(), 0);
        }*/
	/*private*/ append(c: char): State/*{
            buffer.append(c);
            return this;
        }*/
	/*private*/ enter(): State/*{
            this.depth = depth + 1;
            return this;
        }*/
	/*private*/ exit(): State/*{
            this.depth = depth - 1;
            return this;
        }*/
	/*private*/ isShallow(): boolean/*{
            return depth == 1;
        }*/
	/*private*/ advance(): State/*{
            segments = segments.add(buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }*/
	/*private*/ isLevel(): boolean/*{
            return depth == 0;
        }*/
}
class Definition(
            Option<String> beforeType,
            List<String> typeParams,
            String type,
            String name
    ) implements Parameter {
	/*@Override
        public*/ generate(): string/*{
            return generateWithAfterName("");
        }*/
	/*public*/ generateWithAfterName(afterName: string): string/*{
            final var joinedTypeParams = typeParams.iter()
                    .collect(new Joiner(", "))
                    .map(value -> "<" + value + ">")
                    .orElse("");

            final var beforeType = this.beforeType.map(inner -> inner + " ").orElse("");
            return beforeType + name + joinedTypeParams + afterName + ": " + type;
        }*/
}
class Placeholder(String input) implements Parameter {
	/*@Override
        public*/ generate(): string/*{
            return generatePlaceholder(input);
        }*/
}
class Whitespace implements Parameter {
	/*@Override
        public*/ generate(): string/*{
            return "";
        }*/
}
class Joiner implements Collector<String, Option<String>> {
	/*private final*/ delimiter: string;
	Joiner(): public/*{
            this("");
        }*/
	Joiner(delimiter: string): public/*{
            this.delimiter = delimiter;
        }*/
	/*@Override
        public*/ createInitial(): Option<string>/*{
            return new None<>();
        }*/
	/*@Override
        public*/ fold(current: Option<string>, element: string): Option<string>/*{
            return new Some<>(current.map(inner -> inner + delimiter + element).orElse(element));
        }*/
}
class ListCollector<T> implements Collector<T, List<T>> {
	/*@Override
        public*/ createInitial(): List<T>/*{
            return Lists.empty();
        }*/
	/*@Override
        public*/ fold(current: List<T>, element: T): List<T>/*{
            return current.add(element);
        }*/
}
export class Main {
	/*public static*/ main(args: /*String[]*/): void/*{
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var target = source.resolveSibling("Main.ts");

            final var input = Files.readString(source);
            final var output = compile(input);
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }*/
	/*private static*/ compile(input: string): string/*{
        return compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static*/ compileStatements(input: string, mapper: (param0 : string) => string): string/*{
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }*/
	/*private static*/ compileAll(input: string, mapper: (param0 : string) => string, folder: (param0 : State, param1 : Character) => State, merger: (param0 : StringBuilder, param1 : string) => StringBuilder): string/*{
        return generateAll(parseAll(input, folder, mapper), merger);
    }*/
	/*private static*/ generateAll(elements: List<string>, merger: (param0 : StringBuilder, param1 : string) => StringBuilder): string/*{
        return elements.iter()
                .fold(new StringBuilder(), merger)
                .toString();
    }*/
	/*private static*/ parseAll(input: string, folder: (param0 : State, param1 : Character) => State, mapper: (param0 : string) => string): List<string>/*{
        return divide(input, folder)
                .iter()
                .map(mapper)
                .collect(new ListCollector<>());
    }*/
	/*private static*/ mergeStatements(output: StringBuilder, compiled: string): StringBuilder/*{
        return output.append(compiled);
    }*/
	/*private static*/ divide(input: string, folder: (param0 : State, param1 : Character) => State): List<string>/*{
        State state = new State();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }*/
	/*private static*/ foldStatements(current: State, c: char): State/*{
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}*//*' && appended.isShallow()) {
            return appended.advance().exit();
        }*//*
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}*//*') {
            return appended.exit();
        }*/
	appended: return;
}
/*

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileRootStructure(input)
                .orElseGet(() -> generatePlaceholder(input));
    }*/class ", "class").map(tuple -> {
	/*final var joined*/ join(/*tuple.right*/): /*=*/;
	/*return tuple.left*/ joined: /*+*/;/*
        });
    */
}
/*

    private static String join(List<String> list) {
        return list.iter()
                .collect(new Joiner())
                .orElse("");
    }*//*

    private static Option<Tuple<String, List<String>>> compileStructure(String input, String keyword, String targetInfix) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return new None<>();
        }

        final var modifiersString = input.substring(0, classIndex);
        final var afterClass = input.substring(classIndex + keyword.length());
        final var contentStart = afterClass.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }

        final var beforeContent = afterClass.substring(0, contentStart).strip();
        final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        final var folded = divide(inputContent, Main::foldStatements)
                .iter()
                .map(Main::compileClassSegment)
                .fold(new Tuple<>(new StringBuilder(), Lists.<String>empty()), (tuple, element) -> new Tuple<>(tuple.left.append(element.left), tuple.right.addAll(element.right)));

        final var output = folded.left.toString();
        final var structures = folded.right;

        final var modifiers = modifiersString.contains("public") ? "export " : "";

        final var generated = assembleStructure(beforeContent, modifiers, output, targetInfix);
        return new Some<>(new Tuple<>("", structures.add(generated)));
    }*//*

    private static String assembleStructure(String beforeContent, String modifiers, String outputContent, String targetInfix) {
        if (beforeContent.endsWith(")")) {
            final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
            final var paramStart = withoutParamEnd.indexOf("(");
            if (paramStart >= 0) {
                final var name = withoutParamEnd.substring(0, paramStart).strip();
                final var inputParams = withoutParamEnd.substring(paramStart + "(".length());
                final var fields = divide(inputParams, Main::foldValues)
                        .iter()
                        .map(Main::parseParameter)
                        .map(Main::retainDefinition)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                final var output = fields.iter()
                        .map(Definition::generate)
                        .fold(new StringBuilder(), Main::mergeValues);

                final var outputParams = output.toString();
                final var generatedFields = fields.iter()
                        .map(Definition::generate)
                        .map(element -> "\n\t" + element + ";")
                        .collect(new Joiner())
                        .orElse("");

                final var assignments = fields.iter()
                        .map(field -> {
                            final var fieldName = field.name;
                            final var content = "this." + fieldName + " = " + fieldName;
                            return generateStatement(content);
                        })
                        .collect(new Joiner())
                        .orElse("");

                return generateClass(modifiers, name, generatedFields + "\n\tconstructor (" + outputParams + ") {" +
                        assignments +
                        "\n\t}" + outputContent, targetInfix);
            }
        }

        return generateClass(modifiers, beforeContent, outputContent, targetInfix);
    }*//*

    private static Option<Definition> retainDefinition(Parameter parameter) {
        if (parameter instanceof Definition definition) {
            return new Some<>(definition);
        }
        else {
            return new None<>();
        }
    }*//*

    private static String generateStatement(String content) {
        return "\n" + "\t".repeat(2) + content + ";";
    }*//*

    private static String generateClass(String modifiers, String beforeContent, String outputContent, String targetInfix) {
        return modifiers + targetInfix + " " + beforeContent + " {" + outputContent + "\n}\n";
    }*//*

    private static Tuple<String, List<String>> compileClassSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileStructure(input, "record ", "class"))
                .or(() -> compileStructure(input, "class ", "class"))
                .or(() -> compileStructure(input, "interface ", "interface"))
                .or(() -> compileField(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), Lists.empty()));
    }*//*

    private static Option<Tuple<String, List<String>>> compileField(String input) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return getStringListTuple(content);
    }*//*

    private static Option<Tuple<String, List<String>>> getStringListTuple(String content) {
        return compileSimpleDefinition(content).map(definition -> new Tuple<>("\n\t" + definition + ";", Lists.empty()));
    }*//*

    private static Option<String> compileSimpleDefinition(String content) {
        return parseDefinition(content).map(Definition::generate);
    }*//*

    private static Option<Tuple<String, List<String>>> compileWhitespace(String input) {
        return parseWhitespace(input).map(node -> new Tuple<>(node.generate(), Lists.empty()));
    }*//*

    private static Option<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>(new Whitespace());
        }
        else {
            return new None<>();
        }
    }*//*

    private static Option<Tuple<String, List<String>>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var inputDefinition = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var inputParams = withParams.substring(0, paramEnd);
                final var inputAfterParams = withParams.substring(paramEnd + ")".length()).strip();

                final var maybeOutputDefinition = parseDefinition(inputDefinition);
                if (maybeOutputDefinition.isPresent()) {
                    final var outputDefinition = maybeOutputDefinition.get();
                    final var outputParams = compileParameters(inputParams);

                    final var outputAfterParams = inputAfterParams.equals(";")
                            ? ";"
                            : generatePlaceholder(inputAfterParams);

                    final var generated = "\n\t" + outputDefinition.generateWithAfterName("(" + outputParams + ")") + outputAfterParams;
                    return new Some<>(new Tuple<>(generated, Lists.empty()));
                }
            }
        }

        return new None<>();
    }*//*

    private static String compileParameters(String input) {
        return compileValues(input, Main::compileParameter);
    }*//*

    private static String compileValues(String input, Function<String, String> mapper) {
        return compileAll(input, mapper, Main::foldValues, Main::mergeValues);
    }*//*

    private static String compileParameter(String input) {
        return parseParameter(input).generate();
    }*//*

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (!cache.isEmpty()) {
            cache.append(", ");
        }
        return cache.append(element);
    }*//*

    private static State foldValues(State state, char c) {
        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }*//*

    private static Parameter parseParameter(String input) {
        return parseWhitespace(input).<Parameter>map(parameter -> parameter)
                .or(() -> parseDefinition(input).map(parameter -> parameter))
                .orElseGet(() -> new Placeholder(input));
    }*//*

    private static Option<Definition> parseDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        final var beforeName = stripped.substring(0, nameSeparator).strip();
        final var name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(name)) {
            return new None<>();
        }

        final var divisions = divide(beforeName, Main::foldTypeSeparator);
        final var maybePopped = divisions.popLast();
        if (maybePopped.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), compileType(beforeName), name));
        }

        final var popped = maybePopped.get();
        final var beforeTypeDivisions = popped.left;
        final var type = popped.right;
        final var compiledType = compileType(type);

        if (beforeTypeDivisions.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), compileType(type), name));
        }

        final var beforeType = beforeTypeDivisions.iter().collect(new Joiner(" ")).orElse("");
        return new Some<>(assembleDefinition(beforeType, compiledType, name));
    }*//*

    private static State foldTypeSeparator(State state, char c) {
        if (c == ' ' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }*//*

    private static Definition assembleDefinition(String beforeType, String compiledType, String name) {
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                final var beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                final var typeParamsString = withoutEnd.substring(typeParamStart + "<".length());
                final var typeParams = parseAll(typeParamsString, Main::foldValues, String::strip);

                final Option<String> beforeTypeOptional;
                beforeTypeOptional = beforeTypeParams.isEmpty() ? new None<>() : new Some<>(generatePlaceholder(beforeTypeParams));

                return new Definition(beforeTypeOptional, typeParams, compiledType, name);
            }
        }

        return new Definition(new Some<>(generatePlaceholder(beforeType)), Lists.empty(), compiledType, name);
    }*//*

    private static String compileType(String input) {
        final var stripped = input.strip();
        if (stripped.equals("String")) {
            return "string";
        }

        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var argumentsStart = withoutEnd.indexOf("<");
            if (argumentsStart >= 0) {
                final var base = withoutEnd.substring(0, argumentsStart).strip();
                final var inputArguments = withoutEnd.substring(argumentsStart + 1);
                final var elements = parseValues(inputArguments);

                if (base.equals("Supplier")) {
                    return generateFunctionalType(Lists.empty(), elements.get(0));
                }

                if (base.equals("Function")) {
                    return generateFunctionalType(Lists.of(elements.get(0)), elements.get(1));
                }

                if (base.equals("BiFunction")) {
                    return generateFunctionalType(Lists.of(elements.get(0), elements.get(1)), elements.get(2));
                }

                final var outputArguments = generateValues(elements);
                return base + "<" + outputArguments + ">";
            }
        }

        if (isSymbol(stripped)) {
            return stripped;
        }

        return generatePlaceholder(input);
    }*//*

    private static String generateFunctionalType(List<String> parameterTypes, String returnType) {
        final var parameters = parameterTypes.iterWithIndex()
                .map(entry -> "param" + entry.left + " : " + entry.right)
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + parameters + ") => " + returnType;
    }*//*

    private static String generateValues(List<String> elements) {
        return generateAll(elements, Main::mergeValues);
    }*//*

    private static List<String> parseValues(String inputArguments) {
        return parseAll(inputArguments, Main::foldValues, Main::compileType);
    }*//*

    private static boolean isSymbol(String input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }*//*

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("start", "start")
                .replace("end", "end");

        return "start" + replaced + "end";
    }*//*
}*/