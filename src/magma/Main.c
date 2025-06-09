/*public static <T>*/ /*List<T>*/ empty(/**/) {
}
/*public static <T>*/ /*List<T>*/ of(/*T... elements*/) {
}
/*private static*/struct Lists {
	/*public static <T>*/ /*List<T>*/ empty(/**/);
	/*public static <T>*/ /*List<T>*/ of(/*T... elements*/);
};
/*private*/ State(/*List<String> segments, StringBuilder buffer, int depth*/) {
}
/*public*/ State(/**/) {
}
/*private*/ /*boolean*/ isLevel(/**/) {
}
/*private*/ /*State*/ append(/*char c*/) {
}
/*private*/ /*State*/ advance(/**/) {
}
/*private*/ /*State*/ enter(/**/) {
}
/*private*/ /*State*/ exit(/**/) {
}
/*public*/ /*boolean*/ isShallow(/**/) {
}
/*private static*/struct State {
	/*private*/ /*List<String>*/ segments;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ /*int*/ depth;
	/*private*/ State(/*List<String> segments, StringBuilder buffer, int depth*/);
	/*public*/ State(/**/);
	/*private*/ /*boolean*/ isLevel(/**/);
	/*private*/ /*State*/ append(/*char c*/);
	/*private*/ /*State*/ advance(/**/);
	/*private*/ /*State*/ enter(/**/);
	/*private*/ /*State*/ exit(/**/);
	/*public*/ /*boolean*/ isShallow(/**/);
};
/*private*/ /*record*/ ClassDefinition(/*String beforeKeyword, String name, List<String> typeParameters*/) {
}
/*public static*/ /*void*/ main(/*String[] args*/) {
}
/*private static*/ /*String*/ compile(/*String input*/) {
}
/*private static*/ /*String*/ compileStatements(/*String input, Function<String, String> mapper*/) {
}
/*private static*/ /*List<String>*/ divide(/*String input*/) {
}
/*private static*/ /*State*/ fold(/*State state, char c*/) {
}
/*if*/ (/*c == '{'*/) {
}
/*public*/struct Main {/*

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
    }*//*

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

        @Override
        public boolean isEmpty() {
            return this.elements.isEmpty();
        }
    }*//*

    private record Tuple<A, B>(A left, B right) {
    }*//*

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
    }*/
	/*private*/ /*record*/ ClassDefinition(/*String beforeKeyword, String name, List<String> typeParameters*/);
	/*public static*/ /*void*/ main(/*String[] args*/);
	/*private static*/ /*String*/ compile(/*String input*/);
	/*private static*/ /*String*/ compileStatements(/*String input, Function<String, String> mapper*/);
	/*private static*/ /*List<String>*/ divide(/*String input*/);
	/*private static*/ /*State*/ fold(/*State state, char c*/);/*' && appended.isShallow()) {
            return appended.advance().exit();
        }*/
	/*if*/ (/*c == '{'*/);/*') {
            return appended.exit();
        }*/
	/*return*/ appended;
};
/*

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
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
                .orElseGet(() -> generatePlaceholder(input));
    }*//*


    private static Optional<Tuple<List<String>, String>> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var maybeHeader = compileClassDefinition(beforeContent);
                if (maybeHeader.isPresent()) {
                    final var definition = maybeHeader.get();
                    final var others = compileClassWithDefinition(definition, withEnd);
                    return Optional.of(new Tuple<>(others, ""));
                }
            }
        }

        return Optional.empty();
    }

    private static List<String> compileClassWithDefinition(ClassDefinition definition, String withEnd) {
        if (definition.typeParameters.isEmpty()) {
            final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());

            final var segments = divide(inputContent);

            final var tuple = segments.iter()
                    .map(Main::compileClassSegment)
                    .collect(new TupleCollector<>(new ListBulkCollector<>(), new Joiner()));

            final var others = tuple.left;
            final var output = tuple.right.orElse("");

            final var generatedHeader = definition.generate();
            final var generated = generatedHeader + " {" + output + "\n};\n";
            return others.addLast(generated);
        }

        return Lists.empty();
    }*//*

    private static Tuple<List<String>, String> compileClassSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileField(input))
                .or(() -> compileClass(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(Lists.empty(), generatePlaceholder(input)));
    }*//*

    private static Optional<Tuple<List<String>, String>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var beforeParams = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var content = withParams.substring(paramEnd + ")".length()).strip();
                final var maybeDefinition = compileDefinition(beforeParams);
                if (maybeDefinition.isPresent()) {
                    final var header = maybeDefinition.get() + "(" + generatePlaceholder(params) + ")";
                    if (content.equals(";")) {
                        final var generated = header + ";";
                        return Optional.of(new Tuple<>(Lists.of(generated + "\n"), "\n\t" + generated));
                    } else {
                        return Optional.of(new Tuple<>(Lists.of(header + " {\n}" + "\n"), "\n\t" + header + ";"));
                    }
                }
            }
        }

        return Optional.empty();
    }*//*

    private static Optional<Tuple<List<String>, String>> compileWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of(new Tuple<>(Lists.empty(), ""));
        }
        else {
            return Optional.empty();
        }
    }*//*

    private static Optional<Tuple<List<String>, String>> compileField(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(withoutEnd).map(generated -> {
                return new Tuple<>(Lists.empty(), "\n\t" + generated + ";");
            });
        }

        return Optional.empty();
    }*//*

    private static Optional<String> compileDefinition(String withoutEnd) {
        final var nameSeparator = withoutEnd.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = withoutEnd.substring(0, nameSeparator).strip();
            final var name = withoutEnd.substring(nameSeparator + " ".length()).strip();

            if (isSymbol(name)) {
                final var generated = compileDefinitionWithType(beforeName, name);
                return Optional.of(generated);
            }
        }
        return Optional.empty();
    }*//*

    private static boolean isSymbol(String input) {
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c)) {
                continue;
            }
            return false;
        }
        return true;
    }*//*

    private static String compileDefinitionWithType(String beforeName, String name) {
        final var typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator >= 0) {
            final var beforeType = beforeName.substring(0, typeSeparator);
            final var type = beforeName.substring(typeSeparator + " ".length());
            String type1 = compileType(type);
            return generateDefinition(Optional.of(beforeType), type1, name);
        }
        else {
            String type = compileType(beforeName);
            return generateDefinition(Optional.empty(), type, name);
        }
    }*//*

    private static String generateDefinition(Optional<String> maybeBeforeType, String type, String name) {
        final var beforeType = maybeBeforeType
                .map(Main::generatePlaceholder)
                .map(inner -> inner + " ")
                .orElse("");

        return beforeType + type + " " + name;
    }*//*

    private static String compileType(String type) {
        return generatePlaceholder(type);
    }*//*

    private static Optional<ClassDefinition> compileClassDefinition(String input) {
        return compileClassDefinitionWithKeyword(input, "class ")
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "));
    }*//*

    private static Optional<ClassDefinition> compileClassDefinitionWithKeyword(String input, String keyword) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return Optional.empty();
        }

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + keyword.length());
        return Optional.of(compileClassDefinitionWithTypeParameters(beforeKeyword, afterKeyword));
    }*//*

    private static ClassDefinition compileClassDefinitionWithTypeParameters(String beforeKeyword, String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var typeParamsStart = withoutEnd.indexOf("<");
            if (typeParamsStart >= 0) {
                final var base = withoutEnd.substring(0, typeParamsStart);
                final var typeParameters = withoutEnd.substring(typeParamsStart + "<".length());
                return new ClassDefinition(beforeKeyword, base, divide(typeParameters));
            }
        }
        return new ClassDefinition(beforeKeyword, stripped, Lists.empty());
    }*//*

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";
    }*//*
}
*/