package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var output = compileRoot(input);
            final var target = source.resolveSibling("Main.c");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileRoot(String input) {
        return compileAllStatements(input, Main::compileRootSegment)
                .map(output -> output + "int main(){\n\treturn 0;\n}\n")
                .orElse("");
    }

    private static Optional<String> compileAllStatements(String input, Function<String, Optional<String>> compiler) {
        return compileAll(divide(input, Main::divideAtStatementChar), compiler, Main::mergeStatements);
    }

    private static Optional<String> compileAll(List<String> segments, Function<String, Optional<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return parseAll(segments, compiler).flatMap(output -> generateAll(output, merger));
    }

    private static Optional<String> generateAll(List<String> output, BiFunction<StringBuilder, String, StringBuilder> merger) {
        var buffer = new StringBuilder();
        for (var element : output) {
            buffer = merger.apply(buffer, element);
        }

        return Optional.of(buffer.toString());
    }

    private static Optional<List<String>> parseAll(List<String> segments, Function<String, Optional<String>> compiler) {
        Optional<List<String>> maybeCompiled = Optional.of(new ArrayList<String>());
        for (var segment : segments) {
            maybeCompiled = maybeCompiled.flatMap(output -> {
                return compiler.apply(segment).map(str -> {
                    output.add(str);
                    return output;
                });
            });
        }
        return maybeCompiled;
    }

    private static StringBuilder mergeStatements(StringBuilder buffer, String element) {
        return buffer.append(element);
    }

    private static List<String> divide(String input, BiFunction<State, Character, State> applier) {
        final var queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        final var state = new State(queue);
        var current = state;
        while (true) {
            final var maybeNextState = divideWithEscapes(current, applier);
            if (maybeNextState.isPresent()) {
                current = maybeNextState.get();
            } else {
                break;
            }
        }

        return current.advance().segments();
    }

    private static Optional<State> divideWithEscapes(
            State current,
            BiFunction<State, Character, State> applicator
    ) {
        final var maybeNext = current.pop();
        if (maybeNext.isEmpty()) return Optional.empty();

        final char next = maybeNext.orElse('\0');
        return Optional.of(divideAtSingleQuotes(current, next)
                .orElseGet(() -> applicator.apply(current, next)));
    }

    private static Optional<State> divideAtSingleQuotes(State current, char next) {
        if (next != '\'') return Optional.empty();

        return current.append(next).pop().flatMap(value -> {
            final State withValue = current.append(value);
            final Optional<State> withEscaped = value == '\\'
                    ? withValue.popAndAppend()
                    : Optional.of(withValue);

            return withEscaped.flatMap(State::popAndAppend);
        });
    }

    private static State divideAtStatementChar(State current, char next) {
        final var appended = current.append(next);
        if (next == ';' && appended.isLevel()) return appended.advance();
        if (next == '}' && appended.isShallow()) return appended.advance().exit();
        if (next == '{') return appended.enter();
        if (next == '}') return appended.exit();
        return appended;
    }

    private static Optional<String> compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.isEmpty()) return Optional.of("");

        if (stripped.startsWith("package ")) return Optional.of("");
        if (stripped.startsWith("import ")) {
            final var right = stripped.substring("import ".length());
            return truncateRight(right, ";", content -> {
                final var namespace = content.split(Pattern.quote("."));
                return Optional.of("#include <" + String.join("/", namespace) + ".h>\n");
            });
        }

        return compileClass(input).or(() -> invalidate("root segment", input));
    }

    private static Optional<String> compileClass(String input) {
        return split(input, new IndexSplitter("class", new FirstLocator()), tuple -> {
            return split(tuple.right(), new IndexSplitter("{", new FirstLocator()), tuple0 -> {
                final var name = tuple0.left().strip();
                final var withEnd = tuple0.right().strip();
                return truncateRight(withEnd, "}", inputContent -> {
                    return compileAllStatements(inputContent, Main::compileClassSegment).flatMap(outputContent -> {
                        return Optional.of("struct " + name + " {\n};\n" + outputContent);
                    });
                });
            });
        });
    }

    private static Optional<String> invalidate(String type, String input) {
        return printError("Invalid " + type + ": " + input);
    }

    private static Optional<String> printError(String message) {
        System.err.println(message);
        return Optional.empty();
    }

    private static Optional<String> compileClassSegment(String input) {
        if (input.isBlank()) return Optional.of("");

        return compileMethod(input)
                .or(() -> invalidate("class segment", input));
    }

    private static Optional<String> compileMethod(String input) {
        return split(input, new IndexSplitter("(", new FirstLocator()), tuple -> {
            return compileDefinition(tuple.left().strip()).flatMap(outputDefinition -> {
                return split(tuple.right(), new IndexSplitter(")", new FirstLocator()), tuple1 -> {
                    return compileAllValues(tuple1.left(), Main::compileDefinition).flatMap(outputParams -> {
                        return Optional.of(outputDefinition + "(" +
                                outputParams +
                                "){\n}\n");
                    });
                });
            });
        });
    }

    private static Optional<String> compileAllValues(String input, Function<String, Optional<String>> compiler) {
        return compileAllValues(input, compiler, (buffer, element) -> {
            return mergeDelimited(buffer, element, ", ");
        });
    }

    private static Optional<String> compileAllValues(String input, Function<String, Optional<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compileAll(divideByValues(input), compiler, merger);
    }

    private static StringBuilder mergeDelimited(StringBuilder buffer, String element, String delimiter) {
        if (buffer.isEmpty()) return buffer.append(element);
        return buffer.append(delimiter).append(element);
    }

    private static List<String> divideByValues(String input) {
        return divide(input, (state, c) -> {
            if (c == ',' && state.isLevel()) return state.advance();

            final var appended = state.append(c);
            if (c == '<') return appended.enter();
            if (c == '>') return appended.exit();
            return appended;
        });
    }

    private static Optional<String> compileDefinition(String input) {
        final var stripped = input.strip();
        return split(stripped, new IndexSplitter(" ", new LastLocator()), tuple -> {
            final var beforeName = tuple.left();
            final var name = tuple.right();

            final var inputType = split(beforeName, new IndexSplitter(" ", new TypeSeparatorLocator()),
                    tuple1 -> Optional.of(tuple1.right())).orElse(beforeName);

            return compileType(inputType).map(outputType -> outputType + " " + name);
        });
    }

    private static Optional<String> compileType(String input) {
        final var maybeArray = truncateRight(input, "[]", array -> Optional.of(array + "*"));
        if (maybeArray.isPresent()) return maybeArray;

        final var maybeGeneric = truncateRight(input, ">", withoutEnd -> {
            return split(withoutEnd, new IndexSplitter("<", new FirstLocator()), tuple -> {
                List<String> segments = divideByValues(tuple.right());
                return parseAll(segments, Main::compileType).flatMap(compiled -> {
                    final var name = tuple.left().strip();
                    if (name.equals("Function")) {
                        final var paramType = compiled.get(0);
                        final var returns = compiled.get(1);
                        return generateFunctionalType(returns, List.of(paramType));
                    } else if (name.equals("BiFunction")) {
                        final var leftType = compiled.get(0);
                        final var rightType = compiled.get(1);
                        final var returns = compiled.get(2);
                        return generateFunctionalType(returns, List.of(leftType, rightType));
                    }

                    return generateAll(compiled, (buffer, element) -> mergeDelimited(buffer, element, "_")).map(outputParams -> name + "_" + outputParams);
                });
            });
        });
        if (maybeGeneric.isPresent()) return maybeGeneric;

        final var stripped = input.strip();
        if (isSymbol(stripped)) return Optional.of(stripped);
        return invalidate("type", input);
    }

    private static Optional<String> generateFunctionalType(String returns, List<String> params) {
        return Optional.of(returns + " (*)(" + params + ")");
    }

    private static boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (!Character.isLetter(c)) return false;
        }

        return true;
    }

    private static Optional<String> truncateRight(String input, String suffix, Function<String, Optional<String>> mapper) {
        return input.endsWith(suffix)
                ? mapper.apply(input.substring(0, input.length() - suffix.length()))
                : Optional.empty();
    }

    private static Optional<String> split(
            String input,
            Splitter splitter,
            Function<Tuple<String, String>, Optional<String>> mapper
    ) {
        return splitter.split(input).flatMap(mapper);
    }
}
