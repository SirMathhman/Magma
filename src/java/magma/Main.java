package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        JavaFiles.walk(root).match(files -> {
            final var sources = files.stream().filter(path -> path.toString().endsWith(".java")).toList();
            return Main.runWithSources(sources, root);
        }, Some::new).ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> runWithSources(final Iterable<Path> sources, final Path root) {
        for (final var source : sources) {
            final var maybe = Main.runWithSource(root, source);
            if (maybe.isPresent())
                return maybe;
        }

        return new None<>();
    }

    private static Optional<IOException> runWithSource(final Path root, final Path source) {
        final var relative = root.relativize(source.getParent());
        return JavaFiles.readString(source).match(input -> Main.runWithInput(source, input, relative), Some::new);
    }

    private static Optional<IOException> runWithInput(final Path source, final CharSequence input,
                                                      final Path relative) {
        final var output = Main.compileRoot(input);
        final var targetParent = Paths.get(".", "src", "node").resolve(relative);
        return Main.extracted1(targetParent).or(() -> Main.compileAndWrite(source, targetParent, output));
    }

    private static Optional<IOException> compileAndWrite(final Path source, final Path targetParent,
                                                         final CharSequence output) {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);
        final var target = targetParent.resolve(name + ".ts");
        return JavaFiles.writeString(target, output);
    }

    private static Optional<IOException> extracted1(final Path targetParent) {
        if (!Files.exists(targetParent))
            return JavaFiles.createDirectories(targetParent);
        return new None<>();
    }

    private static String compileRoot(final CharSequence input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, Main::foldStatements, mapper, "");
    }

    private static String compileAll(final CharSequence input, final BiFunction<State, Character, State> folder,
                                     final Function<String, String> mapper, final CharSequence delimiter) {
        return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
    }

    private static String compileRootSegment(final String input) {
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    }

    private static String compileRootSegmentValue(final String input) {
        if (input.isBlank())
            return "";
        return Main.compileNamespaced(input).or(() -> Main.compileStructure(input))
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileNamespaced(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ") || strip.startsWith("import "))
            return new Some<>("");
        return new None<>();
    }

    private static Optional<String> compileStructure(final String input) {
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
            return new None<>();

        final var withoutEnd = input.substring(0, input.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart)
            return new None<>();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var content = withoutEnd.substring(contentStart + "{".length());
        final var definition = Main.parseStructureHeader(beforeContent);
        final String structName;
        final String joined;
        if (definition instanceof final StructureHeader header) {
            if (header.annotations().contains("Actual"))
                return new Some<>("");

            structName = header.name();
            final var parameters = header.parameters();
            if (parameters.isEmpty())
                joined = "";
            else {
                final var joinedParameters = parameters.stream().map(Parameter::generate)
                                                       .map(result -> Main.LINE_SEPARATOR + "\t" + result + ";")
                                                       .collect(Collectors.joining());

                final var constructorParams =
                        parameters.stream().map(Parameter::generate).collect(Collectors.joining(", "));

                final var names = parameters.stream().<Optional<String>>map(parameter -> {
                    if (parameter instanceof final Definition definition1)
                        return new Some<>(definition1.name());
                    else
                        return new None<>();
                }).flatMap(Optional::stream).toList();

                final var joinedNames = names.stream().map(name -> "\n\t\tthis." + name + " = " + name + ";")
                                             .collect(Collectors.joining());

                joined = joinedParameters + "\n\tconstructor (" + constructorParams + ") {" + joinedNames + "\n\t}";
            }
        } else {
            structName = "?";
            joined = "";
        }

        final var compiled =
                Main.compileStatements(content, input1 -> Main.compileStructureSegment(input1, structName));
        return new Some<>(definition.generate() + " {" + joined + compiled + Main.LINE_SEPARATOR + "}");
    }

    private static String compileStructureSegment(final String input, final CharSequence structName) {
        final var strip = input.strip();
        if (strip.isEmpty())
            return "";

        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
    }

    private static String compileStructureSegmentValue(final String input, final CharSequence structName) {
        return Main.compileStatement(input, input1 -> Main.parseAssignment(input1, 1).map(Assignment::generate))
                   .or(() -> Main.compileMethod(input, structName)).orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileStatement(final String input,
                                                     final Function<String, Optional<String>> mapper) {
        if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
            return new None<>();

        final var withoutEnd = input.substring(0, input.length() - ";".length());
        return mapper.apply(withoutEnd).map(result -> result + ";");
    }

    private static Optional<String> compileMethod(final String input, final CharSequence structName) {
        final var paramEnd = input.indexOf(')');
        if (0 > paramEnd)
            return new None<>();

        final var withParams = input.substring(0, paramEnd);
        final var paramStart = withParams.indexOf('(');
        if (0 > paramStart)
            return new None<>();

        final var definition = withParams.substring(0, paramStart);
        final var params = withParams.substring(paramStart + "(".length());
        final var joinedParams = "(" + Main.compileParameters(params) + ")";

        final var withBraces = input.substring(paramEnd + ")".length()).strip();
        final var oldHeader = Main.parseMethodHeader(definition, structName);
        final var newHeader = Main.modifyMethodHeader(oldHeader);

        final String outputContent;
        if (";".contentEquals(withBraces) ||
            (newHeader instanceof final Definition definition1 && definition1.annotations().contains("Actual")))
            outputContent = ";";
        else if (!withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
                 '}' == withBraces.charAt(withBraces.length() - 1)) {
            final var substring = withBraces.substring(1, withBraces.length() - 1);
            final var compiled = Main.compileFunctionSegments(substring, 2);
            outputContent = " {" + compiled + Main.LINE_SEPARATOR + "\t}";
        } else
            return new None<>();

        return new Some<>(newHeader.generateWithAfterName(joinedParams) + outputContent);
    }

    private static String compileParameters(final CharSequence params) {
        return Main.compileValues(params, input -> Main.parseParameter(input).generate());
    }

    private static MethodHeader modifyMethodHeader(final MethodHeader header) {
        return switch (header) {
            case final Definition definition -> definition.mapModifiers(oldModifiers -> {
                return Lists.empty();
            });
            default -> header;
        };
    }

    private static String compileFunctionSegments(final CharSequence substring, final int depth) {
        return Main.compileStatements(substring, input -> Main.compileFunctionSegment(input, depth));
    }

    private static Parameter parseParameter(final String input) {
        if (input.isBlank())
            return new Whitespace();

        return Main.parseDefinitionOrPlaceholder(input);
    }

    private static String compileFunctionSegment(final String input, final int depth) {
        if (input.isBlank())
            return "";
        return Main.compileConditional(input, depth).or(() -> Main.compileElse(input, depth))
                   .or(() -> Main.compileStatement(input, input1 -> Main.compileFunctionStatementValue(input1, depth)))
                   .map(value -> System.lineSeparator() + "\t".repeat(depth) + value)
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileElse(final String input, final int depth) {
        final var strip = input.strip();
        if (strip.startsWith("else")) {
            final var substring = strip.substring("else".length());
            return new Some<>("else " + Main.compileBlockOrStatement(depth, substring));
        } else
            return new None<>();
    }

    private static Optional<String> compileFunctionStatementValue(final String input, final int depth) {
        return Main.compileReturn(input, depth).or(() -> Main.compileInvokable(input, depth))
                   .or(() -> Main.parseAssignment(input, depth).map(assignment -> {
                       return assignment.mapAssignable(assignable -> {
                           if (assignable instanceof final Definition definition)
                               return definition.withModifier("let");
                           return assignable;
                       });
                   }).map(Assignment::generate));
    }

    private static Optional<String> compileReturn(final String input, final int depth) {
        final var strip = input.strip();
        if (strip.startsWith("return ")) {
            final var slice = strip.substring("return ".length());
            return new Some<>("return " + Main.compileValueOrPlaceholder(slice, depth));
        }

        return new None<>();
    }

    private static Optional<String> compileConditional(final String input, final int depth) {
        final var strip = input.strip();
        if (!strip.startsWith("if"))
            return new None<>();

        final var slice = strip.substring("if".length()).strip();
        if (slice.isEmpty() || '(' != slice.charAt(0))
            return new None<>();

        final var substring = slice.substring(1);
        return Main.divide(substring, Main::foldConditional).popFirst()
                   .flatMap(tuple -> Main.compileConditionalSegments(tuple, depth));
    }

    private static Optional<String> compileConditionalSegments(final Tuple<String, ListLike<String>> tuple,
                                                               final int depth) {
        final var substring1 = tuple.left();
        if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
            return new None<>();

        final var condition = substring1.substring(0, substring1.length() - 1);
        final var joined = tuple.right().stream().collect(Collectors.joining());
        final var compiled = Main.compileBlockOrStatement(depth, joined);
        return new Some<>("if (" + Main.compileValueOrPlaceholder(condition, depth) + ")" + compiled);
    }

    private static String compileBlockOrStatement(final int depth, final String input) {
        return Main.compileBlock(depth, input).orElseGet(() -> Main.compileFunctionSegment(input.strip(), depth + 1));
    }

    private static Optional<String> compileBlock(final int depth, final String input) {
        if (!Main.isBlock(input.strip()))
            return new None<>();

        final var compiled1 =
                Main.compileFunctionSegments(input.strip().substring(1, input.strip().length() - 1), depth + 1);
        final String compiled = "{" + compiled1 + Main.LINE_SEPARATOR + "\t".repeat(depth) + "}";
        return new Some<>(compiled);

    }

    private static boolean isBlock(final CharSequence withBraces) {
        return !withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
               '}' == withBraces.charAt(withBraces.length() - 1);
    }

    private static State foldConditional(final State state, final char c) {
        final var appended = state.append(c);
        if ('(' == c)
            return appended.enter();
        if (')' == c) {
            if (appended.isLevel())
                return appended.advance();
            return appended.exit();
        }
        return appended;
    }

    private static MethodHeader parseMethodHeader(final String input, final CharSequence structName) {
        return Main.parseConstructor(input, structName).orElseGet(() -> Main.parseDefinitionOrPlaceholder(input));
    }

    private static Optional<MethodHeader> parseConstructor(final String input, final CharSequence structName) {
        final var strip = input.strip();
        final var index = strip.lastIndexOf(' ');
        if (0 <= index) {
            final var name = strip.substring(index + " ".length()).strip();
            if (name.contentEquals(structName))
                return new Some<>(new Constructor());
        }

        return new None<>();
    }

    private static Optional<Assignment> parseAssignment(final String input, final int depth) {
        final var separator = input.indexOf('=');
        if (0 <= separator) {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            final var assignable = Main.parseDefinitionOrPlaceholder(before);
            return new Some<>(new Assignment(assignable, Main.compileValueOrPlaceholder(after, depth)));
        }
        return new None<>();
    }

    private static String compileValueOrPlaceholder(final String input, final int depth) {
        return Main.compileValue(input, depth).orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileValue(final String input, final int depth) {
        final var maybeLambda = Main.compileLambda(input, depth);
        if (maybeLambda.isPresent())
            return maybeLambda;

        final var maybeOperator = Main.compileOperators(input, depth);
        if (maybeOperator.isPresent())
            return maybeOperator;

        final var maybeInvocation = Main.compileInvokable(input, depth);
        if (maybeInvocation.isPresent())
            return maybeInvocation;

        final var dataSeparator = input.lastIndexOf('.');
        if (0 <= dataSeparator) {
            final var value = input.substring(0, dataSeparator);
            final var property = input.substring(dataSeparator + ".".length()).strip();
            if (Main.isSymbol(property))
                return Main.compileValue(value, depth).map(result -> result + "." + property);
        }

        final var methodSeparator = input.lastIndexOf("::");
        if (0 <= methodSeparator) {
            final var value = input.substring(0, methodSeparator);
            final var property = input.substring(methodSeparator + "::".length()).strip();
            if (Main.isSymbol(property))
                return Main.compileValue(value, depth).map(result -> "arg => " + result + "." + property + "(arg)");
        }

        final var strip = input.strip();
        if (!strip.isEmpty() && '!' == strip.charAt(0)) {
            final var substring = strip.substring(1);
            return Main.compileValue(substring, depth).map(value -> "!" + value);
        }

        if (Main.isNumber(strip))
            return new Some<>(strip);

        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
            return new Some<>(strip);

        if (Main.isChar(strip))
            return new Some<>(strip);

        if (Main.isSymbol(strip))
            return new Some<>(strip);

        return new None<>();
    }

    private static Optional<String> compileLambda(final String input, final int depth) {
        final var arrowIndex = input.indexOf("->");
        if (0 > arrowIndex)
            return new None<>();

        final var before = input.substring(0, arrowIndex).strip();
        if (!Main.isSymbol(before))
            return new None<>();

        final var after = input.substring(arrowIndex + "->".length());
        return Main.compileBlock(depth, after).or(() -> Main.compileValue(after, depth))
                   .map(afterResult -> before + " => " + afterResult);
    }

    private static Optional<String> compileOperators(final String input, final int depth) {
        return Main.compileOperator(input, ">=", depth).or(() -> Main.compileOperator(input, "==", depth))
                   .or(() -> Main.compileOperator(input, "+", depth)).or(() -> Main.compileOperator(input, "<", depth))
                   .or(() -> Main.compileOperator(input, "<=", depth))
                   .or(() -> Main.compileOperator(input, "||", depth))
                   .or(() -> Main.compileOperator(input, "!=", depth)).or(() -> Main.compileOperator(input, "-", depth))
                   .or(() -> Main.compileOperator(input, "&&", depth))
                   .or(() -> Main.compileOperator(input, ">", depth));
    }

    private static boolean isChar(final CharSequence strip) {
        return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) &&
               3 <= strip.length();
    }

    private static Optional<String> compileOperator(final String input, final String operator, final int depth) {
        final var i = input.indexOf(operator);
        if (0 > i)
            return new None<>();

        final var leftSlice = input.substring(0, i);
        final var rightSlice = input.substring(i + operator.length());
        return Main.compileValue(leftSlice, depth).flatMap(
                left -> Main.compileValue(rightSlice, depth).map(right -> left + " " + operator + " " + right));
    }

    private static Optional<String> compileInvokable(final String input, final int depth) {
        final var strip = input.strip();
        if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
            return new None<>();

        final var withoutEnd = strip.substring(0, strip.length() - ")".length());
        return Main.divide(withoutEnd, Main::foldInvocation).popLast()
                   .flatMap(tuple -> Main.handleInvocationSegments(tuple, depth));
    }

    private static State foldInvocation(final State state, final char c) {
        final var appended = state.append(c);
        if ('(' == c) {
            final var entered = appended.enter();
            if (entered.isShallow())
                return entered.advance();
            else
                return entered;
        }
        if (')' == c)
            return appended.exit();
        return appended;
    }

    private static boolean isSymbol(final CharSequence input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Main.isSymbolChar(c, i))
                continue;
            return false;
        }
        return true;
    }

    private static boolean isSymbolChar(final char c, final int i) {
        return Character.isLetter(c) || (0 != i && Character.isDigit(c)) || '_' == c;
    }

    private static boolean isNumber(final CharSequence input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    private static Assignable parseDefinitionOrPlaceholder(final String input) {
        final var strip = input.strip();
        return Main.parseDefinition(strip).<Assignable>map(value -> {
            final var newModifiers = Lists.<String>empty();
            return new Definition(value.annotations(), newModifiers, value.typeParams(), value.name(), value.type());
        }).orElseGet(() -> new Placeholder(strip));
    }

    private static Optional<Definition> parseDefinition(final String strip) {
        final var separator = strip.lastIndexOf(' ');
        if (0 > separator)
            return new None<>();

        final var beforeName = strip.substring(0, separator);
        final var name = strip.substring(separator + " ".length());

        return Main.divide(beforeName, Main::foldTypeSeparator).popLast().flatMap(tuple -> {
            final var beforeType = tuple.left().stream().collect(Collectors.joining(" "));
            final var type = tuple.right();

            return Main.divide(beforeType, Main::foldTypeSeparator).popLast().flatMap(typeParamDivisionsTuple -> {
                final var joined = typeParamDivisionsTuple.left().stream().collect(Collectors.joining(" "));
                final var typeParamsString = typeParamDivisionsTuple.right().strip();

                if (typeParamsString.startsWith("<") && typeParamsString.endsWith(">")) {
                    final var slice = typeParamsString.substring(1, typeParamsString.length() - 1);
                    final var typeParams = new JavaList<>(
                            Main.divideValues(slice).stream().map(String::strip).filter(value -> !value.isEmpty())
                                .toList());

                    return Main.assemble(joined, typeParams, name, type);
                } else
                    return new None<>();
            }).or(() -> {
                return Main.assemble(beforeType, Lists.empty(), name, type);
            });
        }).or(() -> {
            return Main.assemble("", Lists.empty(), name, beforeName);
        });
    }

    private static Optional<Definition> assemble(final String beforeTypeParams, final ListLike<String> typeParams,
                                                 final String name, final String type) {
        final var annotationIndex = beforeTypeParams.lastIndexOf('\n');
        if (0 <= annotationIndex) {
            final var annotationString = beforeTypeParams.substring(0, annotationIndex);
            final var modifiersString = beforeTypeParams.substring(annotationIndex + "\n".length());
            return new Some<>(
                    new Definition(Main.parseAnnotations(annotationString), Main.parseModifiers(modifiersString),
                                   typeParams, name, Main.compileType(type)));
        } else {
            final var modifiers = Main.parseModifiers(beforeTypeParams);
            return new Some<>(new Definition(Lists.empty(), modifiers, typeParams, name, Main.compileType(type)));
        }
    }

    private static JavaList<String> parseModifiers(final String joined) {
        final var list = Main.divide(joined, Main.foldByDelimiter(' ')).stream().map(String::strip)
                             .filter(value -> !value.isEmpty()).collect(Collectors.toCollection(ArrayList::new));

        return new JavaList<>(list);
    }

    private static BiFunction<State, Character, State> foldByDelimiter(final char delimiter) {
        return (state, c) -> {
            if (c == delimiter)
                return state.advance();
            return state.append(c);
        };
    }

    private static State foldTypeSeparator(final State state, final Character c) {
        if (' ' == c && state.isLevel())
            return state.advance();

        final var appended = state.append(c);
        if ('<' == c)
            return appended.enter();
        if ('>' == c)
            return appended.exit();
        return appended;
    }

    private static String compileType(final String input) {
        final var strip = input.strip();
        if ("var".contentEquals(strip))
            return "any";
        if ("String".contentEquals(strip))
            return "string";
        if ("int".contentEquals(strip))
            return "number";
        if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ">".length());
            final var start = withoutEnd.indexOf('<');
            if (0 <= start) {
                final var base = withoutEnd.substring(0, start);
                final var argument = withoutEnd.substring(start + "<".length());
                final var list =
                        Main.divideValues(argument).stream().map(String::strip).filter(value -> !value.isEmpty())
                            .toList();
                if (list.isEmpty())
                    return base;
                else {
                    final var compiled = list.stream().map(Main::compileType).collect(Collectors.joining(", "));
                    return base + "<" + compiled + ">";
                }
            }
        }

        if (strip.endsWith("[]")) {
            final var slice = strip.substring(0, strip.length() - "[]".length());
            final var compiled = Main.compileType(slice);
            return compiled + "[]";
        }

        if (Main.isSymbol(strip))
            return strip;

        return Placeholder.generate(strip);
    }

    private static ListLike<String> divideValues(final CharSequence argument) {
        return Main.divide(argument, Main::foldValues);
    }

    private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
        return Main.divideValues(input).stream().map(mapper).collect(Collectors.joining(", "));
    }

    private static State foldValues(final State state, final char c) {
        if (',' == c && state.isLevel())
            return state.advance();

        final var appended = state.append(c);
        if ('-' == c) {
            final var peek = appended.peek();
            if (peek.filter(value -> '>' == value).isPresent())
                return appended.popAndAppendToOption().orElse(appended);
        }

        if ('<' == c || '(' == c)
            return appended.enter();
        if ('>' == c || ')' == c)
            return appended.exit();
        return appended;
    }

    private static StructureDefinition parseStructureHeader(final String input) {
        return Main.parseStructureHeaderWithKeyword(input, "class", "class")
                   .or(() -> Main.parseStructureHeaderWithKeyword(input, "record", "class"))
                   .or(() -> Main.parseStructureHeaderWithKeyword(input, "interface", "interface"))
                   .orElseGet(() -> new Placeholder(input));
    }

    private static Optional<StructureDefinition> parseStructureHeaderWithKeyword(final String input,
                                                                                 final String keyword,
                                                                                 final String type) {
        final var classIndex = input.indexOf(keyword + " ");
        if (0 > classIndex)
            return new None<>();

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + (keyword + " ").length()).strip();

        final var permitsIndex = afterKeyword.indexOf("permits");
        if (0 <= permitsIndex) {
            final var substring = afterKeyword.substring(0, permitsIndex);
            return Main.getStructureDefinitionSome(type, beforeKeyword, substring);
        }
        return Main.getStructureDefinitionSome(type, beforeKeyword, afterKeyword);
    }

    private static Some<StructureDefinition> getStructureDefinitionSome(final String type, final String beforeKeyword,
                                                                        final String afterKeyword) {
        final var implementsIndex = afterKeyword.indexOf("implements ");
        if (0 <= implementsIndex) {
            final var beforeImplements = afterKeyword.substring(0, implementsIndex).strip();
            final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
            return new Some<>(Main.complete(type, beforeKeyword, beforeImplements, new Some<>(afterImplements)));
        } else
            return new Some<>(Main.complete(type, beforeKeyword, afterKeyword, new None<>()));
    }

    private static StructureHeader complete(final String type, final String beforeKeyword,
                                            final String beforeImplements, final Optional<String> maybeImplements) {
        final var strip = beforeImplements.strip();
        if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ")".length());
            final var contentStart = withoutEnd.indexOf('(');
            if (0 <= contentStart) {
                final var strip1 = withoutEnd.substring(0, contentStart).strip();
                final var substring = withoutEnd.substring(contentStart + "(".length());
                final ListLike<Parameter> parameters =
                        new JavaList<>(Main.divideValues(substring).stream().map(Main::parseParameter).toList());
                return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1, parameters);
            }
        }

        return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements,
                                                      Lists.empty());
    }

    private static StructureHeader parseStructureHeaderByAnnotations(final String type, final String beforeKeyword,
                                                                     final Optional<String> maybeImplements,
                                                                     final String strip1,
                                                                     final ListLike<Parameter> parameters) {
        final var index = beforeKeyword.lastIndexOf(System.lineSeparator());
        if (0 <= index) {
            final var annotations = Main.parseAnnotations(beforeKeyword.substring(0, index));
            final var substring1 = beforeKeyword.substring(index + System.lineSeparator().length());
            return new StructureHeader(type, annotations, substring1, strip1, maybeImplements, parameters);
        }

        return new StructureHeader(type, Lists.empty(), beforeKeyword, strip1, maybeImplements, parameters);
    }

    private static ListLike<String> parseAnnotations(final String input) {
        final var copy = Arrays.stream(Pattern.compile("\\n").split(input.strip())).map(String::strip)
                               .filter(value -> !value.isEmpty()).map(value -> value.substring(1))
                               .collect(Collectors.toCollection(ArrayList::new));
        return new JavaList<>(copy);
    }

    private static ListLike<String> divide(final CharSequence input, final BiFunction<State, Character, State> folder) {
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop().toTuple(new Tuple<>(current, '\0'));
            if (maybe.left()) {
                final var tuple = maybe.right();
                current = tuple.left();
                current = Main.fold(current, tuple.right(), folder);
            } else
                break;
        }

        return current.advance().unwrap();
    }

    private static State fold(final State state, final char c, final BiFunction<State, Character, State> folder) {
        return Main.foldSingleQuotes(state, c).or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> folder.apply(state, c));
    }

    private static Optional<State> foldDoubleQuotes(final State state, final char c) {
        if ('\"' != c)
            return new None<>();

        final var current = state.append('\"');
        while (true) {
            final var maybeTuple =
                    current.popAndAppendToTuple().flatMap(tuple -> Main.getObjectOptional(tuple, current));
            if (maybeTuple.isEmpty())
                break;
        }

        return new Some<>(current);

    }

    private static Optional<State> getObjectOptional(final Tuple<State, Character> tuple, final State current) {
        final var left = tuple.left();

        final var next = tuple.right();
        if ('\\' == next)
            return new Some<>(left.popAndAppendToOption().orElse(current));
        if ('\"' == next)
            return new None<>();

        return new Some<>(left);
    }

    private static Optional<State> foldSingleQuotes(final State state, final char c) {
        if ('\'' != c)
            return new None<>();
        return state.append(c).popAndAppendToTuple().flatMap(tuple -> {
            if ('\\' == tuple.right())
                return tuple.left().popAndAppendToOption();
            return new Some<>(tuple.left());
        }).flatMap(State::popAndAppendToOption);
    }

    private static State foldStatements(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}' == c && appended.isShallow())
            return appended.exit().advance();
        if ('{' == c || '(' == c)
            return appended.enter();
        if ('}' == c || ')' == c)
            return appended.exit();
        return appended;
    }

    private static Optional<String> handleInvocationSegments(final Tuple<ListLike<String>, String> tuple,
                                                             final int depth) {
        final var joined = tuple.left().stream().collect(Collectors.joining());
        if (joined.isEmpty() || '(' != joined.charAt(joined.length() - 1))
            return new None<>();

        final var substring = joined.substring(0, joined.length() - "(".length());
        final var argument = tuple.right();
        return Main.compileCaller(substring, depth).map(caller -> caller + "(" + Main.compileValues(argument,
                                                                                                    input -> Main.compileValueOrPlaceholder(
                                                                                                            input,
                                                                                                            depth)) +
                                                                  ")");
    }

    private static Optional<String> compileCaller(final String input, final int depth) {
        final var strip = input.strip();
        if (strip.startsWith("new ")) {
            final var substring = strip.substring("new ".length());
            return new Some<>("new " + Main.compileType(substring));
        }

        return Main.compileValue(strip, depth);
    }
}
