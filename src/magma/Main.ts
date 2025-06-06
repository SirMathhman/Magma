class Tuple<L, R> {
}
class State {
	/*private final*/ segments: List<String>;
	/*private*/ buffer: StringBuilder;
	/*private*/ depth: int;
	State(segments: List<String>, buffer: StringBuilder, depth: int): public/* {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }*/
	State(/**/): public/* {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }*/
	/*private*/ append(c: char): State/* {
            getBuffer().append(c);
            return this;
        }*/
	/*private*/ enter(/**/): State/* {
            setDepth(getDepth() + 1);
            return this;
        }*/
	/*private*/ exit(/**/): State/* {
            setDepth(getDepth() - 1);
            return this;
        }*/
	/*private*/ isShallow(/**/): boolean/* {
            return getDepth() == 1;
        }*/
	/*private*/ advance(/**/): State/* {
            segments().add(getBuffer().toString());
            setBuffer(new StringBuilder());
            return this;
        }*/
	/*private*/ isLevel(/**/): boolean/* {
            return getDepth() == 0;
        }*/
	/*public*/ getBuffer(/**/): StringBuilder/* {
            return buffer;
        }*/
	/*public*/ setBuffer(buffer: StringBuilder): void/* {
            this.buffer = buffer;
        }*/
	/*public*/ getDepth(/**/): int/* {
            return depth;
        }*/
	/*public*/ setDepth(depth: int): void/* {
            this.depth = depth;
        }*/
	/*public*/ segments(/**/): List<String>/* {
            return segments;
        }*/
}
export class Main {
	/*public static*/ main(args: String[]): void/* {
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
	/*private static*/ compile(input: String): String/* {
        return compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static*/ compileStatements(input: String, /* Function<String*/, mapper: String>): String/* {
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }*/
	/*private static*/ compileAll(input: String, /* Function<String*/, mapper: String>, /* BiFunction<State*/, /* Character*/, folder: State>, /* BiFunction<StringBuilder*/, /* String*/, merger: StringBuilder>): String/* {
        final var segments = divide(input, folder);
        var output = new StringBuilder();
        for (var segment : segments) {
            final var compiled = mapper.apply(segment);
            output = merger.apply(output, compiled);
        }

        return output.toString();
    }*/
	/*private static*/ mergeStatements(output: StringBuilder, compiled: String): StringBuilder/* {
        return output.append(compiled);
    }*/
	/*private static*/ divide(input: String, /* BiFunction<State*/, /* Character*/, folder: State>): List<String>/* {
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
	/*'*/ appended.isShallow(/**/): &&/*) {
            return appended.advance().exit();
        }*/
	/*
        if */(/*c*/ '{': ==): /**//* {
            return appended.enter();
        }
        if (c == '}*/
	/*') {
           */ appended.exit(/**/): return/*;
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
    }*/class ").map(tuple -> {
	/*final var joined =*/ tuple.right): String.join("",;
	/*return tuple.left*/ joined: +;/*
        });
    */
}
/*

    private static Optional<Tuple<String, List<String>>> compileStructure(String input, String keyword) {
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

        final var structures = new ArrayList<String>();
        final var segments = divide(inputContent, Main::foldStatements);
        final var output = new StringBuilder();
        for (var segment : segments) {
            final var tuple = compileClassSegment(segment);
            output.append(tuple.left);
            structures.addAll(tuple.right);
        }

        final var outputContent = output.toString();
        final var modifiers = modifiersString.contains("public") ? "export " : "";

        final var generated = assembleClass(beforeContent, modifiers, outputContent);
        structures.add(generated);
        return Optional.of(new Tuple<>("", structures));
    }*//*

    private static String assembleClass(String beforeContent, String modifiers, String outputContent) {
        if (beforeContent.endsWith(")")) {
            final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
            final var paramStart = withoutParamEnd.indexOf("(");
            if (paramStart >= 0) {
                final var name = withoutParamEnd.substring(0, paramStart).strip();
                return generateClass(modifiers, name, outputContent);
            }
        }

        return generateClass(modifiers, beforeContent, outputContent);
    }*/class " + beforeContent + " {
	/*" + outputContent*/ "\n}\n": +;
}
/*

    private static Tuple<String, List<String>> compileClassSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileStructure(input, "record "))
                .or(() -> compileStructure(input, "class "))
                .or(() -> compileField(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), Collections.emptyList()));
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
        return compileSimpleDefinition(content).map(definition -> new Tuple<>("\n\t" + definition + ";", Collections.emptyList()));
    }*//*

    private static Optional<String> compileSimpleDefinition(String content) {
        return compileDefinition(content).map(definition -> definition.left + ": " + definition.right);
    }*//*

    private static Optional<Tuple<String, List<String>>> compileWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of(new Tuple<>("", Collections.emptyList()));
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
                final var outputDefinition = compileDefinitionOrPlaceholder(inputDefinition);
                final var outputParams = compileAll(inputParams, Main::compileSimpleDefinitionOrPlaceholder, Main::foldValues, Main::mergeValues);

                final var generated = "\n\t" + outputDefinition.left + "(" + outputParams + "): " + outputDefinition.right + generatePlaceholder(withBraces);
                return Optional.of(new Tuple<>(generated, Collections.emptyList()));
            }
        }

        return Optional.empty();
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

    private static String compileSimpleDefinitionOrPlaceholder(String input) {
        return compileSimpleDefinition(input).orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Tuple<String, String> compileDefinitionOrPlaceholder(String input) {
        return compileDefinition(input).orElseGet(() -> new Tuple<>(generatePlaceholder(input), generatePlaceholder("")));
    }*//*

    private static Optional<Tuple<String, String>> compileDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = stripped.substring(0, nameSeparator).strip();
            final var name = stripped.substring(nameSeparator + " ".length());
            final var typeSeparator = beforeName.lastIndexOf(" ");
            if (typeSeparator < 0) {
                return Optional.of(new Tuple<>(name, beforeName));
            }

            final var beforeType = beforeName.substring(0, typeSeparator);
            final var type = beforeName.substring(typeSeparator + " ".length());
            return Optional.of(new Tuple<>(generatePlaceholder(beforeType) + " " + name, type));
        }
        return Optional.empty();
    }*//*

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("start", "start")
                .replace("end", "end");

        return "start" + replaced + "end";
    }*//*
}*/