/*private*/struct Collector<T, C> {
	/*C*/ createInitial();
	/*C fold(C current,*/ /*T*/ element);
	/*}

    private interface Iterator<T> {
        <R> Iterator<R> map(Function<T,*/ /*R>*/ mapper);
	/*<C> C collect(Collector<T,*/ /*C>*/ collector);
	/*<R> R fold(R initial, BiFunction<R, T,*/ /*R>*/ folder);
	/*}

    private interface List<T> {
        List<T>*/ /*addLast(T*/ element);
	/*Iterator<T>*/ iter();
	/*List<T>*/ /*addAllLast(List<T>*/ others);
	/*}

    private interface Head<T> {
       */ /*Optional<T>*/ next();
	/*}

    private static class RangeHead implements Head<Integer> {
        private final*/ /*int*/ length;
	/*private*/ /*int*/ count;
	/*public RangeHead(int length) {
            this.length = length;
            this.count = 0;
        }

        @Override
        public Optional<Integer> next() {
            if (this.count < this.length) {
                final var value = this.count;
                this.count++;
                return Optional.of(value);
            }
            else {
                return Optional.empty();
            }
        }
    }

    private record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> Iterator<R> map(Function<T, R> mapper) {
            return new HeadedIterator<>(() -> this.head.next().map(mapper));
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        }

        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) {
            var current = initial;
            while (true) {
                R finalCurrent = current;
                final var folded = this.head.next().map(next -> folder.apply(finalCurrent, next));
                if (folded.isPresent()) {
                    current = folded.get();
                }
                else {
                    return current;
                }
            }
        }
    }

    private record JavaList<T>(java.util.List<T> elements) implements List<T> {
        public JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public List<T> addLast(T element) {
            this.elements.add(element);
            return this;
        }

        @Override
        public Iterator<T> iter() {
            return new HeadedIterator<>(new RangeHead(this.elements.size())).map(this.elements::get);
        }

        @Override
        public List<T> addAllLast(List<T> others) {
            return others.iter().<List<T>>fold(this, List::addLast);
        }
    }

    private static class Lists {
        public static <T> List<T> empty() {
            return new JavaList<T>();
        }
    }

    private static class State {
        private*/ /*List<String>*/ segments;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ /*int*/ depth;
	/*private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(Lists.empty(), new StringBuilder(), 0);
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private State append(char c) {
            this.buffer.append(c);
            return this;
        }

        private State advance() {
            this.segments = this.segments.addLast(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private State enter() {
            this.depth = this.depth + 1;
            return this;
        }

        private State exit() {
            this.depth = this.depth - 1;
            return this;
        }
    }

    private record Tuple<A, B>(A left, B right) {
    }

    private static class Joiner implements Collector<String, Optional<String>> {
        @Override
        public Optional<String> createInitial() {
            return Optional.empty();
        }

        @Override
        public Optional<String> fold(Optional<String> current, String element) {
            return Optional.of(current.map(inner -> inner + element).orElse(element));
        }
    }

    private record TupleCollector<A, AC, B, BC>(Collector<A, AC> leftCollector, Collector<B, BC> rightCollector)
            implements Collector<Tuple<A, B>, Tuple<AC, BC>> {
        @Override
        public Tuple<AC, BC> createInitial() {
            return new Tuple<>(this.leftCollector.createInitial(), this.rightCollector.createInitial());
        }

        @Override
        public Tuple<AC, BC> fold(Tuple<AC, BC> current, Tuple<A, B> element) {
            return new Tuple<>(this.leftCollector.fold(current.left, element.left), this.rightCollector.fold(current.right, element.right));
        }
    }

    private static class ListBulkCollector<T> implements Collector<List<T>, List<T>> {
        @Override
        public List<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List<T> fold(List<T> current, List<T> element) {
            return current.addAllLast(element);
        }
    }

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var string = compile(input);
            Files.writeString(target, string);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return*/ /*compileStatements(input,*/ Main::compileRootSegment);
	/*}

    private static String compileStatements(String input, Function<String, String> mapper) {
        return divide(input)
                .iter()
                .map(mapper)
                .collect(new*/ /*Joiner())*/ .orElse("");
	/*}

    private static List<String> divide(String input) {
        var current =*/ /*new*/ State();
	/*for (var i*/ /*=*/ 0;
	/*i*/ /*<*/ input.length();
	/*i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

       */ /*return*/ current.advance().segments;
	/*}

    private static State fold(State state, char c) {
        final var appended*/ /*=*/ state.append(c);
	/*if (c*/ /*==*/ ';
	/*' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
       */ /*return*/ appended;
	/*}

    private static String compileRootSegment(String input) {
        final var stripped*/ /*=*/ input.strip();
	/*if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileClass(input)
                .map(tuple -> {
                    final var joined = tuple.left
                            .iter()
                            .collect(new Joiner())
                            .orElse("");

                    return joined + tuple.right;
                })
                .orElseGet(()*/ /*->*/ generatePlaceholder(input));
	/*}


    private static Optional<Tuple<List<String>, String>> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var header = compileClassDefinition(beforeContent);
                final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());

                final var segments = divide(inputContent);

                final var tuple = segments.iter()
                        .map(Main::compileClassSegment)
                        .collect(new TupleCollector<>(new ListBulkCollector<>(), new Joiner()));

                final var others = tuple.left;
                final var output = tuple.right.orElse("");

                final var generated = header + "{" + output + "\n};\n";
                return Optional.of(new Tuple<>(others.addLast(generated), ""));
            }
        }

       */ /*return*/ Optional.empty();
	/*}

    private static Tuple<List<String>, String> compileClassSegment(String input) {
        return compileField(input)
                .or(() -> compileClass(input))
                .orElseGet(() -> new*/ /*Tuple<>(Lists.empty(),*/ generatePlaceholder(input)));
	/*}

    private static Optional<Tuple<List<String>, String>> compileField(String input) {
        final var stripped*/ /*=*/ input.strip();
	/*if*/ (stripped.endsWith(";
	/*")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            final var nameSeparator = withoutEnd.lastIndexOf(" ");
            if (nameSeparator >= 0) {
                final var beforeName = withoutEnd.substring(0, nameSeparator).strip();
                final var name = withoutEnd.substring(nameSeparator + " ".length()).strip();

                final var generated = compileFieldWithType(beforeName, name);
                return Optional.of(new Tuple<>(Lists.empty(), generated));
            }
        }

       */ /*return*/ Optional.empty();
	/*}

    private static String compileFieldWithType(String beforeName, String name) {
        final var typeSeparator =*/ /*beforeName.lastIndexOf("*/ ");
	/*if (typeSeparator >= 0) {
            final var beforeType = beforeName.substring(0, typeSeparator);
            final var type = beforeName.substring(typeSeparator + " ".length());
            return generateField(Optional.of(beforeType), compileType(type), name);
        }
        else {
            return generateField(Optional.empty(), compileType(beforeName), name);
        }
    }

    private static String generateField(Optional<String> maybeBeforeType, String type, String name) {
        final var beforeType = maybeBeforeType
                .map(Main::generatePlaceholder)
                .map(inner -> inner + "*/ /*")*/ .orElse("");
	/*return "\n\t" + beforeType + type + " " + name*/ /*+*/ ";/*";*/
	/*}

    private static String compileType(String type) {
       */ /*return*/ generatePlaceholder(type);
	/*}

    private static String compileClassDefinition(String input) {
        return compileClassDefinitionWithKeyword(input, "class ")
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .orElseGet(()*/ /*->*/ generatePlaceholder(input));
	/*}

    private static Optional<String> compileClassDefinitionWithKeyword(String input, String keyword) {
        final var classIndex*/ /*=*/ input.indexOf(keyword);
	/*if (classIndex < 0) {
            return Optional.empty();
        }

        final var beforeKeyword =*/ /*input.substring(0,*/ classIndex).strip();
	/*final var afterKeyword = input.substring(classIndex*/ /*+*/ keyword.length());
	/*return Optional.of(generatePlaceholder(beforeKeyword) + "struct "*/ /*+*/ afterKeyword);
	/*}

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end")*/ /*+*/ "*/";/*
    */
};
/*public*/struct Main {
};
