#include "./java/io/IOException"
#include "./java/nio/file/Files"
#include "./java/nio/file/Path"
#include "./java/nio/file/Paths"
#include "./java/util/ArrayList"
#include "./java/util/Arrays"
#include "./java/util/Collections"
#include "./java/util/Deque"
#include "./java/util/LinkedList"
#include "./java/util/List"
#include "./java/util/Optional"
#include "./java/util/function/BiFunction"
#include "./java/util/function/Function"
#include "./java/util/regex/Pattern"
#include "./java/util/stream/Collectors"
#include "./java/util/stream/IntStream"
/* private */ struct Result<T, X> {
<R> R match(/* Function<T, R> */ whenOk, /* Function<X, R> */ whenErr);
};
/* private */ struct Err<T, X>(X error) implements Result<T, X> {

};
/* private */ struct Ok<T, X>(T value) implements Result<T, X> {

};
/* private */ /* static */ struct State {
/* private final Deque<Character> queue; *//* 
        private final List<String> segments; *//* 
        private StringBuilder buffer; *//* 
        private int depth; */
};
/* public */ struct Main {

};
/* private */ /* static */ /* final */ /* List<String> */ imports = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ /* List<String> */ structs = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ /* List<String> */ globals = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ /* List<String> */ methods = /* new ArrayList<>() */;
/* @Override */ /* public */ <R> R match(/* Function<T, R> */ whenOk, /* Function<X, R> */ whenErr) {/* 
            return whenErr.apply(error); *//* 
         */
}/* @Override */ /* public */ <R> R match(/* Function<T, R> */ whenOk, /* Function<X, R> */ whenErr) {/* 
            return whenOk.apply(value); *//* 
         */
}struct private State(/* Deque<Character> */ queue, /* List<String> */ segments, struct StringBuilder buffer, struct int depth) {/* 
            this.queue = queue; *//* 
            this.segments = segments; *//* 
            this.buffer = buffer; *//* 
            this.depth = depth; *//* 
         */
}struct public State(/* Deque<Character> */ queue) {/* 
            this(queue, new ArrayList<>(), new StringBuilder(), 0); *//* 
         */
}/* private */ struct State advance(/*  */) {/* 
            segments.add(buffer.toString()); *//* 
            buffer = new StringBuilder(); *//* 
            return this; *//* 
         */
}/* private */ struct State append(struct char c) {/* 
            buffer.append(c); *//* 
            return this; *//* 
         */
}/* private */ struct boolean isLevel(/*  */) {/* 
            return depth == 0; *//* 
         */
}/* private */ struct char pop(/*  */) {/* 
            return queue.pop(); *//* 
         */
}/* private */ struct boolean hasElements(/*  */) {/* 
            return !queue.isEmpty(); *//* 
         */
}/* private */ struct State exit(/*  */) {/* 
            this.depth = depth - 1; *//* 
            return this; *//* 
         */
}/* private */ struct State enter(/*  */) {/* 
            this.depth = depth + 1; *//* 
            return this; *//* 
         */
}/* public */ /* List<String> */ segments(/*  */) {/* 
            return segments; *//* 
         */
}/* public */ /* static */ void main(struct String* args) {/* 
        Path source = Paths.get(".", "src", "java", "magma", "Main.java"); *//* 
        readString(source)
                .match(input -> compileAndWrite(input, source), Optional::of)
                .ifPresent(Throwable::printStackTrace); *//* 
     */
}/* private */ /* static */ /* Optional<IOException> */ compileAndWrite(struct String input, struct Path source) {/* 
        Path target = source.resolveSibling("main.c"); *//* 
        String output = compile(input); *//* 
        return writeString(target, output); *//* 
     */
}/* private */ /* static */ /* Optional<IOException> */ writeString(struct Path target, struct String output) {/* 
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } *//*  catch (IOException e) {
            return Optional.of(e);
        } *//* 
     */
}/* private */ /* static */ /* Result<String, IOException> */ readString(struct Path source) {/* 
        try {
            return new Ok<>(Files.readString(source));
        } *//*  catch (IOException e) {
            return new Err<>(e);
        } *//* 
     */
}/* private */ /* static */ struct String compile(struct String input) {/* 
        List<String> segments = divide(input, Main::divideStatementChar); *//* 
        return parseAll(segments, Main::compileRootSegment)
                .map(list -> {
                    List<String> copy = new ArrayList<String>();
                    copy.addAll(imports);
                    copy.addAll(structs);
                    copy.addAll(globals);
                    copy.addAll(methods);
                    copy.addAll(list);
                    return copy;
                } *//* )
                .map(compiled -> mergeAll(Main::mergeStatements, compiled))
                .or(() -> generatePlaceholder(input)).orElse(""); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileStatements(struct String input, /* Function<String, Optional<String>> */ compiler) {/* 
        return compileAndMerge(divide(input, Main::divideStatementChar), compiler, Main::mergeStatements); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileAndMerge(/* List<String> */ segments, /* Function<String, Optional<String>> */ compiler, /* BiFunction<StringBuilder, String, StringBuilder> */ merger) {/* 
        return parseAll(segments, compiler).map(compiled -> mergeAll(merger, compiled)); *//* 
     */
}/* private */ /* static */ struct String mergeAll(/* BiFunction<StringBuilder, String, StringBuilder> */ merger, /* List<String> */ compiled) {/* 
        StringBuilder output = new StringBuilder(); *//* 
        for (String segment : compiled) {
            output = merger.apply(output, segment);
        } *//* 

        return output.toString(); *//* 
     */
}/* private */ /* static */ /* Optional<List<String>> */ parseAll(/* List<String> */ segments, /* Function<String, Optional<String>> */ compiler) {/* 
        Optional<List<String>> maybeCompiled = Optional.of(new ArrayList<String>()); *//* 
        for (String segment : segments) {
            maybeCompiled = maybeCompiled.flatMap(allCompiled -> {
                return compiler.apply(segment).map(compiledSegment -> {
                    allCompiled.add(compiledSegment);
                    return allCompiled;
                });
            });
        } *//* 
        return maybeCompiled; *//* 
     */
}/* private */ /* static */ struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {/* 
        return output.append(compiled); *//* 
     */
}/* private */ /* static */ /* List<String> */ divide(struct String input, /* BiFunction<State, Character, State> */ divider) {/* 
        LinkedList<Character> queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new)); *//* 

        State state = new State(queue); *//* 
        while (state.hasElements()) {
            char c = state.pop();

            if (c == '\'') {
                state.append(c);
                char maybeSlash = state.pop();
                state.append(maybeSlash);

                if (maybeSlash == '\\') state.append(state.pop());
                state.append(state.pop());
                continue;
            }

            state = divider.apply(state, c);
        } *//* 

        return state.advance().segments(); *//* 
     */
}/* private */ /* static */ struct State divideStatementChar(struct State state, struct char c) {/* 
        State appended = state.append(c); *//* 
        if (c == ';' && appended.isLevel()) return appended.advance(); *//* 
        if (c == '}' && isShallow(appended)) return appended.advance().exit(); *//* 
        if (c == '{') return appended.enter(); *//* 
        if (c == '}') return appended.exit(); *//* 
        return appended; *//* 
     */
}/* private */ /* static */ struct boolean isShallow(struct State state) {/* 
        return state.depth == 1; *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileRootSegment(struct String input) {/* 
        if (input.startsWith("package ")) return Optional.of(""); *//* 

        String stripped = input.strip(); *//* 
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                String joined = String.join("/", content.split(Pattern.quote(".")));
                imports.add("#include \"./" + joined + "\"\n");
                return Optional.of("");
            }
        } *//* 

        Optional<String> maybeClass = compileToStruct(input, "class "); *//* 
        if (maybeClass.isPresent()) return maybeClass; *//* 

        return generatePlaceholder(input); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileToStruct(struct String input, struct String infix) {/* 
        int classIndex = input.indexOf(infix); *//* 
        if (classIndex < 0) return Optional.empty(); *//* 

        String substring = input.substring(0, classIndex); *//* 

        String afterKeyword = input.substring(classIndex + infix.length()); *//* 
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart >= 0) {
            String name = afterKeyword.substring(0, contentStart).strip();
            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                return compileModifiers(substring).flatMap(newModifiers -> {
                    return compileStatements(inputContent, input1 -> compileClassMember(input1)).map(outputContent -> {
                        structs.add(newModifiers + " struct " + name + " {\n" +
                                outputContent +
                                "\n" + "};\n");
                        return "";
                    });
                });
            }
        } *//* 
        return Optional.empty(); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileModifiers(struct String substring) {/* 
        String[] oldModifiers = substring.strip().split(" "); *//* 
        List<String> list = Arrays.stream(oldModifiers)
                .map(String::strip)
                .filter(modifier -> !modifier.isEmpty())
                .toList(); *//* 

        if (list.isEmpty()) return Optional.empty(); *//* 
        return Optional.of(list.stream()
                .map(Main::generatePlaceholder)
                .flatMap(Optional::stream)
                .collect(Collectors.joining(" "))); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileClassMember(struct String input) {/* 
        return compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface "))
                .or(() -> compileToStruct(input, "record "))
                .or(() -> compileToStruct(input, "class "))
                .or(() -> compileInitialization(input))
                .or(() -> compileMethod(input, 0))
                .or(() -> generatePlaceholder(input)); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileInitialization(struct String input) {/* 
        if (!input.endsWith("; *//* ")) return Optional.empty(); *//* 

        String withoutEnd = input.substring(0, input.length() - "; *//* ".length()); *//* 
        int valueSeparator = withoutEnd.indexOf("="); *//* 
        if (valueSeparator < 0) return Optional.empty(); *//* 

        String definition = withoutEnd.substring(0, valueSeparator).strip(); *//* 
        String value = withoutEnd.substring(valueSeparator + "=".length()).strip(); *//* 
        return compileDefinition(definition).map(outputDefinition -> {
            String generated = outputDefinition + " = " + generatePlaceholder(value).orElse("") + ";\n";
            globals.add(generated);
            return "";
        } *//* ); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileWhitespace(struct String input) {/* 
        if (input.isBlank()) return Optional.of(""); *//* 
        return Optional.empty(); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileMethod(struct String input, struct int depth) {/* 
        int paramStart = input.indexOf("("); *//* 
        if (paramStart < 0) return Optional.empty(); *//* 

        String inputDefinition = input.substring(0, paramStart).strip(); *//* 
        String withParams = input.substring(paramStart + "(".length()); *//* 

        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) return Optional.empty();

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, definition -> compileDefinition(definition).or(() -> generatePlaceholder(definition))).flatMap(outputParams -> {
                String header = "\t".repeat(depth) + outputDefinition + "(" + outputParams + ")";
                String body = withParams.substring(paramEnd + ")".length()).strip();
                if (body.startsWith("{") && body.endsWith("}")) {
                    String inputContent = body.substring("{".length(), body.length() - "}".length());
                    return compileStatements(inputContent, Main::compileStatement).flatMap(outputContent -> {
                        methods.add(header + " {" + outputContent + "\n}");
                        return Optional.of("");
                    });
                }

                return Optional.of(header + ";");
            });
        } *//* ); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileValues(struct String input, /* Function<String, Optional<String>> */ compiler) {/* 
        List<String> divided = divide(input, Main::divideValueChar); *//* 
        return compileValues(divided, compiler); *//* 
     */
}/* private */ /* static */ struct State divideValueChar(struct State state, struct char c) {/* 
        if (c == ',' && state.isLevel()) return state.advance(); *//* 

        State appended = state.append(c); *//* 
        if (c == '<') return appended.enter(); *//* 
        if (c == '>') return appended.exit(); *//* 
        return appended; *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileValues(/* List<String> */ params, /* Function<String, Optional<String>> */ compoiler) {/* 
        return compileAndMerge(params, compoiler, Main::mergeValues); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileStatement(struct String input) {/* 
        return generatePlaceholder(input); *//* 
     */
}/* private */ /* static */ struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {/* 
        if (cache.isEmpty()) return cache.append(element); *//* 
        return cache.append(", ").append(element); *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileDefinition(struct String definition) {/* 
        int nameSeparator = definition.lastIndexOf(" "); *//* 
        if (nameSeparator >= 0) {
            String beforeName = definition.substring(0, nameSeparator).strip();
            String name = definition.substring(nameSeparator + " ".length()).strip();

            int typeSeparator = -1;
            int depth = 0;
            for (int i = beforeName.length() - 1; i >= 0; i--) {
                char c = beforeName.charAt(i);
                if (c == ' ' && depth == 0) {
                    typeSeparator = i;
                    break;
                } else {
                    if (c == '>') depth++;
                    if (c == '<') depth--;
                }
            }

            if (typeSeparator >= 0) {
                String beforeType = beforeName.substring(0, typeSeparator).strip();

                String modifiers;
                List<String> typeParams;
                if (beforeType.endsWith(">")) {
                    String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                    int typeParamStart = withoutEnd.indexOf("<");
                    if (typeParamStart >= 0) {
                        modifiers = withoutEnd.substring(0, typeParamStart);
                        String substring = withoutEnd.substring(typeParamStart + 1);
                        typeParams = splitValues(substring);
                    } else {
                        modifiers = beforeType;
                        typeParams = Collections.emptyList();
                    }
                } else {
                    modifiers = beforeType;
                    typeParams = Collections.emptyList();
                }

                String inputType = beforeName.substring(typeSeparator + " ".length());
                Optional<String> compiledModifiers = compileModifiers(modifiers.strip());
                return compileType(inputType, typeParams).flatMap(outputType -> {
                    return Optional.of(generateDefinition(compiledModifiers, typeParams, outputType, name));
                });
            } else {
                return compileType(beforeName, Collections.emptyList()).flatMap(outputType -> {
                    return Optional.of(generateDefinition(Optional.empty(), Collections.emptyList(), outputType, name));
                });
            }
        } *//* 
        return Optional.empty(); *//* 
     */
}/* private */ /* static */ /* List<String> */ splitValues(struct String substring) {/* 
        String[] paramsArrays = substring.strip().split(Pattern.quote(",")); *//* 
        return Arrays.stream(paramsArrays)
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .toList(); *//* 
     */
}/* private */ /* static */ struct String generateDefinition(/* Optional<String> */ maybeModifiers, /* List<String> */ maybeTypeParams, struct String type, struct String name) {/* 
        String modifiersString = maybeModifiers.map(modifiers -> modifiers + " ").orElse(""); *//* 

        String typeParamsString; *//* 
        if (maybeTypeParams.isEmpty()) {
            typeParamsString = "";
        } *//*  else {
            typeParamsString = "<" + String.join(", ", maybeTypeParams) + "> ";
        } *//* 

        return modifiersString + typeParamsString + type + " " + name; *//* 
     */
}/* private */ /* static */ /* Optional<String> */ compileType(struct String input, /* List<String> */ typeParams) {/* 
        if (input.equals("void")) return Optional.of("void"); *//* 

        if (input.endsWith("[]")) {
            return compileType(input.substring(0, input.length() - "[]".length()), typeParams)
                    .map(value -> value + "*");
        } *//* 

        if (isSymbol(input)) {
            if (typeParams.contains(input)) {
                return Optional.of(input);
            } else {
                return Optional.of("struct " + input);
            }
        } *//* 

        return generatePlaceholder(input); *//* 
     */
}/* private */ /* static */ struct boolean isSymbol(struct String input) {/* 
        for (int i = 0; *//*  i < input.length(); *//*  i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) continue;
            return false;
        } *//* 
        return true; *//* 
     */
}/* private */ /* static */ /* Optional<String> */ generatePlaceholder(struct String input) {/* 
        return Optional.of("/* " + input + " */"); *//* 
     */
}/* 
 */