interface Parameter {
	generate(): string;
}
interface Collector<T, C> {
	createInitial(): C;
	/*C fold(C current,*/ element): T;
}
interface Head<T> {
	next(): Optional<T>;
}
interface Iterator<T> {
	/*<R> Iterator<R> map(Function<T,*/ mapper): /*R>*/;
	/*<R> R fold(R initial, BiFunction<R, T,*/ folder): /*R>*/;
	/*<C> C collect(Collector<T,*/ collector): /*C>*/;
	/*<R> Iterator<R> flatMap(Function<T,*/ mapper): Iterator</*R>*/>;
	next(): Optional<T>;
}
interface List<T> {
	/*List<T>*/ element): /*add(T*/;
	iter(): Iterator<T>;
	/*List<T>*/ elements): addAll(List<T>;
}
class Lists {
	/*public static <T>*/ empty(): List<T>/* {
            return new JavaList<>();
        }*/
}
class Iterators {
	/*public static <T>*/ fromOptional(optional: Optional<T>): Iterator<T>/* {
            return new HeadedIterator<>(optional
                    .<Head<T>>map(SingleHead::new)
                    .orElseGet(EmptyHead::new));
        }*/
}
class RangeHead implements Head<Integer> {
	/*private final*/ length: int;
	/*private int counter*/ 0: /*=*/;
	RangeHead(length: int): public/* {
            this.length = length;
        }*/
	/*@Override
        public*/ next(): Optional<Integer>/* {
            if (counter >= length) {
                return Optional.empty();
            }

            final var value = counter;
            counter++;
            return Optional.of(value);
        }*/
}
class JavaList<T>(java.util.List<T> elements) implements List<T> {
	JavaList(): public/* {
            this(new ArrayList<>());
        }*/
	/*@Override
        public*/ add(element: T): List<T>/* {
            elements.add(element);
            return this;
        }*/
	/*@Override
        public*/ iter(): Iterator<T>/* {
            return new HeadedIterator<>(new RangeHead(elements.size())).map(elements::get);
        }*/
	/*@Override
        public*/ addAll(elements: List<T>): List<T>/* {
            return elements.iter().<List<T>>fold(this, List::add);
        }*/
}
class EmptyHead<T> implements Head<T> {
	/*@Override
        public*/ next(): Optional<T>/* {
            return Optional.empty();
        }*/
}
class SingleHead<T> implements Head<T> {
	/*private final*/ element: T;
	/*private boolean retrieved*/ false: /*=*/;
	SingleHead(element: T): public/* {
            this.element = element;
        }*/
	/*@Override
        public*/ next(): Optional<T>/* {
            if (retrieved) {
                return Optional.empty();
            }

            retrieved = true;
            return Optional.of(element);
        }*/
}
class FlatMapHead<T, R> implements Head<R> {
	/*private final*/ head: Head<T>;
	/*private final Function<T,*/ mapper: Iterator</*R>*/>;
	/*private*/ current: Iterator<R>;
	FlatMapHead(initial: Iterator<R>, head: Head<T>, /* Function<T*/, mapper: Iterator</*R>*/>): public/* {
            this.current = initial;
            this.head = head;
            this.mapper = mapper;
        }*/
	/*@Override
        public*/ next(): Optional<R>/* {
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
                    return Optional.empty();
                }
            }
        }*/
}
class HeadedIterator<T>(Head<T> head) implements Iterator<T> {
	/*@Override
        public <R>*/ map(/*Function<T*/, mapper: /*R>*/): Iterator<R>/* {
            return new HeadedIterator<>(() -> head.next().map(mapper));
        }*/
	/*@Override
        public <R>*/ fold(initial: R, /* BiFunction<R*/, /* T*/, folder: /*R>*/): R/* {
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
        public <C>*/ collect(/*Collector<T*/, collector: /*C>*/): C/* {
            return fold(collector.createInitial(), collector::fold);
        }*/
	/*@Override
        public <R>*/ flatMap(/*Function<T*/, mapper: Iterator</*R>*/>): Iterator<R>/* {
            final var head = this.head.next()
                    .map(mapper)
                    .<Head<R>>map(initial -> new FlatMapHead<>(initial, this.head, mapper))
                    .orElseGet(EmptyHead::new);

            return new HeadedIterator<>(head);
        }*/
	/*@Override
        public*/ next(): Optional<T>/* {
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
	State(segments: List<string>, buffer: StringBuilder, depth: int): public/* {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }*/
	State(): public/* {
            this(Lists.empty(), new StringBuilder(), 0);
        }*/
	/*private*/ append(c: char): State/* {
            buffer.append(c);
            return this;
        }*/
	/*private*/ enter(): State/* {
            this.depth = depth + 1;
            return this;
        }*/
	/*private*/ exit(): State/* {
            this.depth = depth - 1;
            return this;
        }*/
	/*private*/ isShallow(): boolean/* {
            return depth == 1;
        }*/
	/*private*/ advance(): State/* {
            segments = segments.add(buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }*/
	/*private*/ isLevel(): boolean/* {
            return depth == 0;
        }*/
}
class Definition(Optional<String> beforeType, String type, String name) implements Parameter {
	/*@Override
        public*/ generate(): string/* {
            return generateWithAfterName("");
        }*/
	/*public*/ generateWithAfterName(afterName: string): string/* {
            final var beforeType = this.beforeType.map(inner -> inner + " ").orElse("");
            return beforeType + name + afterName + ": " + type;
        }*/
}
class Placeholder(String input) implements Parameter {
	/*@Override
        public*/ generate(): string/* {
            return generatePlaceholder(input);
        }*/
}
class Whitespace implements Parameter {
	/*@Override
        public*/ generate(): string/* {
            return "";
        }*/
}
class Joiner implements Collector<String, Optional<String>> {
	/*@Override
        public*/ createInitial(): Optional<string>/* {
            return Optional.empty();
        }*/
	/*@Override
        public*/ fold(current: Optional<string>, element: string): Optional<string>/* {
            return Optional.of(current.map(inner -> inner + element).orElse(element));
        }*/
}
class ListCollector<T> implements Collector<T, List<T>> {
	/*@Override
        public*/ createInitial(): List<T>/* {
            return Lists.empty();
        }*/
	/*@Override
        public*/ fold(current: List<T>, element: T): List<T>/* {
            return current.add(element);
        }*/
}
export class Main {
	/*public static*/ main(args: /*String[]*/): void/* {
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
	/*private static*/ compile(input: string): string/* {
        return compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static*/ compileStatements(input: string, /* Function<String*/, mapper: /*String>*/): string/* {
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }*/
	/*private static*/ compileAll(input: string, /* Function<String*/, mapper: /*String>*/, /* BiFunction<State*/, /* Character*/, folder: /*State>*/, /* BiFunction<StringBuilder*/, /* String*/, merger: /*StringBuilder>*/): string/* {
        return divide(input, folder)
                .iter()
                .map(mapper)
                .fold(new StringBuilder(), merger)
                .toString();
    }*/
	/*private static*/ mergeStatements(output: StringBuilder, compiled: string): StringBuilder/* {
        return output.append(compiled);
    }*/
	/*private static*/ divide(input: string, /* BiFunction<State*/, /* Character*/, folder: /*State>*/): List<string>/* {
        State state = new State();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }*/
	/*private static*/ foldStatements(current: State, c: char): State/* {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}*/
	/*'*/ appended.isShallow(): /*&&*//*) {
            return appended.advance().exit();
        }*//*
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}*/
	/*') {
           */ appended.exit(): return/*;
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
	/*final var joined*/ join(tuple.right): /*=*/;
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

    private static Optional<Tuple<String, List<String>>> compileStructure(String input, String keyword, String targetInfix) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return Optional.empty();
        }

        final var modifiersString = input.substring(0, classIndex);
        final var afterClass = input.substring(classIndex + keyword.length());
        final var contentStart = afterClass.indexOf("{");
        if (contentStart < 0) {
            return Optional.empty();
        }

        final var beforeContent = afterClass.substring(0, contentStart).strip();
        final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return Optional.empty();
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
        return Optional.of(new Tuple<>("", structures.add(generated)));
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
                            return generateStatement(content, 2);
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

    private static Optional<Definition> retainDefinition(Parameter parameter) {
        if (parameter instanceof Definition definition) {
            return Optional.of(definition);
        }
        else {
            return Optional.empty();
        }
    }*//*

    private static String generateStatement(String content, int depth) {
        return "\n" + "\t".repeat(depth) + content + ";";
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

    private static Optional<Tuple<String, List<String>>> compileField(String input) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return Optional.empty();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return getStringListTuple(content);
    }*//*

    private static Optional<Tuple<String, List<String>>> getStringListTuple(String content) {
        return compileSimpleDefinition(content).map(definition -> new Tuple<>("\n\t" + definition + ";", Lists.empty()));
    }*//*

    private static Optional<String> compileSimpleDefinition(String content) {
        return compileDefinition(content).map(Definition::generate);
    }*//*

    private static Optional<Tuple<String, List<String>>> compileWhitespace(String input) {
        return parseWhitespace(input).map(node -> new Tuple<>(node.generate(), Lists.empty()));
    }*//*

    private static Optional<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of(new Whitespace());
        }
        else {
            return Optional.empty();
        }
    }*//*

    private static Optional<Tuple<String, List<String>>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var inputDefinition = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var inputParams = withParams.substring(0, paramEnd);
                final var withBraces = withParams.substring(paramEnd + ")".length());

                final var maybeOutputDefinition = compileDefinition(inputDefinition);
                if (maybeOutputDefinition.isPresent()) {
                    final var outputDefinition = maybeOutputDefinition.get();
                    final var outputParams = compileParameters(inputParams);

                    final var generated = "\n\t" + outputDefinition.generateWithAfterName("(" + outputParams + ")") + generatePlaceholder(withBraces);
                    return Optional.of(new Tuple<>(generated, Lists.empty()));
                }
            }
        }

        return Optional.empty();
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
        if (c == ',') {
            return state.advance();
        }
        return state.append(c);
    }*//*

    private static Parameter parseParameter(String input) {
        return parseWhitespace(input).<Parameter>map(parameter -> parameter)
                .or(() -> compileDefinition(input))
                .orElseGet(() -> new Placeholder(input));
    }*//*

    private static Optional<Definition> compileDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return Optional.empty();
        }

        final var beforeName = stripped.substring(0, nameSeparator).strip();
        final var name = stripped.substring(nameSeparator + " ".length());
        final var typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator < 0) {
            return Optional.of(new Definition(Optional.empty(), compileType(beforeName), name));
        }

        final var beforeType = beforeName.substring(0, typeSeparator);
        final var type = beforeName.substring(typeSeparator + " ".length());
        return Optional.of(new Definition(Optional.of(generatePlaceholder(beforeType)), compileType(type), name));
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
                final var outputArguments = compileValues(inputArguments, Main::compileType);
                return base + "<" + outputArguments + ">";
            }
        }

        if (isSymbol(stripped)) {
            return stripped;
        }

        return generatePlaceholder(input);
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