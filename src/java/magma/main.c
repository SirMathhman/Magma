#include "./org/jetbrains/annotations/NotNull"
#include "./java/io/IOException"
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
/* public */ struct Result<T, X> {
	/* <R> */ /* R */ /* match(Function<T, */ /* R> */ /* whenOk, */ Function<struct X, struct R> whenErr);
};
/* public */ struct Err<T, X>(X error) implements Result<T, X> {
};
/* public */ struct Ok<T, X>(T value) implements Result<T, X> {
};
/* private */ /* static */ struct State {
	/* private */ /* final */ Deque<char> queue;
	/* private */ /* final */ List<struct String> segments;
	/* private */ struct StringBuilder buffer;
	/* private */ int depth;
};
/* public */ struct Main {
};
/* private */ /* static */ /* final */ List<struct String> imports = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ List<struct String> structs = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ List<struct String> globals = /* new ArrayList<>() */;
/* private */ /* static */ /* final */ List<struct String> methods = /* new ArrayList<>() */;
/* @Override */ /* public */ <R> R match(Function<struct T, struct R> whenOk, Function<struct X, struct R> whenErr) {
	return /* whenErr.apply(this.error) */;
}
/* @Override */ /* public */ <R> R match(Function<struct T, struct R> whenOk, Function<struct X, struct R> whenErr) {
	return /* whenOk.apply(this.value) */;
}
struct private State(Deque<char> queue, List<struct String> segments, struct StringBuilder buffer, int depth) {/* 
            this.queue = queue; *//* 
            this.segments = segments; *//* 
            this.buffer = buffer; *//* 
            this.depth = depth; */
}
struct public State(Deque<char> queue) {/* 
            this(queue, new ArrayList<>(), new StringBuilder(), 0); */
}
/* private */ struct State advance(/*  */) {/* 
            this.segments.add(this.buffer.toString()); *//* 
            this.buffer = new StringBuilder(); */
	return /* this */;
}
/* private */ struct State append(char c) {/* 
            this.buffer.append(c); */
	return /* this */;
}
/* private */ int isLevel(/*  */) {
	return /* this.depth == 0 */;
}
/* private */ char pop(/*  */) {
	return /* this.queue.pop() */;
}
/* private */ int hasElements(/*  */) {
	return /* !this.queue.isEmpty() */;
}
/* private */ struct State exit(/*  */) {/* 
            this.depth = this.depth - 1; */
	return /* this */;
}
/* private */ struct State enter(/*  */) {/* 
            this.depth = this.depth + 1; */
	return /* this */;
}
/* public */ List<struct String> segments(/*  */) {
	return /* this.segments */;
}
/* public */ /* static */ void main(struct String* args) {
	struct Path source = /* Paths.get(".", "src", "java", "magma", "Main.java") */;/* 
        magma.Files.readString(source)
                .match(input -> compileAndWrite(input, source), Optional::of)
                .ifPresent(Throwable::printStackTrace); */
}
/* private */ /* static */ Optional<struct IOException> compileAndWrite(struct String input, struct Path source) {
	struct Path target = /* source.resolveSibling("main.c") */;
	struct String output = /* compile(input) */;
	return /* magma.Files.writeString(target, output) */;
}
/* private */ /* static */ struct String compile(struct String input) {
	List<struct String> segments = /* divide(input, Main::divideStatementChar) */;/* 
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
                .or(() -> generatePlaceholder(input)).orElse(""); */
}
/* private */ /* static */ Optional<struct String> compileStatements(struct String input, Function<struct String, Optional<struct String>> compiler) {
	return /* compileAndMerge(divide(input, Main::divideStatementChar), compiler, Main::mergeStatements) */;
}
/* private */ /* static */ Optional<struct String> compileAndMerge(List<struct String> segments, Function<struct String, Optional<struct String>> compiler, BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger) {
	return /* parseAll(segments, compiler).map(compiled -> mergeAll(merger, compiled)) */;
}
/* private */ /* static */ struct String mergeAll(BiFunction<struct StringBuilder, struct String, struct StringBuilder> merger, List<struct String> compiled) {
	struct StringBuilder output = /* new StringBuilder() */;/* 
        for (String segment : compiled) {
            output = merger.apply(output, segment);
        } */
	return /* output.toString() */;
}
/* private */ /* static */ Optional<List<struct String>> parseAll(List<struct String> segments, Function<struct String, Optional<struct String>> compiler) {
	Optional<List<struct String>> maybeCompiled = /* Optional.of(new ArrayList<String>()) */;/* 
        for (String segment : segments) {
            maybeCompiled = maybeCompiled.flatMap(allCompiled -> compiler.apply(segment).map(compiledSegment -> {
                allCompiled.add(compiledSegment);
                return allCompiled;
            }));
        } */
	return /* maybeCompiled */;
}
/* private */ /* static */ struct StringBuilder mergeStatements(struct StringBuilder output, struct String compiled) {
	return /* output.append(compiled) */;
}
/* private */ /* static */ List<struct String> divide(struct String input, BiFunction<struct State, struct Character, struct State> divider) {
	LinkedList<char> queue = /* IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new)) */;
	struct State state = /* new State(queue) */;/* 
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
        } */
	return /* state.advance().segments() */;
}
/* private */ /* static */ struct State divideStatementChar(struct State state, char c) {
	struct State appended = /* state.append(c) */;
	struct if (c = /* = ';' && appended.isLevel()) return appended.advance() */;
	struct if (c = /* = '}' && isShallow(appended)) return appended.advance().exit() */;
	struct if (c = /* = '{') return appended.enter() */;
	struct if (c = /* = '}') return appended.exit() */;
	return /* appended */;
}
/* private */ /* static */ int isShallow(struct State state) {
	return /* state.depth == 1 */;
}
/* private */ /* static */ Optional<struct String> compileRootSegment(struct String input) {/* 
        if (input.startsWith("package ")) return Optional.of(""); */
	struct String stripped = /* input.strip() */;/* 
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                String joined = String.join("/", content.split(Pattern.quote(".")));
                imports.add("#include \"./" + joined + "\"\n");
                return Optional.of("");
            }
        } */
	Optional<struct String> maybeClass = /* compileToStruct(input, "class ") */;/* 
        if (maybeClass.isPresent()) return maybeClass; */
	return /* generatePlaceholder(input) */;
}
/* private */ /* static */ Optional<struct String> compileToStruct(struct String input, struct String infix) {
	int classIndex = /* input.indexOf(infix) */;/* 
        if (classIndex < 0) return Optional.empty(); */
	struct String substring = /* input.substring(0, classIndex) */;
	struct String afterKeyword = /* input.substring(classIndex + infix.length()) */;/* 
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart >= 0) {
            String name = afterKeyword.substring(0, contentStart).strip();
            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                return compileModifiers(substring).flatMap(newModifiers -> compileStatements(inputContent, Main::compileClassMember).map(outputContent -> {
                    structs.add(newModifiers + " struct " + name + " {\n" +
                            outputContent + "};\n");
                    return "";
                }));
            }
        } */
	return /* Optional.empty() */;
}
/* private */ /* static */ Optional<struct String> compileModifiers(struct String substring) {
	struct String* oldModifiers = /* substring.strip().split(" ") */;
	List<struct String> list = /* Arrays.stream(oldModifiers)
                .map(String::strip)
                .filter(modifier -> !modifier.isEmpty())
                .toList() */;/* 

        if (list.isEmpty()) return Optional.empty(); */
	return /* Optional.of(list.stream()
                .map(Main::generatePlaceholder)
                .flatMap(Optional::stream)
                .collect(Collectors.joining(" "))) */;
}
/* private */ /* static */ Optional<struct String> compileClassMember(struct String input) {
	return /* compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface "))
                .or(() -> compileToStruct(input, "record "))
                .or(() -> compileToStruct(input, "class "))
                .or(() -> compileGlobalInitialization(input))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> compileMethod(input))
                .or(() -> generatePlaceholder(input)) */;
}
/* private */ /* static */ /* @NotNull */ Optional<struct String> compileDefinitionStatement(struct String input) {
	struct String stripped = /* input.strip() */;/* 
        if (stripped.endsWith("; *//* ")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(content).map(result -> "\t" + result + ";\n");
        } */
	return /* Optional.empty() */;
}
/* private */ /* static */ Optional<struct String> compileGlobalInitialization(struct String input) {/* 
        return compileInitialization(input).map(generated -> {
            globals.add(generated + ";\n");
            return "";
        } *//* ); */
}
/* private */ /* static */ Optional<struct String> compileInitialization(struct String input) {/* 
        if (!input.endsWith("; *//* ")) return Optional.empty(); */
	struct String withoutEnd = /* input.substring(0, input.length() - " */;/* ".length()); */
	int valueSeparator = /* withoutEnd.indexOf("=") */;/* 
        if (valueSeparator < 0) return Optional.empty(); */
	struct String definition = /* withoutEnd.substring(0, valueSeparator).strip() */;
	struct String value = /* withoutEnd.substring(valueSeparator + "=".length()).strip() */;/* 
        return compileDefinition(definition).map(outputDefinition -> {
            return outputDefinition + " = " + generatePlaceholder(value).orElse("");
        } *//* ); */
}
/* private */ /* static */ Optional<struct String> compileWhitespace(struct String input) {/* 
        if (input.isBlank()) return Optional.of(""); */
	return /* Optional.empty() */;
}
/* private */ /* static */ Optional<struct String> compileMethod(struct String input) {
	int paramStart = /* input.indexOf("(") */;/* 
        if (paramStart < 0) return Optional.empty(); */
	struct String inputDefinition = /* input.substring(0, paramStart).strip() */;
	struct String withParams = /* input.substring(paramStart + "(".length()) */;/* 

        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) return Optional.empty();

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, definition -> compileDefinition(definition).or(() -> generatePlaceholder(definition))).flatMap(outputParams -> {
                String header = "\t".repeat(0) + outputDefinition + "(" + outputParams + ")";
                String body = withParams.substring(paramEnd + ")".length()).strip();
                if (body.startsWith("{") && body.endsWith("}")) {
                    String inputContent = body.substring("{".length(), body.length() - "}".length());
                    return compileStatements(inputContent, Main::compileStatementOrBlock).flatMap(outputContent -> {
                        methods.add(header + " {" + outputContent + "\n}\n");
                        return Optional.of("");
                    });
                }

                return Optional.of(header + ";");
            });
        } *//* ); */
}
/* private */ /* static */ Optional<struct String> compileValues(struct String input, Function<struct String, Optional<struct String>> compiler) {
	List<struct String> divided = /* divide(input, Main::divideValueChar) */;
	return /* compileValues(divided, compiler) */;
}
/* private */ /* static */ struct State divideValueChar(struct State state, char c) {
	struct if (c = /* = ',' && state.isLevel()) return state.advance() */;
	struct State appended = /* state.append(c) */;
	struct if (c = /* = '<') return appended.enter() */;
	struct if (c = /* = '>') return appended.exit() */;
	return /* appended */;
}
/* private */ /* static */ Optional<struct String> compileValues(List<struct String> params, Function<struct String, Optional<struct String>> compoiler) {
	return /* compileAndMerge(params, compoiler, Main::mergeValues) */;
}
/* private */ /* static */ Optional<struct String> compileStatementOrBlock(struct String input) {
	return /* compileWhitespace(input)
                .or(() -> compileStatement(input))
                .or(() -> compileInitialization(input).map(value -> "\n\t" + value + " */;/* "))
                .or(() -> generatePlaceholder(input)); */
}
/* private */ /* static */ Optional<struct String> compileStatement(struct String input) {
	struct String stripped = /* input.strip() */;/* 
        if (stripped.endsWith("; *//* ")) {
            String value = stripped.substring(0, stripped.length() - ";".length());
            if (value.startsWith("return ")) {
                return compileValue(value.substring("return ".length())).map(result -> "\n\treturn " + result + ";");
            }
        } */
	return /* Optional.empty() */;
}
/* private */ /* static */ Optional<struct String> compileValue(struct String input) {
	return /* generatePlaceholder(input) */;
}
/* private */ /* static */ struct StringBuilder mergeValues(struct StringBuilder cache, struct String element) {/* 
        if (cache.isEmpty()) return cache.append(element); */
	return /* cache.append(", ").append(element) */;
}
/* private */ /* static */ Optional<struct String> compileDefinition(struct String definition) {
	int nameSeparator = /* definition.lastIndexOf(" ") */;/* 
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
                return compileType(inputType, typeParams).flatMap(outputType -> Optional.of(generateDefinition(compiledModifiers, typeParams, outputType, name)));
            } else {
                return compileType(beforeName, Collections.emptyList()).flatMap(outputType -> Optional.of(generateDefinition(Optional.empty(), Collections.emptyList(), outputType, name)));
            }
        } */
	return /* Optional.empty() */;
}
/* private */ /* static */ List<struct String> splitValues(struct String substring) {
	struct String* paramsArrays = /* substring.strip().split(Pattern.quote(",")) */;
	return /* Arrays.stream(paramsArrays)
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .toList() */;
}
/* private */ /* static */ struct String generateDefinition(Optional<struct String> maybeModifiers, List<struct String> maybeTypeParams, struct String type, struct String name) {
	struct String modifiersString = /* maybeModifiers.map(modifiers -> modifiers + " ").orElse("") */;/* 

        String typeParamsString; *//* 
        if (maybeTypeParams.isEmpty()) {
            typeParamsString = "";
        } *//*  else {
            typeParamsString = "<" + String.join(", ", maybeTypeParams) + "> ";
        } */
	return /* modifiersString + typeParamsString + type + " " + name */;
}
/* private */ /* static */ Optional<struct String> compileType(struct String input, List<struct String> typeParams) {/* 
        if (input.equals("void")) return Optional.of("void"); *//* 

        if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
            return Optional.of("int");
        } *//* 

        if (input.equals("char") || input.equals("Character")) {
            return Optional.of("char");
        } *//* 

        if (input.endsWith("[]")) {
            return compileType(input.substring(0, input.length() - "[]".length()), typeParams)
                    .map(value -> value + "*");
        } */
	struct String stripped = /* input.strip() */;/* 
        if (isSymbol(stripped)) {
            if (typeParams.contains(stripped)) {
                return Optional.of(stripped);
            } else {
                return Optional.of("struct " + stripped);
            }
        } *//* 

        if (stripped.endsWith(">")) {
            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart >= 0) {
                String base = slice.substring(0, argsStart).strip();
                String params = slice.substring(argsStart + "<".length()).strip();
                return compileValues(params, type -> compileType(type, typeParams)).map(compiled -> {
                    return base + "<" + compiled + ">";
                });
            }
        } */
	return /* generatePlaceholder(input) */;
}
/* private */ /* static */ int isSymbol(struct String input) {
	/* for */ /* (int */ i = /* 0 */;/*  i < input.length(); *//*  i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) continue;
            return false;
        } */
	return /* true */;
}
/* private */ /* static */ Optional<struct String> generatePlaceholder(struct String input) {
	return /* Optional.of("/* " + input + " */") */;
}
/* 
 */