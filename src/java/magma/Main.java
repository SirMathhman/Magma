package magma;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public interface Result<T, X> {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
    }

    public record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(this.error);
        }
    }

    public record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(this.value);
        }
    }

    private static class State {
        private final Deque<Character> queue;
        private final List<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(Deque<Character> queue, List<String> segments, StringBuilder buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State(Deque<Character> queue) {
            this(queue, new ArrayList<>(), new StringBuilder(), 0);
        }

        private State advance() {
            this.segments.add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private State append(char c) {
            this.buffer.append(c);
            return this;
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private char pop() {
            return this.queue.pop();
        }

        private boolean hasElements() {
            return !this.queue.isEmpty();
        }

        private State exit() {
            this.depth = this.depth - 1;
            return this;
        }

        private State enter() {
            this.depth = this.depth + 1;
            return this;
        }

        public List<String> segments() {
            return this.segments;
        }
    }

    private static final List<String> imports = new ArrayList<>();
    private static final List<String> structs = new ArrayList<>();
    private static final List<String> globals = new ArrayList<>();
    private static final List<String> methods = new ArrayList<>();

    public static void main(String[] args) {
        Path source = Paths.get(".", "src", "java", "magma", "Main.java");
        magma.Files.readString(source)
                .match(input -> compileAndWrite(input, source), Optional::of)
                .ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> compileAndWrite(String input, Path source) {
        Path target = source.resolveSibling("main.c");
        String output = compile(input);
        return magma.Files.writeString(target, output);
    }

    private static String compile(String input) {
        List<String> segments = divide(input, Main::divideStatementChar);
        return parseAll(segments, Main::compileRootSegment)
                .map(list -> {
                    List<String> copy = new ArrayList<String>();
                    copy.addAll(imports);
                    copy.addAll(structs);
                    copy.addAll(globals);
                    copy.addAll(methods);
                    copy.addAll(list);
                    return copy;
                })
                .map(compiled -> mergeAll(Main::mergeStatements, compiled))
                .or(() -> generatePlaceholder(input)).orElse("");
    }

    private static Optional<String> compileStatements(String input, Function<String, Optional<String>> compiler) {
        return compileAndMerge(divide(input, Main::divideStatementChar), compiler, Main::mergeStatements);
    }

    private static Optional<String> compileAndMerge(List<String> segments, Function<String, Optional<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return parseAll(segments, compiler).map(compiled -> mergeAll(merger, compiled));
    }

    private static String mergeAll(BiFunction<StringBuilder, String, StringBuilder> merger, List<String> compiled) {
        StringBuilder output = new StringBuilder();
        for (String segment : compiled) {
            output = merger.apply(output, segment);
        }

        return output.toString();
    }

    private static Optional<List<String>> parseAll(List<String> segments, Function<String, Optional<String>> compiler) {
        Optional<List<String>> maybeCompiled = Optional.of(new ArrayList<String>());
        for (String segment : segments) {
            maybeCompiled = maybeCompiled.flatMap(allCompiled -> compiler.apply(segment).map(compiledSegment -> {
                allCompiled.add(compiledSegment);
                return allCompiled;
            }));
        }
        return maybeCompiled;
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    private static List<String> divide(String input, BiFunction<State, Character, State> divider) {
        LinkedList<Character> queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        State state = new State(queue);
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
        }

        return state.advance().segments();
    }

    private static State divideStatementChar(State state, char c) {
        State appended = state.append(c);
        if (c == ';' && appended.isLevel()) return appended.advance();
        if (c == '}' && isShallow(appended)) return appended.advance().exit();
        if (c == '{' || c == '(') return appended.enter();
        if (c == '}' || c == ')') return appended.exit();
        return appended;
    }

    private static boolean isShallow(State state) {
        return state.depth == 1;
    }

    private static Optional<String> compileRootSegment(String input) {
        if (input.startsWith("package ")) return Optional.of("");

        String stripped = input.strip();
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                String joined = String.join("/", content.split(Pattern.quote(".")));
                imports.add("#include \"./" + joined + "\"\n");
                return Optional.of("");
            }
        }

        Optional<String> maybeClass = compileToStruct(input, "class ", new ArrayList<>());
        if (maybeClass.isPresent()) return maybeClass;

        return generatePlaceholder(input);
    }

    private static Optional<String> compileToStruct(String input, String infix, List<String> typeParams) {
        int classIndex = input.indexOf(infix);
        if (classIndex < 0) return Optional.empty();

        String afterKeyword = input.substring(classIndex + infix.length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart >= 0) {
            String name = afterKeyword.substring(0, contentStart).strip();
            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                return compileStatements(inputContent, input1 -> compileClassMember(input1, typeParams)).map(outputContent -> {
                    structs.add("struct " + name + " {\n" + outputContent + "};\n");
                    return "";
                });
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileClassMember(String input, List<String> typeParams) {
        return compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface ", typeParams))
                .or(() -> compileToStruct(input, "record ", typeParams))
                .or(() -> compileToStruct(input, "class ", typeParams))
                .or(() -> compileGlobalInitialization(input, typeParams))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> compileMethod(input, typeParams))
                .or(() -> generatePlaceholder(input));
    }

    private static @NotNull Optional<String> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(content).map(result -> "\t" + result + ";\n");
        }
        return Optional.empty();
    }

    private static Optional<String> compileGlobalInitialization(String input, List<String> typeParams) {
        return compileInitialization(input, typeParams).map(generated -> {
            globals.add(generated + ";\n");
            return "";
        });
    }

    private static Optional<String> compileInitialization(String input, List<String> typeParams) {
        if (!input.endsWith(";")) return Optional.empty();

        String withoutEnd = input.substring(0, input.length() - ";".length());
        int valueSeparator = withoutEnd.indexOf("=");
        if (valueSeparator < 0) return Optional.empty();

        String definition = withoutEnd.substring(0, valueSeparator).strip();
        String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
        return compileDefinition(definition).flatMap(outputDefinition -> {
            return compileValue(value, typeParams).map(outputValue -> {
                return outputDefinition + " = " + outputValue;
            });
        });
    }

    private static Optional<String> compileWhitespace(String input) {
        if (input.isBlank()) return Optional.of("");
        return Optional.empty();
    }

    private static Optional<String> compileMethod(String input, List<String> typeParams) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) return Optional.empty();

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) return Optional.empty();

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, definition -> {
                return compileWhitespace(definition)
                        .or(() -> compileDefinition(definition))
                        .or(() -> generatePlaceholder(definition));
            }).flatMap(outputParams -> {
                String header = "\t".repeat(0) + outputDefinition + "(" + outputParams + ")";
                String body = withParams.substring(paramEnd + ")".length()).strip();
                if (body.startsWith("{") && body.endsWith("}")) {
                    String inputContent = body.substring("{".length(), body.length() - "}".length());
                    return compileStatements(inputContent, input1 -> compileStatementOrBlock(input1, typeParams)).flatMap(outputContent -> {
                        methods.add(header + " {" + outputContent + "\n}\n");
                        return Optional.of("");
                    });
                }

                return Optional.of(header + ";");
            });
        });
    }

    private static Optional<String> compileValues(String input, Function<String, Optional<String>> compiler) {
        List<String> divided = divide(input, Main::divideValueChar);
        return compileValues(divided, compiler);
    }

    private static State divideValueChar(State state, char c) {
        if (c == ',' && state.isLevel()) return state.advance();

        State appended = state.append(c);
        if (c == '<') return appended.enter();
        if (c == '>') return appended.exit();
        return appended;
    }

    private static Optional<String> compileValues(List<String> params, Function<String, Optional<String>> compoiler) {
        return compileAndMerge(params, compoiler, Main::mergeValues);
    }

    private static Optional<String> compileStatementOrBlock(String input, List<String> typeParams) {
        return compileWhitespace(input)
                .or(() -> compileStatement(input, typeParams))
                .or(() -> compileInitialization(input, typeParams).map(value -> "\n\t" + value + ";"))
                .or(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileStatement(String input, List<String> typeParams) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String value = stripped.substring(0, stripped.length() - ";".length());
            if (value.startsWith("return ")) {
                return compileValue(value.substring("return ".length()), typeParams).map(result -> "\n\treturn " + result + ";");
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileValue(String input, List<String> typeParams) {
        String stripped = input.strip();
        if (stripped.startsWith("new ")) {
            String slice = stripped.substring("new ".length());
            int argsStart = slice.indexOf("(");
            if (argsStart >= 0) {
                String type = slice.substring(0, argsStart);
                String withEnd = slice.substring(argsStart + "(".length()).strip();
                if (withEnd.endsWith(")")) {
                    String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                    return compileType(type, typeParams).flatMap(outputType -> {
                        return compileArgs(argsString, typeParams).map(value -> outputType + value);
                    });
                }
            }
        }

        int argsStart = input.indexOf("(");
        if (argsStart >= 0) {
            String type = input.substring(0, argsStart);
            String withEnd = input.substring(argsStart + "(".length()).strip();
            if (withEnd.endsWith(")")) {
                String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                return compileValue(type, typeParams).flatMap(caller -> {
                    return compileArgs(argsString, typeParams).map(value -> caller + value);
                });
            }
        }

        int separator = input.lastIndexOf(".");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + ".".length()).strip();
            return compileValue(object, typeParams).map(compiled -> compiled + "." + property);
        }

        return generatePlaceholder(input);
    }

    private static Optional<String> compileArgs(String argsString, List<String> typeParams) {
        return compileValues(argsString, arg -> {
            return compileWhitespace(arg).or(() -> compileValue(arg, typeParams));
        }).map(args -> {
            return "(" + args + ")";
        });
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) return cache.append(element);
        return cache.append(", ").append(element);
    }

    private static Optional<String> compileDefinition(String definition) {
        int nameSeparator = definition.lastIndexOf(" ");
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

                List<String> typeParams;
                if (beforeType.endsWith(">")) {
                    String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                    int typeParamStart = withoutEnd.indexOf("<");
                    if (typeParamStart >= 0) {
                        String substring = withoutEnd.substring(typeParamStart + 1);
                        typeParams = splitValues(substring);
                    } else {
                        typeParams = Collections.emptyList();
                    }
                } else {
                    typeParams = Collections.emptyList();
                }

                String inputType = beforeName.substring(typeSeparator + " ".length());
                return compileType(inputType, typeParams).flatMap(outputType -> Optional.of(generateDefinition(typeParams, outputType, name)));
            } else {
                return compileType(beforeName, Collections.emptyList()).flatMap(outputType -> Optional.of(generateDefinition(Collections.emptyList(), outputType, name)));
            }
        }
        return Optional.empty();
    }

    private static List<String> splitValues(String substring) {
        String[] paramsArrays = substring.strip().split(Pattern.quote(","));
        return Arrays.stream(paramsArrays)
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .toList();
    }

    private static String generateDefinition(List<String> maybeTypeParams, String type, String name) {
        String typeParamsString;
        if (maybeTypeParams.isEmpty()) {
            typeParamsString = "";
        } else {
            typeParamsString = "<" + String.join(", ", maybeTypeParams) + "> ";
        }

        return typeParamsString + type + " " + name;
    }

    private static Optional<String> compileType(String input, List<String> typeParams) {
        if (input.equals("void")) return Optional.of("void");

        if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
            return Optional.of("int");
        }

        if (input.equals("char") || input.equals("Character")) {
            return Optional.of("char");
        }

        if (input.endsWith("[]")) {
            return compileType(input.substring(0, input.length() - "[]".length()), typeParams)
                    .map(value -> value + "*");
        }

        String stripped = input.strip();
        if (isSymbol(stripped)) {
            if (typeParams.contains(stripped)) {
                return Optional.of(stripped);
            } else {
                return Optional.of("struct " + stripped);
            }
        }

        if (stripped.endsWith(">")) {
            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart >= 0) {
                String base = slice.substring(0, argsStart).strip();
                String params = slice.substring(argsStart + "<".length()).strip();
                return compileValues(params, type -> {
                    return compileWhitespace(type).or(() -> compileType(type, typeParams));
                }).map(compiled -> {
                    return base + "<" + compiled + ">";
                });
            }
        }

        return generatePlaceholder(input);
    }

    private static boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) continue;
            return false;
        }
        return true;
    }

    private static Optional<String> generatePlaceholder(String input) {
        return Optional.of("/* " + input + " */");
    }
}
