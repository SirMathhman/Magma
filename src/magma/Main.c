/*C*/ createInitial(/**/)/*;*//*C*/ fold(/*C current, T element*/)/*;*//*private*/struct Collector<T, C> {/*C*/ createInitial(/**/)/*;*//*C*/ fold(/*C current, T element*/)/*;*/
};
/*<R>*/ /*Iterator<R>*/ map(/*Function<T, R> mapper*/)/*;*//*<C>*/ /*C*/ collect(/*Collector<T, C> collector*/)/*;*//*<R>*/ /*R*/ fold(/*R initial, BiFunction<R, T, R> folder*/)/*;*//*private*/struct Iterator<T> {/*<R>*/ /*Iterator<R>*/ map(/*Function<T, R> mapper*/)/*;*//*<C>*/ /*C*/ collect(/*Collector<T, C> collector*/)/*;*//*<R>*/ /*R*/ fold(/*R initial, BiFunction<R, T, R> folder*/)/*;*/
};
/*List<T>*/ addLast(/*T element*/)/*;*//*Iterator<T>*/ iter(/**/)/*;*//*List<T>*/ addAllLast(/*List<T> others*/)/*;*//*private*/struct List<T> {/*List<T>*/ addLast(/*T element*/)/*;*//*Iterator<T>*/ iter(/**/)/*;*//*List<T>*/ addAllLast(/*List<T> others*/)/*;*/
};
/*Optional<T>*/ next(/**/)/*;*//*private*/struct Head<T> {/*Optional<T>*/ next(/**/)/*;*/
};
/*

        public RangeHead(int length) */{
	/*this.length*/ /*=*/ length;/*
            this.count = 0;*/
};
/*if (this.count < this.length) */{/*final var value = this.count;*//*
                this.count++;*//*
                return Optional.of(value);*/
};
/*
            else */{/*return Optional.empty();*/
};
/*

        @Override
        public Optional<Integer> next() */{
};
/*private static*/struct RangeHead implements Head<Integer> {
	/*private final*/ /*int*/ length;
	/*private*/ /*int*/ count;
};
/*@Override
        public <R> Iterator<R> map(Function<T, R> mapper) */{/*return new HeadedIterator<>(() -> this.head.next().map(mapper));*/
};
/*

        @Override
        public <C> C collect(Collector<T, C> collector) */{/*return this.fold(collector.createInitial(), collector::fold);*/
};
/*
                if (folded.isPresent()) */{/*current = folded.get();*/
};
/*
                else */{
	/*return*/ current;
};
/*
            while (true) */{
	/*R finalCurrent*/ /*=*/ current;/*
                final var folded = this.head.next().map(next -> folder.apply(finalCurrent, next));*/
};
/*

        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) */{
	/*var current*/ /*=*/ initial;
};
/*

    private record HeadedIterator<T>(Head<T> head) implements Iterator<T> */{
};
/*public JavaList() */{/*this(new ArrayList<>());*/
};
/*

        @Override
        public List<T> addLast(T element) */{/*this.elements.add(element);*/
	/*return*/ this;
};
/*

        @Override
        public Iterator<T> iter() */{/*return new HeadedIterator<>(new RangeHead(this.elements.size())).map(this.elements::get);*/
};
/*

        @Override
        public List<T> addAllLast(List<T> others) */{/*return others.iter().<List<T>>fold(this, List::addLast);*/
};
/*

    private record JavaList<T>(java.util.List<T> elements) implements List<T> */{
};
/*public static <T> List<T> empty() */{/*return new JavaList<T>();*/
};
/*

        public static <T> List<T> of(T... elements) */{/*return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));*/
};
/*private static*/struct Lists {
};
/*

        private State(List<String> segments, StringBuilder buffer, int depth) */{
	/*this.segments*/ /*=*/ segments;
	/*this.buffer*/ /*=*/ buffer;
	/*this.depth*/ /*=*/ depth;
};
/*

        public State() */{/*this(Lists.empty(), new StringBuilder(), 0);*/
};
/*

        private boolean isLevel() */{/*return this.depth == 0;*/
};
/*

        private State append(char c) */{/*this.buffer.append(c);*/
	/*return*/ this;
};
/*this.buffer =*/ /*new*/ StringBuilder(/**/)/*;*//*

        private State advance() */{/*this.segments = this.segments.addLast(this.buffer.toString());*//*this.buffer =*/ /*new*/ StringBuilder(/**/)/*;*/
	/*return*/ this;
};
/*

        private State enter() */{/*this.depth = this.depth + 1;*/
	/*return*/ this;
};
/*

        private State exit() */{/*this.depth = this.depth - 1;*/
	/*return*/ this;
};
/*

        public boolean isShallow() */{/*return this.depth == 1;*/
};
/*private static*/struct State {
	/*private*/ /*List<String>*/ segments;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ /*int*/ depth;
};
/*

    private record Tuple<A, B>(A left, B right) */{
};
/*@Override
        public Optional<String> createInitial() */{/*return Optional.empty();*/
};
/*

        @Override
        public Optional<String> fold(Optional<String> current, String element) */{/*return Optional.of(current.map(inner -> inner + element).orElse(element));*/
};
/*private static*/struct Joiner implements Collector<String, Optional<String>> {
};
/*@Override
        public Tuple<AC, BC> createInitial() */{/*return new Tuple<>(this.leftCollector.createInitial(), this.rightCollector.createInitial());*/
};
/*

        @Override
        public Tuple<AC, BC> fold(Tuple<AC, BC> current, Tuple<A, B> element) */{/*return new Tuple<>(this.leftCollector.fold(current.left, element.left), this.rightCollector.fold(current.right, element.right));*/
};
/*

    private record TupleCollector<A, AC, B, BC>(Collector<A, AC> leftCollector, Collector<B, BC> rightCollector)
            implements Collector<Tuple<A, B>, Tuple<AC, BC>> */{
};
/*@Override
        public List<T> createInitial() */{/*return Lists.empty();*/
};
/*

        @Override
        public List<T> fold(List<T> current, List<T> element) */{/*return current.addAllLast(element);*/
};
/*private static*/struct ListBulkCollector<T> implements Collector<List<T>, List<T>> {
};
/*final var string*/ /*=*/ compile(/*input*/)/*;*//*try */{/*final var source = Paths.get(".", "src", "magma", "Main.java");*//*
            final var input = Files.readString(source);*//*
            final var target = source.resolveSibling("Main.c");*//*final var string*/ /*=*/ compile(/*input*/)/*;*//*
            Files.writeString(target, string);*/
};
/* catch (IOException e) */{/*//noinspection CallToPrintStackTrace
            e.printStackTrace();*/
};
/*

    public static void main(String[] args) */{
};
/*return*/ compileStatements(/*input, Main::compileRootSegment*/)/*;*//*

    private static String compile(String input) */{/*return*/ compileStatements(/*input, Main::compileRootSegment*/)/*;*/
};
/*return*/ divide(/*input*/)/*
                .iter()
                .map(mapper)
                .collect(new Joiner())
                .orElse("");*//*

    private static String compileStatements(String input, Function<String, String> mapper) */{/*return*/ divide(/*input*/)/*
                .iter()
                .map(mapper)
                .collect(new Joiner())
                .orElse("");*/
};
/*var current =*/ /*new*/ State(/**/)/*;*//*current*/ /*=*/ fold(/*current, c*/)/*;*//* i++) */{/*final var c = input.charAt(i);*//*current*/ /*=*/ fold(/*current, c*/)/*;*/
};
/*

    private static List<String> divide(String input) */{/*var current =*/ /*new*/ State(/**/)/*;*//*
        for (var i = 0;*//* i < input.length();*//*

        return current.advance().segments;*/
};
/*' && appended.isLevel()) */{/*return appended.advance();*/
};
/*

    private static State fold(State state, char c) */{/*final var appended = state.append(c);*//*
        if (c == ';*//*
        if (c == '*/
};
/*' && appended.isShallow()) */{/*return appended.advance().exit();*/
};
/*') */{/*return appended.enter();*/
};
/*
        if (c == '*/{/*
        if (c == '*/
};
/*') */{/*return appended.exit();*/
};
/*public*/struct Main {
	/*return*/ appended;
};
/*
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) */{/*return "";*/
};
/*

        return compileClass(input)
                .map(tuple -> */{/*final var joined = tuple.left
                            .iter()
                            .collect(new Joiner())
                            .orElse("");*//*

                    return joined + tuple.right;*/
};
/*

    private static String compileRootSegment(String input) */{/*final var stripped = input.strip();*//*)
                .orElseGet(() -> generatePlaceholder(input));*/
};
/*
            final var withEnd = input.substring(contentStart + "*/{/*".length()).strip();*//*
            if (withEnd.endsWith("*/
};
/*final var header*/ /*=*/ compileClassDefinition(/*beforeContent*/)/*;*//*")) */{/*final var header*/ /*=*/ compileClassDefinition(/*beforeContent*/)/*;*//*
                final var inputContent = withEnd.substring(0, withEnd.length() - "*/
};
/*final var segments*/ /*=*/ divide(/*inputContent*/)/*;*//*

                final var generated = header + "*/{/*" + output + "\n*/
};
/*
        if (contentStart >= 0) */{/*final var beforeContent = input.substring(0, contentStart);*//*".length());*//*final var segments*/ /*=*/ divide(/*inputContent*/)/*;*//*

                final var tuple = segments.iter()
                        .map(Main::compileClassSegment)
                        .collect(new TupleCollector<>(new ListBulkCollector<>(), new Joiner()));*//*

                final var others = tuple.left;*//*
                final var output = tuple.right.orElse("");*//*;*//*\n";*//*
                return Optional.of(new Tuple<>(others.addLast(generated), ""));*/
};
/*final var contentStart = input.indexOf('*/{/*');*/
};
/*


    private static Optional<Tuple<List<String>, String>> compileClass(String input) */{/*

        return Optional.empty();*/
};
/*return*/ compileWhitespace(/*input*/)/*
                .or(() -> compileField(input))
                .or(() -> compileClass(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(Lists.empty(), generatePlaceholder(input)));*//*

    private static Tuple<List<String>, String> compileClassSegment(String input) */{/*return*/ compileWhitespace(/*input*/)/*
                .or(() -> compileField(input))
                .or(() -> compileClass(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(Lists.empty(), generatePlaceholder(input)));*/
};
/*final var maybeDefinition*/ /*=*/ compileDefinition(/*beforeParams*/)/*;*//*
                if (maybeDefinition.isPresent()) */{/*final var generated = maybeDefinition.get() + "(" + generatePlaceholder(params) + ")" + generatePlaceholder(content);*//*
                    return Optional.of(new Tuple<>(Lists.of(generated), generated));*/
};
/*
            if (paramEnd >= 0) */{/*final var params = withParams.substring(0, paramEnd);*//*
                final var content = withParams.substring(paramEnd + ")".length());*//*final var maybeDefinition*/ /*=*/ compileDefinition(/*beforeParams*/)/*;*/
};
/*
        if (paramStart >= 0) */{/*final var beforeParams = input.substring(0, paramStart);*//*
            final var withParams = input.substring(paramStart + "(".length());*//*
            final var paramEnd = withParams.indexOf(")");*/
};
/*

    private static Optional<Tuple<List<String>, String>> compileMethod(String input) */{/*final var paramStart = input.indexOf("(");*//*

        return Optional.empty();*/
};
/*if (input.isBlank()) */{/*return Optional.of(new Tuple<>(Lists.empty(), ""));*/
};
/*
        else */{/*return Optional.empty();*/
};
/*

    private static Optional<Tuple<List<String>, String>> compileWhitespace(String input) */{
};
/*
            return compileDefinition(withoutEnd).map(generated -> */{/*return new Tuple<>(Lists.empty(), "\n\t" + generated + ";*//*");*/
};
/*")) */{/*final var withoutEnd = stripped.substring(0, stripped.length() - ";*//*".length());*//*);*/
};
/*

    private static Optional<Tuple<List<String>, String>> compileField(String input) */{/*final var stripped = input.strip();*//*
        if (stripped.endsWith(";*//*

        return Optional.empty();*/
};
/*final var generated*/ /*=*/ compileDefinitionWithType(/*beforeName, name*/)/*;*//*

            if (isSymbol(name)) */{/*final var generated*/ /*=*/ compileDefinitionWithType(/*beforeName, name*/)/*;*//*
                return Optional.of(generated);*/
};
/*
        if (nameSeparator >= 0) */{/*final var beforeName = withoutEnd.substring(0, nameSeparator).strip();*//*
            final var name = withoutEnd.substring(nameSeparator + " ".length()).strip();*/
};
/*

    private static Optional<String> compileDefinition(String withoutEnd) */{/*final var nameSeparator = withoutEnd.lastIndexOf(" ");*//*
        return Optional.empty();*/
};
/*
            if (Character.isLetter(c)) */{/*continue;*/
};
/* i++) */{/*final var c = input.charAt(i);*/
	/*return*/ false;
};
/*

    private static boolean isSymbol(String input) */{/*for (var i = 0;*//* i < input.length();*/
	/*return*/ true;
};
/*String type1*/ /*=*/ compileType(/*type*/)/*;*//*return*/ generateDefinition(/*Optional.of(beforeType*/)/*, type1, name);*//*
        if (typeSeparator >= 0) */{/*final var beforeType = beforeName.substring(0, typeSeparator);*//*
            final var type = beforeName.substring(typeSeparator + " ".length());*//*String type1*/ /*=*/ compileType(/*type*/)/*;*//*return*/ generateDefinition(/*Optional.of(beforeType*/)/*, type1, name);*/
};
/*String type*/ /*=*/ compileType(/*beforeName*/)/*;*//*return*/ generateDefinition(/*Optional.empty(*/)/*, type, name);*//*
        else */{/*String type*/ /*=*/ compileType(/*beforeName*/)/*;*//*return*/ generateDefinition(/*Optional.empty(*/)/*, type, name);*/
};
/*

    private static String compileDefinitionWithType(String beforeName, String name) */{/*final var typeSeparator = beforeName.lastIndexOf(" ");*/
};
/*

    private static String generateDefinition(Optional<String> maybeBeforeType, String type, String name) */{/*final var beforeType = maybeBeforeType
                .map(Main::generatePlaceholder)
                .map(inner -> inner + " ")
                .orElse("");*/
	/*return beforeType + type + " "*/ /*+*/ name;
};
/*return*/ generatePlaceholder(/*type*/)/*;*//*

    private static String compileType(String type) */{/*return*/ generatePlaceholder(/*type*/)/*;*/
};
/*return*/ compileClassDefinitionWithKeyword(/*input, "class "*/)/*
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .orElseGet(() -> generatePlaceholder(input));*//*

    private static String compileClassDefinition(String input) */{/*return*/ compileClassDefinitionWithKeyword(/*input, "class "*/)/*
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .orElseGet(() -> generatePlaceholder(input));*/
};
/*
        if (classIndex < 0) */{/*return Optional.empty();*/
};
/*

    private static Optional<String> compileClassDefinitionWithKeyword(String input, String keyword) */{/*final var classIndex = input.indexOf(keyword);*//*

        final var beforeKeyword = input.substring(0, classIndex).strip();*//*
        final var afterKeyword = input.substring(classIndex + keyword.length());*//*
        return Optional.of(generatePlaceholder(beforeKeyword) + "struct " + afterKeyword);*/
};
/*

    private static String generatePlaceholder(String input) */{/*return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";*/
};
/*
}
*/