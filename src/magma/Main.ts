export class Main {
	class Tuple {/**/}
	class State {/*private final List<String> segments;*//*
        private StringBuilder buffer;*//*
        private int depth;*/
	/*public State*/(/*List<String> segments, StringBuilder buffer, int depth*/): /* {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }*/
	/*public State*/(/**/): /* {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }*/
	/*private*/ append(/*char c*/): State/* {
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
	/*public*/ setBuffer(/*StringBuilder buffer*/): void/* {
            this.buffer = buffer;
        }*/
	/*public*/ getDepth(/**/): int/* {
            return depth;
        }*/
	/*public*/ setDepth(/*int depth*/): void/* {
            this.depth = depth;
        }*/
	/*public*/ segments(/**/): List<String>/* {
            return segments;
        }*//*
    */}
	/*public static*/ main(/*String[] args*/): void/* {
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
	/*private static*/ compile(/*String input*/): String/* {
        return compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static*/ compileStatements(/*String input, Function<String, String> mapper*/): String/* {
        return compileAll(input, mapper, Main::foldStatements);
    }*/
	/*private static*/ compileAll(/*String input, Function<String, String> mapper, BiFunction<State, Character, State> folder*/): String/* {
        final var segments = divide(input, folder);
        final var output = new StringBuilder();
        for (var segment : segments) {
            output.append(mapper.apply(segment));
        }

        return output.toString();
    }*/
	/*private static*/ divide(/*String input, BiFunction<State, Character, State> folder*/): List<String>/* {
        State state = new State();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }*/
	/*private static*/ foldStatements(/*State current, char c*/): State/* {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}*/
	/*'*/ appended.isShallow(/**/): &&/*) {
            return appended.advance().exit();
        }*/
	/*if*/(/*c == '{'*/): /* {
            return appended.enter();
        }
        if (c == '}*/
	/*') {
           */ appended.exit(/**/): return/*;
        }*//*
        return appended;*//*
    */}/*

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileStructure(input, "class ", 0).orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Optional<String> compileStructure(String input, String keyword, int depth) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex >= 0) {
            final var modifiersString = input.substring(0, classIndex);
            final var afterClass = input.substring(classIndex + keyword.length());
            final var contentStart = afterClass.indexOf("{");
            if (contentStart >= 0) {
                final var beforeContent = afterClass.substring(0, contentStart).strip();
                final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    final var outputContent = compileStatements(inputContent, Main::compileClassSegment);
                    final var modifiers = modifiersString.contains("public") ? "export " : "";

                    if (beforeContent.endsWith(")")) {
                        final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
                        final var paramStart = withoutParamEnd.indexOf("(");
                        if (paramStart >= 0) {
                            final var name = withoutParamEnd.substring(0, paramStart).strip();
                            return generateClass(depth, modifiers, name, outputContent);
                        }
                    }

                    return generateClass(depth, modifiers, beforeContent, outputContent);
                }
            }
        }
        return Optional.empty();
    }*/class " + beforeContent + " {/*" + outputContent + "}");
    */}/*

    private static String compileClassSegment(String input) {
        return compileStructure(input, "record ", 1)
                .or(() -> compileStructure(input, "class ", 1))
                .or(() -> compileMethod(input))
                .orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Optional<String> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var inputDefinition = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var withBraces = withParams.substring(paramEnd + ")".length());
                final var outputDefinition = compileDefinition(inputDefinition);
                return Optional.of("\n\t" + outputDefinition.left + "(" + generatePlaceholder(params) + "): " + outputDefinition.right + generatePlaceholder(withBraces));
            }
        }

        return Optional.empty();
    }*//*

    private static Tuple compileDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = stripped.substring(0, nameSeparator).strip();
            final var name = stripped.substring(nameSeparator + " ".length());
            final var typeSeparator = beforeName.lastIndexOf(" ");
            if (typeSeparator >= 0) {
                final var beforeType = beforeName.substring(0, typeSeparator);
                final var type = beforeName.substring(typeSeparator + " ".length());
                return new Tuple(generatePlaceholder(beforeType) + " " + name, type);
            }
        }

        return new Tuple(generatePlaceholder(stripped), "");
    }*//*

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("start", "start")
                .replace("end", "end");

        return "start" + replaced + "end";
    }*//*
}*/