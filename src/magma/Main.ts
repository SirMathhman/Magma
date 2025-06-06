interface Option {
	map<R>(mapper: (param0 : T) => R): Option<R>;
	orElseGet(other: () => T): T;
	isPresent(): boolean;
	get(): T;
	orElse(other: T): T;
	or(other: () => Option<T>): Option<T>;
	isEmpty(): boolean;
	and<R>(other: () => Option<R>): Option<Tuple<T, R>>;
}
interface Parameter {
}
interface Collector {
	createInitial(): C;
	fold(current: C, element: T): C;
}
interface Head {
	next(): Option<T>;
}
interface Iterator {
	map<R>(mapper: (param0 : T) => R): Iterator<R>;
	fold<R>(initial: R, folder: (param0 : R, param1 : T) => R): R;
	collect<C>(collector: Collector<T, C>): C;
	flatMap<R>(mapper: (param0 : T) => Iterator<R>): Iterator<R>;
	next(): Option<T>;
	filter(predicate: Predicate<T>): Iterator<T>;
	zip<R>(other: Iterator<R>): Iterator<Tuple<T, R>>;
}
interface List {
	add(element: T): List<T>;
	iter(): Iterator<T>;
	addAll(elements: List<T>): List<T>;
	popLast(): Option<Tuple<List<T>, T>>;
	isEmpty(): boolean;
	get(index: int): T;
	iterWithIndex(): Iterator<Tuple<Integer, T>>;
	iterReversed(): Iterator<T>;
	mapLast(mapper: (param0 : T) => T): List<T>;
}
interface Generating {
	generate(): string;
}
interface Map {
	putAll(other: Map<K, V>): Map<K, V>;
	iter(): Iterator<Tuple<K, V>>;
	putTuple(kvTuple: Tuple<K, V>): Map<K, V>;
	put(key: K, value: V): Map<K, V>;
	get(key: K): Option<V>;
}
interface ValueArgument {
}
interface TypeArgument {
}
class Some implements Option<T> {
	value: T;
	constructor (value: T) {
		this.value = value;
	}
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Option<R> {
		return new Some<>(mapper(value));
	}
	/*@Override
        public*/ orElseGet(other: () => T): T {
		return value;
	}
	/*@Override
        public*/ isPresent(): boolean {
		return true;
	}
	/*@Override
        public*/ get(): T {
		return value;
	}
	/*@Override
        public*/ orElse(other: T): T {
		return value;
	}
	/*@Override
        public*/ or(other: () => Option<T>): Option<T> {
		return this;
	}
	/*@Override
        public*/ isEmpty(): boolean {
		return false;
	}
	/*@Override
        public */ and<R>(other: () => Option<R>): Option<Tuple<T, R>> {
		return other(/*)*/.map(/*otherValue -> new Tuple<>(value, otherValue*/));
	}
}
class None implements Option<T> {
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Option<R> {
		return new None<>();
	}
	/*@Override
        public*/ orElseGet(other: () => T): T {
		return other();
	}
	/*@Override
        public*/ isPresent(): boolean {
		return false;
	}
	/*@Override
        public*/ get(): T {
		return null;
	}
	/*@Override
        public*/ orElse(other: T): T {
		return other;
	}
	/*@Override
        public*/ or(other: () => Option<T>): Option<T> {
		return other();
	}
	/*@Override
        public*/ isEmpty(): boolean {
		return true;
	}
	/*@Override
        public */ and<R>(other: () => Option<R>): Option<Tuple<T, R>> {
		return new None<>();
	}
}
class Lists {
	/*public static */ empty<T>(): List<T> {
		return new JavaList<>();
	}
	/*@SafeVarargs
        public static */ of<T>(elements: /*T...*/): List<T> {
		return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
	}
}
class Iterators {
	/*public static */ fromOptional<T>(option: Option<T>): Iterator<T> {
		return new HeadedIterator<>(option.<Head<T>>map(/*SingleHead::new)
                    */.orElseGet(EmptyHead::new));
	}
}
class RangeHead implements Head<Integer> {
	/*private final*/ length: int;/*
        private int counter = 0;*/
	RangeHead(length: int): public {/*
            this.length = length;*/
	}
	/*@Override
        public*/ next(): Option<Integer> {/*
            if (counter >= length) {
                return new None<>();
            }*//*

            final var value = counter;*//*
            counter++;*/
		return new Some<>(value);
	}
}
class JavaList implements List<T> {
	elements: java.util.List<T>;
	constructor (elements: java.util.List<T>) {
		this.elements = elements;
	}
	JavaList(): public {/*
            this(new ArrayList<>());*/
	}
	/*@Override
        public*/ add(element: T): List<T> {/*
            final var copy = new ArrayList<T>(elements);*//*
            copy.add(element);*/
		return new JavaList<>(copy);
	}
	/*@Override
        public*/ iter(): Iterator<T> {
		return createIteratorFromSize(/*)*/.map(elements::get);
	}
	/*private*/ createIteratorFromSize(): Iterator<Integer> {
		return new HeadedIterator<>(new RangeHead(elements.size()));
	}
	/*@Override
        public*/ addAll(elements: List<T>): List<T> {
		return elements.iter(/*)*/.<List<T>>fold(this, /* List::add*/);
	}
	/*@Override
        public*/ popLast(): Option<Tuple<List<T>, T>> {/*
            if (elements.isEmpty()) {
                return new None<>();
            }*//*

            final var last = elements.removeLast();*/
		return new Some<>(/*new Tuple<>(this*/, /* last)*/);
	}
	/*@Override
        public*/ isEmpty(): boolean {
		return elements.isEmpty();
	}
	/*@Override
        public*/ get(index: int): T {
		return elements.get(index);
	}
	/*@Override
        public*/ iterWithIndex(): Iterator<Tuple<Integer, T>> {
		return createIteratorFromSize(/*)*/.map(/*index -> new Tuple<>*/(index, elements.get(index)));
	}
	/*@Override
        public*/ iterReversed(): Iterator<T> {
		return createIteratorFromSize(/*)
                    */.map(/*index -> elements*/.size() - index - 1).map(elements::get);
	}
	/*@Override
        public*/ mapLast(mapper: (param0 : T) => T): List<T> {/*
            if (elements.isEmpty()) {
                return this;
            }*//*

            final var mapped = mapper.apply(elements.getLast());*//*
            elements.set(elements.size() - 1, mapped);*/
		return this;
	}
}
class EmptyHead implements Head<T> {
	/*@Override
        public*/ next(): Option<T> {
		return new None<>();
	}
}
class SingleHead implements Head<T> {
	/*private final*/ element: T;
	/*private boolean retrieved*/ false: /*=*/;
	SingleHead(element: T): public {/*
            this.element = element;*/
	}
	/*@Override
        public*/ next(): Option<T> {/*
            if (retrieved) {
                return new None<>();
            }*//*

            retrieved = true;*/
		return new Some<>(element);
	}
}
class FlatMapHead implements Head<R> {
	/*private final*/ head: Head<T>;
	/*private final*/ mapper: (param0 : T) => Iterator<R>;
	/*private*/ current: Iterator<R>;
	FlatMapHead(initial: Iterator<R>, head: Head<T>, mapper: (param0 : T) => Iterator<R>): public {/*
            this.current = initial;*//*
            this.head = head;*//*
            this.mapper = mapper;*/
	}
	/*@Override
        public*/ next(): Option<R> {/*
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
            }*/
	}
}
class HeadedIterator implements Iterator<T> {
	head: Head<T>;
	constructor (head: Head<T>) {
		this.head = head;
	}
	/*@Override
        public */ map<R>(mapper: (param0 : T) => R): Iterator<R> {
		return new HeadedIterator<>(/**/(/*) -> head*/.next().map(mapper));
	}
	/*@Override
        public */ fold<R>(initial: R, folder: (param0 : R, param1 : T) => R): R {/*
            var current = initial;*//*
            while (true) {
                R finalCurrent = current;
                final var maybeNext = head.next().map(next -> folder.apply(finalCurrent, next));
                if (maybeNext.isPresent()) {
                    current = maybeNext.get();
                }
                else {
                    return current;
                }
            }*/
	}
	/*@Override
        public */ collect<C>(collector: Collector<T, C>): C {
		return fold(collector.createInitial(), /* collector::fold*/);
	}
	/*@Override
        public */ flatMap<R>(mapper: (param0 : T) => Iterator<R>): Iterator<R> {/*
            final var head = this.head.next()
                    .map(mapper)
                    .<Head<R>>map(initial -> new FlatMapHead<>(initial, this.head, mapper))
                    .orElseGet(EmptyHead::new);*/
		return new HeadedIterator<>(head);
	}
	/*@Override
        public*/ next(): Option<T> {
		return head.next();
	}
	/*@Override
        public*/ filter(predicate: Predicate<T>): Iterator<T> {/*
            return flatMap(element -> {
                final var isValid = predicate.test(element);
                final var head = isValid ? new SingleHead<>(element) : new EmptyHead<T>();
                return new HeadedIterator<>(head);
            }*//*);*/
	}
	/*@Override
        public */ zip<R>(other: Iterator<R>): Iterator<Tuple<T, R>> {
		return new HeadedIterator<>(/**/(/*) -> head*/.next(/*)*/.and(() -> other.next()));
	}
}
class Tuple {
	left: L;
	right: R;
	constructor (left: L, right: R) {
		this.left = left;
		this.right = right;
	}
}
class DivideState {
	/*private*/ segments: List<string>;
	/*private*/ buffer: StringBuilder;
	/*private*/ depth: int;
	DivideState(segments: List<string>, buffer: StringBuilder, depth: int): public {/*
            this.segments = segments;*//*
            this.buffer = buffer;*//*
            this.depth = depth;*/
	}
	DivideState(): public {/*
            this(Lists.empty(), new StringBuilder(), 0);*/
	}
	/*private*/ append(c: char): DivideState {/*
            buffer.append(c);*/
		return this;
	}
	/*private*/ enter(): DivideState {/*
            this.depth = depth + 1;*/
		return this;
	}
	/*private*/ exit(): DivideState {/*
            this.depth = depth - 1;*/
		return this;
	}
	/*private*/ isShallow(): boolean {
		return /*depth == 1*/;
	}
	/*private*/ advance(): DivideState {/*
            segments = segments.add(buffer.toString());*//*
            this.buffer = new StringBuilder();*/
		return this;
	}
	/*private*/ isLevel(): boolean {
		return /*depth == 0*/;
	}
}
class Definition implements Parameter, Generating {
	beforeType: Option<string>;
	typeParams: List<string>;
	type: Type;
	name: string;
	constructor (beforeType: Option<string>, typeParams: List<string>, type: Type, name: string) {
		this.beforeType = beforeType;
		this.typeParams = typeParams;
		this.type = type;
		this.name = name;
	}
	Definition(type: Type, name: string): public {/*
            this(new None<>(), Lists.empty(), type, name);*/
	}
	/*@Override
        public*/ generate(): string {
		return generateWithAfterName(/*""*/);
	}
	/*public*/ generateWithAfterName(afterName: string): string {/*
            final var joinedTypeParams = typeParams.iter()
                    .collect(new Joiner(", "))
                    .map(value -> "<" + value + ">")
                    .orElse("");*//*

            final var beforeType = this.beforeType.map(inner -> inner + " ").orElse("");*/
		return /*beforeType + name + joinedTypeParams + afterName + ": " + type*/.generate();
	}
	/*public*/ mapType(mapper: (param0 : Type) => Type): Definition {
		return new Definition(beforeType, typeParams, mapper(type), name);
	}
}
class Placeholder implements Parameter, Value, Type {
	input: string;
	constructor (input: string) {
		this.input = input;
	}
	/*@Override
        public*/ generate(): string {
		return generatePlaceholder(input);
	}
}
class Whitespace implements Parameter, Generating, ValueArgument, TypeArgument {
	/*@Override
        public*/ generate(): string {
		return /*""*/;
	}
}
class Joiner implements Collector<string, Option<string>> {
	/*private final*/ delimiter: string;
	Joiner(): public {/*
            this("");*/
	}
	Joiner(delimiter: string): public {/*
            this.delimiter = delimiter;*/
	}
	/*@Override
        public*/ createInitial(): Option<string> {
		return new None<>();
	}
	/*@Override
        public*/ fold(current: Option<string>, element: string): Option<string> {
		return new Some<>(current.map(/*inner -> inner + delimiter + element)*/.orElse(element));
	}
}
class ListCollector implements Collector<T, List<T>> {
	/*@Override
        public*/ createInitial(): List<T> {
		return Lists.empty();
	}
	/*@Override
        public*/ fold(current: List<T>, element: T): List<T> {
		return current.add(element);
	}
}
class Construction implements Caller {
	type: Type;
	constructor (type: Type) {
		this.type = type;
	}
	/*@Override
        public*/ generate(): string {
		return /*"new " + type*/.generate();
	}
}
class Invocation implements Value {
	caller: Caller;
	arguments: List<Value>;
	constructor (caller: Caller, arguments: List<Value>) {
		this.caller = caller;
		this.arguments = arguments;
	}
	/*@Override
        public*/ generate(): string {
		return caller.generate() + "(" + generateNodes(arguments) + ")";
	}
}
class FieldAccess implements Value {
	parent: Value;
	property: string;
	constructor (parent: Value, property: string) {
		this.parent = parent;
		this.property = property;
	}
	/*@Override
        public*/ generate(): string {
		return parent.generate() + "." + property;
	}
}
class Symbol implements Value, Type {
	input: string;
	constructor (input: string) {
		this.input = input;
	}
	/*@Override
        public*/ generate(): string {
		return input;
	}
}
class StructureType implements Type {
	name: string;
	definitions: Map<string, Definition>;
	constructor (name: string, definitions: Map<string, Definition>) {
		this.name = name;
		this.definitions = definitions;
	}
	/*@Override
        public*/ generate(): string {
		return /*"?"*/;
	}
	/*public*/ isNamed(name: string): boolean {
		return this.name.equals(name);
	}
	/*public*/ findField(name: string): Option<Definition> {
		return definitions.get(name);
	}
}
class Frame {
	definitions: List<Definition>;
	structureTypes: List<StructureType>;
	constructor (definitions: List<Definition>, structureTypes: List<StructureType>) {
		this.definitions = definitions;
		this.structureTypes = structureTypes;
	}
	Frame(): public {/*
            this(Lists.empty(), Lists.empty());*/
	}
	/*private*/ resolveValue(name: string): Option<Definition> {
		return definitions.iter(/*)
                    */.filter(/*definition -> definition*/.name.equals(name)).next();
	}
	/*public*/ defineAllValues(definitions: List<Definition>): Frame {
		return new Frame(this.definitions.addAll(definitions), structureTypes);
	}
	/*public*/ resolveType(name: string): Option<StructureType> {
		return structureTypes.iter(/*)
                    */.filter(/*type -> type*/.isNamed(name)).next();
	}
	/*public*/ defineStructureType(structureType: StructureType): Frame {
		return new Frame(definitions, structureTypes.add(structureType));
	}
	/*public*/ defineValue(definition: Definition): Frame {
		return new Frame(definitions.add(definition), structureTypes);
	}
}
class CompileState {
	/*private final*/ stack: Stack;
	/*public*/ structures: List<string>;
	CompileState(stack: Stack, structures: List<string>): private {/*
            this.stack = stack;*//*
            this.structures = structures;*/
	}
	CompileState(): private {/*
            this(new Stack(Lists.of(new Frame())), Lists.empty());*/
	}
	/*public*/ defineAll(definitions: List<Definition>): CompileState {
		return mapLast(/*frame -> frame*/.defineAllValues(definitions));
	}
	/*public*/ defineStructureType(structureType: StructureType): CompileState {
		return mapLast(/*last -> last*/.defineStructureType(structureType));
	}
	/*private*/ mapLast(mapper: (param0 : Frame) => Frame): CompileState {
		return new CompileState(new Stack(stack.frames(/*)*/.mapLast(mapper)), structures);
	}
	/*public*/ addStructure(structure: string): CompileState {
		return new CompileState(stack, structures.add(structure));
	}
	/*public*/ defineValue(definition: Definition): CompileState {
		return new CompileState(stack.define(definition), structures);
	}
	/*public*/ enter(): CompileState {
		return new CompileState(stack.enter(), structures);
	}
	/*public*/ exit(): Option<Tuple<CompileState, List<Definition>>> {/*
            return stack.exit().map(stack -> {
                return new Tuple<>(new CompileState(stack.left, structures), stack.right);
            }*//*);*/
	}
}
class StringType implements Type {
	/*@Override
        public*/ generate(): string {
		return /*"string"*/;
	}
}
class FunctionType implements Type {
	/*private final*/ parameterTypes: List<Type>;
	/*private final*/ returnType: Type;
	FunctionType(parameterTypes: List<Type>, returnType: Type): public {/*
            this.parameterTypes = parameterTypes;*//*
            this.returnType = returnType;*/
	}
	/*@Override
        public*/ generate(): string {/*
            final var parameters = parameterTypes.iterWithIndex()
                    .map(entry -> "param" + entry.left + " : " + entry.right.generate())
                    .collect(new Joiner(", "))
                    .orElse("");*/
		return /*"*/(/*" + parameters + ") => " + returnType*/.generate();
	}
}
class TemplateType implements Type {
	base: string;
	arguments: List<Type>;
	constructor (base: string, arguments: List<Type>) {
		this.base = base;
		this.arguments = arguments;
	}
	/*@Override
        public*/ generate(): string {/*
            final var outputArguments = generateNodes(arguments);*/
		return /*base + "<" + outputArguments + ">"*/;
	}
}
class JavaMap implements Map<K, V> {
	map: java.util.Map<K, V>;
	constructor (map: java.util.Map<K, V>) {
		this.map = map;
	}
	JavaMap(): public {/*
            this(new HashMap<>());*/
	}
	/*@Override
        public*/ putAll(other: Map<K, V>): Map<K, V> {
		return other.iter(/*)*/.<Map<K, V>>fold(this, /* Map::putTuple*/);
	}
	/*@Override
        public*/ iter(): Iterator<Tuple<K, V>> {
		return new JavaList<>(new ArrayList<>(map.entrySet(/*)))
                    */.iter().map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
	}
	/*@Override
        public*/ putTuple(tuple: Tuple<K, V>): Map<K, V> {/*
            map.put(tuple.left, tuple.right);*/
		return this;
	}
	/*@Override
        public*/ put(key: K, value: V): Map<K, V> {/*
            map.put(key, value);*/
		return this;
	}
	/*@Override
        public*/ get(key: K): Option<V> {/*
            if (map.containsKey(key)) {
                return new Some<>(map.get(key));
            }*/
		return new None<>();
	}
}
class Maps {
	/*public static */ empty<K, V>(): Map<K, V> {
		return new JavaMap<>();
	}
}
class Stack {
	frames: List<Frame>;
	constructor (frames: List<Frame>) {
		this.frames = frames;
	}
	/*public*/ resolveType(name: string): Option<StructureType> {
		return frames(/*)*/.iterReversed(/*)
                    */.map(/*frame -> frame*/.resolveType(name)).flatMap(Iterators::fromOptional).next();
	}
	/*public*/ resolveValue(name: string): Option<Type> {
		return frames(/*)*/.iterReversed(/*)
                    */.map(/*frame -> frame*/.resolveValue(/*name)*/).flatMap(Iterators::fromOptional).next().map(definition -> definition.type);
	}
	/*public*/ define(definition: Definition): Stack {
		return new Stack(frames.mapLast(/*last -> last*/.defineValue(definition)));
	}
	/*public*/ enter(): Stack {
		return new Stack(frames.add(new Frame()));
	}
	/*public*/ exit(): Option<Tuple<Stack, List<Definition>>> {/*
            return frames.popLast().map(tuple -> {
                return new Tuple<>(new Stack(tuple.left), tuple.right.definitions);
            }*//*);*/
	}
}
class MapCollector implements Collector<Tuple<K, V>, Map<K, V>> {
	/*@Override
        public*/ createInitial(): Map<K, V> {
		return Maps.empty();
	}
	/*@Override
        public*/ fold(current: Map<K, V>, element: Tuple<K, V>): Map<K, V> {
		return current.putTuple(element);
	}
}
class StructureRefType implements Type {
	name: string;
	constructor (name: string) {
		this.name = name;
	}
	/*@Override
        public*/ generate(): string {
		return name;
	}
}
export class Main {/*

    private interface Value extends Caller, ValueArgument {
    }*//*

    private interface Caller extends Generating {
    }*/
	/*private interface Type extends Generating, TypeArgument {
        default*/ extract(actual: Type): Map<string, Type> {
		return Maps.empty();/*
        }

        default Type resolve(Map<String, Type> resolved) {
            return this;*//*
        }
    */
	}
	/*public static*/ main(args: /*String[]*/): void {/*
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var target = source.resolveSibling("Main.ts");

            final var input = Files.readString(source);
            final var output = compile(input);
            Files.writeString(target, output);
        }*//* catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }*/
	}
	/*private static*/ compile(input: string): string {
		return compileStatements(input, /*input1 -> compileRootSegment*/(/*input1*/, new CompileState()));
	}
	/*private static*/ compileStatements(input: string, mapper: (param0 : string) => string): string {
		return compileAll(input, mapper, /* Main::foldStatements*/, /* Main::mergeStatements*/);
	}
	/*private static*/ compileAll(input: string, mapper: (param0 : string) => string, folder: (param0 : DivideState, param1 : Character) => DivideState, merger: (param0 : StringBuilder, param1 : string) => StringBuilder): string {
		return mergeAll(/*parseAll(input*/, folder, /* mapper)*/, merger);
	}
	/*private static*/ mergeAll(elements: List<string>, merger: (param0 : StringBuilder, param1 : string) => StringBuilder): string {
		return elements.iter(/*)
                */.fold(/*new StringBuilder(*/), /*merger)
                */.toString();
	}
	/*private static */ parseAll<T>(input: string, folder: (param0 : DivideState, param1 : Character) => DivideState, mapper: (param0 : string) => T): List<T> {
		return divide(input, /*folder)
                */.iter(/*)
                */.map(mapper).collect(new ListCollector<>());
	}
	/*private static*/ mergeStatements(output: StringBuilder, compiled: string): StringBuilder {
		return output.append(compiled);
	}
	/*private static*/ divide(input: string, folder: (param0 : DivideState, param1 : Character) => DivideState): List<string> {/*
        DivideState state = new DivideState();*//*
        final var length = input.length();*//*
        var current = state;*//*
        for (var i = 0;*//* i < length;*//* i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }*/
		return current.advance().segments;
	}
	/*private static*/ foldStatements(current: DivideState, c: char): DivideState {/*
        final var appended = current.append(c);*//*
        if (c == ';*//*' && appended.isLevel()) {
            return appended.advance();
        }*//*
        if (c == '*/
	}/*' && appended.isShallow()) {
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

    private static String compileRootSegment(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileRootStructure(input, state)
                .orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Option<String> compileRootStructure(String input, CompileState state) {
        return compileStructure(input, "class ", "class", state).map(tuple -> {
            final var joined = join(tuple.right.structures);
            return tuple.left + joined;
        });
    }*//*

    private static String join(List<String> list) {
        return list.iter()
                .collect(new Joiner())
                .orElse("");
    }*//*

    private static Option<Tuple<String, CompileState>> compileStructure(String input, String keyword, String targetInfix, CompileState state) {
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
        final var modifiers = modifiersString.contains("public") ? "export " : "";

        return assembleStructureWithImplements(targetInfix, state, inputContent, beforeContent, modifiers);
    }*//*

    private static Option<Tuple<String, CompileState>> assembleStructureWithImplements(String targetInfix, CompileState state, String inputContent, String beforeContent, String modifiers) {
        final var implementsIndex = beforeContent.lastIndexOf(" implements ");
        if (implementsIndex >= 0) {
            final var beforeImplements = beforeContent.substring(0, implementsIndex);
            final var implementsString = beforeContent.substring(implementsIndex + " implements ".length());
            final var implementsTypes = parseValuesString(implementsString, Main::parseType);

            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeImplements, implementsTypes);
        }
        else {
            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeContent, Lists.empty());
        }
    }*//*

    private static Option<Tuple<String, CompileState>> assembleStructureWithParameters(String targetInfix, CompileState state, String inputContent, String modifiers, String beforeContent, List<Type> implementsTypes) {
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

                if (!fields.isEmpty()) {
                    return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new Some<>(fields), name);
                }
            }
        }

        return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new None<>(), beforeContent);
    }*//*

    private static Option<Tuple<String, CompileState>> getTupleOption(
            String targetInfix,
            CompileState state,
            String inputContent,
            String modifiers,
            List<Type> implementsTypes,
            Option<List<Definition>> maybeFields,
            String name) {
        final String beforeBody;
        if (maybeFields.isPresent()) {
            final var fields = maybeFields.get();
            final var output1 = fields.iter()
                    .map(Definition::generate)
                    .fold(new StringBuilder(), Main::mergeValues);

            final var outputParams = output1.toString();
            final var generatedFields = fields.iter()
                    .map(Definition::generate)
                    .map(element -> "\n\t" + element + ";")
                    .collect(new Joiner())
                    .orElse("");

            final var assignments = joinConstructorAssignments(fields);

            beforeBody = generatedFields + "\n\tconstructor (" + outputParams + ") {" + assignments + "\n\t}";
        }
        else {
            beforeBody = "";
        }

        final var stripped = name.strip();
        if (stripped.endsWith(">")) {
            final var typeParamsStart = stripped.indexOf("<");
            if (typeParamsStart >= 0) {
                final var name1 = stripped.substring(0, typeParamsStart);
                return assembleStructure(targetInfix, state, inputContent, modifiers, implementsTypes, name1, beforeBody, maybeFields);
            }
        }

        return assembleStructure(targetInfix, state, inputContent, modifiers, implementsTypes, name, beforeBody, maybeFields);
    }*//*

    private static Option<Tuple<String, CompileState>> assembleStructure(String targetInfix, CompileState state, String inputContent, String modifiers, List<Type> implementsTypes, String name, String beforeBody, Option<List<Definition>> maybeFields) {
        final var strippedName = name.strip();
        if (!isSymbol(strippedName)) {
            return new None<>();
        }

        final var classSegmentsTuple = joinClassSegments(inputContent, state.enter());
        final var classSegmentsOutput = classSegmentsTuple.left.toString();
        final var classSegmentsState = classSegmentsTuple.right;

        final var maybeExited = classSegmentsState.exit();
        if (maybeExited.isPresent()) {
            final var exited = maybeExited.get();
            final var right = exited.right
                    .iter()
                    .map(definition -> new Tuple<>(definition.name, definition))
                    .collect(new MapCollector<>());

            final var maybeWithConstructorType = maybeFields.map(fields -> {
                final var constructorTypes = fields.iter()
                        .map(Definition::type)
                        .collect(new ListCollector<>());

                return right.put("new", new Definition(new FunctionType(constructorTypes, new StructureRefType(name)), "new"));
            }).orElse(right);

            final var structureType = new StructureType(strippedName, maybeWithConstructorType);
            final var defined = exited.left.defineStructureType(structureType);
            final var outputContent = beforeBody + classSegmentsOutput;
            final var joinedImplements = implementsTypes.isEmpty() ? "" : " implements " + generateNodes(implementsTypes);
            var generated = modifiers + targetInfix + " " + strippedName + joinedImplements + " {" + outputContent + "\n}\n";

            return new Some<>(new Tuple<>("", defined.addStructure(generated)));
        }
        else {
            return new None<>();
        }
    }*//*

    private static String joinConstructorAssignments(List<Definition> fields) {
        return fields.iter()
                .map(field -> {
                    final var fieldName = field.name;
                    final var content = "this." + fieldName + " = " + fieldName;
                    return generateStatement(content);
                })
                .collect(new Joiner())
                .orElse("");
    }*//*

    private static Tuple<StringBuilder, CompileState> joinClassSegments(String inputContent, CompileState state) {
        return divide(inputContent, Main::foldStatements)
                .iter()
                .fold(new Tuple<>(new StringBuilder(), state), (tuple, element) -> {
                    final var compiled = compileClassSegment(element, tuple.right);
                    final var append = tuple.left.append(compiled.left);
                    return new Tuple<>(append, compiled.right);
                });
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

    private static String joinWithDelimiter(List<String> list, String delimiter) {
        return list.iter().collect(new Joiner(delimiter)).orElse("");
    }*//*

    private static Tuple<String, CompileState> compileClassSegment(String input, CompileState state) {
        return compileWhitespaceWithState(input, state)
                .or(() -> compileStructure(input, "record ", "class", state))
                .or(() -> compileStructure(input, "class ", "class", state))
                .or(() -> compileStructure(input, "interface ", "interface", state))
                .or(() -> compileField(input, state))
                .or(() -> compileMethod(input, state))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), state));
    }*//*

    private static Option<Tuple<String, CompileState>> compileField(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return compileSimpleDefinition(content).map(definition -> new Tuple<String, CompileState>("\n\t" + definition + ";", state));
    }*//*

    private static Option<String> compileSimpleDefinition(String content) {
        return parseDefinition(content).map(Definition::generate);
    }*//*

    private static Option<Tuple<String, CompileState>> compileWhitespaceWithState(String input, CompileState state) {
        return compileWhitespace(input).map(node -> new Tuple<>(node, state));
    }*//*

    private static Option<String> compileWhitespace(String input) {
        return parseWhitespace(input).map(Whitespace::generate);
    }*//*

    private static Option<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>(new Whitespace());
        }
        else {
            return new None<>();
        }
    }*//*

    private static Option<Tuple<String, CompileState>> compileMethod(String input, CompileState state) {
        final var paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        final var inputDefinition = input.substring(0, paramStart);
        final var withParams = input.substring(paramStart + "(".length());
        final var paramEnd = withParams.indexOf(")");
        if (paramEnd < 0) {
            return new None<>();
        }

        final var inputParams = withParams.substring(0, paramEnd);
        final var inputAfterParams = withParams.substring(paramEnd + ")".length()).strip();

        final var maybeOutputDefinition = parseDefinition(inputDefinition);
        if (!maybeOutputDefinition.isPresent()) {
            return new None<>();
        }

        final var outputDefinition = maybeOutputDefinition.get();
        final var parameters = parseAll(inputParams, Main::foldValues, Main::parseParameter)
                .iter()
                .map(Main::retainDefinition)
                .flatMap(Iterators::fromOptional)
                .collect(new ListCollector<>());

        final var paramTypes = parameters.iter()
                .map(Definition::type)
                .collect(new ListCollector<>());

        final var outputParams = generateNodes(parameters);
        if (inputAfterParams.equals(";")) {
            return assembleMethod(outputDefinition, outputParams, ";", state, paramTypes);
        }

        if (!inputAfterParams.startsWith("{") || !inputAfterParams.endsWith("}")) {
            return new None<>();
        }

        final var content = inputAfterParams.substring(1, inputAfterParams.length() - 1);
        final CompileState defined = state.defineAll(parameters);
        final String outputAfterParams = compileStatements(content, input1 -> compileFunctionSegments(input1, defined));
        return assembleMethod(outputDefinition, outputParams, " {" + outputAfterParams + "\n\t}", state, paramTypes);
    }*//*

    private static Some<Tuple<String, CompileState>> assembleMethod(Definition outputDefinition, String outputParams, String outputAfterParams, CompileState state, List<Type> paramTypes) {
        final var header = outputDefinition.generateWithAfterName("(" + outputParams + ")");
        final var generated = "\n\t" + header + outputAfterParams;
        return new Some<>(new Tuple<>(generated, state.defineValue(outputDefinition.mapType(type -> {
            return new FunctionType(paramTypes, type);
        }))));
    }*//*

    private static String compileFunctionSegments(String input, CompileState state) {
        return compileWhitespace(input)
                .or(() -> compileFunctionStatement(input, state))
                .orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Option<String> compileFunctionStatement(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        return compileFunctionStatementValue(withoutEnd, state).map(value -> "\n\t\t" + value + ";");
    }*//*

    private static Option<String> compileFunctionStatementValue(String withoutEnd, CompileState state) {
        if (withoutEnd.startsWith("return ")) {
            final var value = withoutEnd.substring("return ".length());
            final var generated = parseValue(value, state);
            return new Some<>("return " + generated.generate());
        }
        else {
            return new None<>();
        }
    }*//*

    private static Value parseValue(String input, CompileState state) {
        return parseInvocation(input, state).<Value>map(value -> value)
                .or(() -> parseAccess(input, state).map(value -> value))
                .or(() -> parseSymbol(input).map(value -> value))
                .orElseGet(() -> new Placeholder(input));
    }*//*

    private static Option<FieldAccess> parseAccess(String input, CompileState state) {
        var stripped = input.strip();
        final var separator = stripped.lastIndexOf(".");
        if (separator >= 0) {
            final var parentString = stripped.substring(0, separator);
            final var property = stripped.substring(separator + ".".length());
            final var parent = parseValue(parentString, state);
            return new Some<>(new FieldAccess(parent, property));
        }

        return new None<>();
    }*//*

    private static Option<Invocation> parseInvocation(String input, CompileState state) {
        var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var argumentsStart = withoutEnd.indexOf("(");
            if (argumentsStart >= 0) {
                final var callerString = withoutEnd.substring(0, argumentsStart).strip();
                final var argumentsString = withoutEnd.substring(argumentsStart + "(".length());
                final var arguments = parseValuesString(argumentsString, input1 -> parseArgument(input1, state))
                        .iter()
                        .map(Main::retainValue)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                final var caller = mapCaller(state, callerString);
                return new Some<>(parseAfterInvocation(state, caller, arguments));
            }
        }

        return new None<>();
    }*//*

    private static Invocation parseAfterInvocation(CompileState state, Caller caller, List<Value> arguments) {
        if (caller instanceof Construction(var type)) {
            if (type instanceof TemplateType(String base, List<Type> templateArgumentTypes)) {
                if (templateArgumentTypes.isEmpty()) {
                    final var maybeStructureType = state.stack.resolveType(base);
                    if (maybeStructureType.isPresent()) {
                        final var structureType = maybeStructureType.get();
                        final var maybeConstructorDefinition = structureType.findField("new");
                        if (maybeConstructorDefinition.isPresent()) {
                            final var constructorDefinition = maybeConstructorDefinition.get();
                            final var constructorDefinitionType = constructorDefinition.type;
                            if (constructorDefinitionType instanceof FunctionType functionalConstructorDefinition) {
                                final var constructorArgumentTypes = functionalConstructorDefinition.parameterTypes;

                                final var argumentTypes = arguments.iter()
                                        .map(argument -> resolveValue(argument, state))
                                        .flatMap(Iterators::fromOptional)
                                        .collect(new ListCollector<>());

                                final var resolved = constructorArgumentTypes.iter()
                                        .zip(argumentTypes.iter())
                                        .map(pair -> pair.left.extract(pair.right))
                                        .fold(Maps.<String, Type>empty(), Map::putAll);

                                final var actualArgumentTypes = argumentTypes.iter()
                                        .map(argument -> argument.resolve(resolved))
                                        .collect(new ListCollector<>());

                                final var actualTemplateType = new TemplateType(base, actualArgumentTypes);
                                return new Invocation(new Construction(actualTemplateType), arguments);
                            }
                        }
                    }
                }
            }
        }
        return new Invocation(caller, arguments);
    }*//*

    private static Option<Type> resolveValue(Value argument, CompileState state) {
        return new None<>();
    }*//*

    private static Option<Value> retainValue(ValueArgument argument) {
        if (argument instanceof Value value) {
            return new Some<>(value);
        }
        else {
            return new None<>();
        }
    }*//*

    private static ValueArgument parseArgument(String input, CompileState state) {
        return parseWhitespace(input).<ValueArgument>map(value -> value)
                .orElseGet(() -> parseValue(input, state));
    }*//*

    private static Option<Symbol> parseSymbol(String input) {
        final var stripped = input.strip();
        if (isSymbol(stripped)) {
            return new Some<>(new Symbol(stripped));
        }
        return new None<>();
    }*//*

    private static Caller mapCaller(CompileState state, String callerString) {
        final var caller = parseCaller(callerString, state);

        if (caller instanceof FieldAccess access) {
            final var parent = access.parent;
            if (parent instanceof Symbol(String value)) {
                final var maybeType = state.stack.resolveValue(value);
                if (maybeType.isPresent()) {
                    final var type = maybeType.get();
                    if (type instanceof FunctionType) {
                        return parent;
                    }
                }
            }
        }

        return caller;
    }*//*

    private static <T extends Generating> String generateNodes(List<T> arguments) {
        final var generated = arguments.iter()
                .map(Generating::generate)
                .collect(new ListCollector<>());

        return mergeAll(generated, Main::mergeValues);
    }*//*

    private static Caller parseCaller(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("new ")) {
            final var afterNew = stripped.substring("new ".length());
            final var type = parseType(afterNew);
            return new Construction(type);
        }

        return parseValue(stripped, state);
    }*//*

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (!cache.isEmpty()) {
            cache.append(", ");
        }
        return cache.append(element);
    }*//*

    private static DivideState foldValues(DivideState state, char c) {
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
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(beforeName), name));
        }

        final var popped = maybePopped.get();
        final var beforeTypeDivisions = popped.left;
        final var type = popped.right;
        final var compiledType = parseType(type);

        if (beforeTypeDivisions.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(type), name));
        }

        final var beforeType = joinWithDelimiter(beforeTypeDivisions, " ");
        return new Some<>(assembleDefinition(beforeType, compiledType, name));
    }*//*

    private static DivideState foldTypeSeparator(DivideState state, char c) {
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

    private static Definition assembleDefinition(String beforeType, Type compiledType, String name) {
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                final var beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                final var typeParamsString = withoutEnd.substring(typeParamStart + "<".length());
                final var typeParams = parseValuesString(typeParamsString, String::strip);

                final Option<String> beforeTypeOptional;
                beforeTypeOptional = beforeTypeParams.isEmpty() ? new None<>() : new Some<>(generatePlaceholder(beforeTypeParams));

                return new Definition(beforeTypeOptional, typeParams, compiledType, name);
            }
        }

        return new Definition(new Some<>(generatePlaceholder(beforeType)), Lists.empty(), compiledType, name);
    }*//*

    private static Type parseType(String input) {
        final var stripped = input.strip();
        if (stripped.equals("String")) {
            return new StringType();
        }

        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var argumentsStart = withoutEnd.indexOf("<");
            if (argumentsStart >= 0) {
                final var base = withoutEnd.substring(0, argumentsStart).strip();
                final var inputArguments = withoutEnd.substring(argumentsStart + 1);
                final var elements = parseValuesString(inputArguments, Main::parseTypeArgument)
                        .iter()
                        .map(Main::retainType)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                if (base.equals("Supplier")) {
                    List<Type> parameterTypes = Lists.empty();
                    return new FunctionType(parameterTypes, elements.get(0));
                }

                if (base.equals("Function")) {
                    List<Type> parameterTypes = Lists.of(elements.get(0));
                    return new FunctionType(parameterTypes, elements.get(1));
                }

                if (base.equals("BiFunction")) {
                    List<Type> parameterTypes = Lists.of(elements.get(0), elements.get(1));
                    return new FunctionType(parameterTypes, elements.get(2));
                }

                return new TemplateType(base, elements);
            }
        }

        return parseSymbol(stripped).<Type>map(value -> value)
                .orElseGet(() -> new Placeholder(input));
    }*//*

    private static Option<Type> retainType(TypeArgument argument) {
        if (argument instanceof Type type) {
            return new Some<>(type);
        }
        else {
            return new None<>();
        }
    }*//*

    private static TypeArgument parseTypeArgument(String input) {
        return parseWhitespace(input)
                .<TypeArgument>map(whitespace -> whitespace)
                .orElseGet(() -> parseType(input));
    }*//*

    private static <T> List<T> parseValuesString(String input, Function<String, T> mapper) {
        return parseAll(input, Main::foldValues, mapper);
    }*//*

    private static boolean isSymbol(String input) {
        final var length = input.length();
        if (length == 0) {
            return false;
        }

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