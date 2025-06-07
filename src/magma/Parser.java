package magma;

import java.util.function.Function;
import java.util.function.BiFunction;
import magma.ast.*;
import magma.compile.*;
import magma.util.*;

/**
 * Utility class for parsing source code into AST nodes.
 */
public final class Parser {
    private Parser() {}

    public static Option<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>(new Whitespace());
        }
        else {
            return new None<>();
        }
    }

    public static Value parseValue(String input, CompileState state) {
        return parseInvocation(input, state).<Value>map(value -> value)
                .or(() -> parseAccess(input, state).map(value -> value))
                .or(() -> parseIdentifier(input).map(value -> value))
                .orElseGet(() -> new Placeholder(input));
    }

    public static Option<FieldAccess> parseAccess(String input, CompileState state) {
        var stripped = input.strip();
        final var separator = stripped.lastIndexOf(".");
        if (separator >= 0) {
            final var parentString = stripped.substring(0, separator);
            final var property = stripped.substring(separator + ".".length());
            final var parent = parseValue(parentString, state);
            return new Some<>(new FieldAccess(parent, property));
        }

        return new None<>();
    }

    public static Option<Invocation> parseInvocation(String input, CompileState state) {
        var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var argumentsStart = withoutEnd.indexOf("(");
            if (argumentsStart >= 0) {
                final var callerString = withoutEnd.substring(0, argumentsStart).strip();
                final var argumentsString = withoutEnd.substring(argumentsStart + "(".length());
                final var arguments = parseValuesString(argumentsString, arg -> parseArgument(arg, state))
                        .iter()
                        .map(Parser::retainValue)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                final var caller = mapCaller(state, callerString);
                return new Some<>(parseAfterInvocation(state, caller, arguments));
            }
        }

        return new None<>();
    }

    private static Invocation parseAfterInvocation(CompileState state, Caller caller, List<Value> arguments) {
        if (caller instanceof Construction(var type)) {
            if (type instanceof TemplateType(String base, List<Type> templateArgumentTypes)) {
                if (templateArgumentTypes.isEmpty()) {
                    final var maybeStructureType = state.stack.resolveType(base);
                    if (maybeStructureType.isPresent()) {
                        final var structureType = maybeStructureType.get();
                        final var maybeConstructorDefinition = structureType.find("new");
                        if (maybeConstructorDefinition.isPresent()) {
                            final var constructorDefinition = maybeConstructorDefinition.get();
                            final var constructorDefinitionType = constructorDefinition.type;
                            if (constructorDefinitionType instanceof FunctionType functionalConstructorDefinition) {
                                final var constructorArgumentTypes = functionalConstructorDefinition.parameterTypes();

                                final var argumentTypes = arguments.iter()
                                        .map(argument -> resolveValue(argument, state))
                                        .flatMap(Iterators::fromOptional)
                                        .collect(new ListCollector<>());

                                final var resolved = constructorArgumentTypes.iter()
                                        .zip(argumentTypes.iter())
                                        .map(pair -> pair.left().extract(pair.right()))
                                        .fold(Maps.<String, Type>empty(), Map::putAll);

                                final var actualArgumentTypes = argumentTypes.iter()
                                        .map(argument -> argument.resolve(resolved))
                                        .collect(new ListCollector<>());

                                final var actualTemplateType = new TemplateType(base, actualArgumentTypes);
                                return new Invocation(new Construction(actualTemplateType), arguments);
                            }
                        }
                    }
                }
            }
        }
        return new Invocation(caller, arguments);
    }

    private static Option<Type> resolveValue(Value argument, CompileState state) {
        return switch (argument) {
            case FieldAccess fieldAccess -> new None<>();
            case Invocation invocation -> resolveInvocation(invocation, state);
            case Placeholder placeholder -> new None<>();
            case Identifier symbol -> state.stack.resolveValue(symbol.value());
        };
    }

    private static Option<Type> resolveInvocation(Invocation invocation, CompileState state) {
        final var maybeCallerType = resolveCaller(invocation.caller(), state);
        if (maybeCallerType.isPresent()) {
            final var callerType = maybeCallerType.get();
            if (callerType instanceof FunctionType type) {
                return new Some<>(type.returnType());
            }
        }

        return new None<>();
    }

    private static Option<Type> resolveCaller(Caller caller, CompileState state) {
        return switch (caller) {
            case Construction construction -> new None<>();
            case Value value -> resolveValue(value, state);
        };
    }

    private static Option<Value> retainValue(ValueArgument argument) {
        if (argument instanceof Value value) {
            return new Some<>(value);
        }
        else {
            return new None<>();
        }
    }

    private static ValueArgument parseArgument(String input, CompileState state) {
        return parseWhitespace(input).<ValueArgument>map(value -> value)
                .orElseGet(() -> parseValue(input, state));
    }

    private static Option<Identifier> parseIdentifier(String input) {
        final var stripped = input.strip();
        if (isIdentifier(stripped)) {
            return new Some<>(new Identifier(stripped));
        }
        return new None<>();
    }

    private static Caller mapCaller(CompileState state, String callerString) {
        final var caller = parseCaller(callerString, state);

        if (caller instanceof FieldAccess access) {
            final var parent = access.parent();
            if (parent instanceof Identifier(String value)) {
                final var maybeType = state.stack.resolveValue(value);
                if (maybeType.isPresent()) {
                    final var type = maybeType.get();
                    if (type instanceof FunctionType) {
                        return parent;
                    }
                }
            }
        }

        return caller;
    }

    private static Caller parseCaller(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("new ")) {
            final var afterNew = stripped.substring("new ".length());
            final var type = parseType(afterNew, state);
            return new Construction(type);
        }

        return parseValue(stripped, state);
    }

    public static DivideState foldValues(DivideState state, char c) {
        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }

    public static Parameter parseParameter(String input, CompileState state) {
        return parseWhitespace(input).<Parameter>map(parameter -> parameter)
                .or(() -> parseDefinition(input, state).map(parameter -> parameter))
                .orElseGet(() -> new Placeholder(input));
    }

    public static Option<Definition> parseDefinition(String input, CompileState state) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        final var beforeName = stripped.substring(0, nameSeparator).strip();
        final var name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isIdentifier(name)) {
            return new None<>();
        }

        final var divisions = divide(beforeName, Parser::foldTypeSeparator);
        final var maybePopped = divisions.popLast();
        if (maybePopped.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(beforeName, state), name));
        }

        final var popped = maybePopped.get();
        final var beforeTypeDivisions = popped.left();
        final var type = popped.right();
        final var compiledType = parseType(type, state);

        if (beforeTypeDivisions.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(type, state), name));
        }

        final var beforeType = joinWithDelimiter(beforeTypeDivisions);
        return new Some<>(assembleDefinition(beforeType, compiledType, name));
    }

    private static DivideState foldTypeSeparator(DivideState state, char c) {
        if (c == ' ' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }

    private static Definition assembleDefinition(String beforeType, Type compiledType, String name) {
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                final var beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                final var typeParamsString = withoutEnd.substring(typeParamStart + "<".length());
                final var typeParams = parseValuesString(typeParamsString, String::strip);

                final Option<String> beforeTypeOptional;
                beforeTypeOptional = beforeTypeParams.isEmpty() ? new None<>() : new Some<>(Generator.generatePlaceholder(beforeTypeParams));

                return new Definition(beforeTypeOptional, typeParams, compiledType, name);
            }
        }

        return new Definition(new Some<>(Generator.generatePlaceholder(beforeType)), Lists.empty(), compiledType, name);
    }

    public static Type parseType(String input, CompileState state) {
        final var stripped = input.strip();
        final var maybeTypeParam = state.stack.resolveTypeParam(stripped);
        if (maybeTypeParam.isPresent()) {
            return maybeTypeParam.get();
        }

        if (stripped.equals("String")) {
            return new StringType();
        }

        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var argumentsStart = withoutEnd.indexOf("<");
            if (argumentsStart >= 0) {
                final var base = withoutEnd.substring(0, argumentsStart).strip();
                final var inputArguments = withoutEnd.substring(argumentsStart + 1);
                final var elements = parseValuesString(inputArguments, arg -> parseTypeArgument(arg, state))
                        .iter()
                        .map(Parser::retainType)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                switch (base) {
                    case "Supplier" -> {
                        List<Type> parameterTypes = Lists.empty();
                        return new FunctionType(parameterTypes, elements.get(0));
                    }
                    case "Function" -> {
                        List<Type> parameterTypes = Lists.of(elements.get(0));
                        return new FunctionType(parameterTypes, elements.get(1));
                    }
                    case "BiFunction" -> {
                        List<Type> parameterTypes = Lists.of(elements.get(0), elements.get(1));
                        return new FunctionType(parameterTypes, elements.get(2));
                    }
                }

                return new TemplateType(base, elements);
            }
        }

        return parseIdentifier(stripped).<Type>map(value -> value)
                .orElseGet(() -> new Placeholder(input));
    }

    private static Option<Type> retainType(TypeArgument argument) {
        if (argument instanceof Type type) {
            return new Some<>(type);
        }
        else {
            return new None<>();
        }
    }

    private static TypeArgument parseTypeArgument(String input, CompileState state) {
        return parseWhitespace(input)
                .<TypeArgument>map(whitespace -> whitespace)
                .orElseGet(() -> parseType(input, state));
    }

    public static <T> List<T> parseValuesString(String input, Function<String, T> mapper) {
        return parseAll(input, Parser::foldValues, mapper);
    }

    public static boolean isIdentifier(String input) {
        final var length = input.length();
        if (length == 0) {
            return false;
        }

        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private static <T> List<T> parseAll(String input, BiFunction<DivideState, Character, DivideState> folder, Function<String, T> mapper) {
        return divide(input, folder)
                .iter()
                .map(mapper)
                .collect(new ListCollector<>());
    }

    public static List<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
        DivideState state = new DivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }

    private static String joinWithDelimiter(List<String> list) {
        return list.iter().collect(new Joiner(" ")).orElse("");
    }
}
