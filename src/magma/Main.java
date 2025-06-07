package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;

import magma.ast.*;
import magma.util.*;
import magma.util.result.*;
import magma.compile.*;

// New utility classes providing parsing and generation helpers

public class Main {
    public static void main(String[] args) {
        try {
            final var root = Paths.get(".", "src");
            final var targetRoot = Paths.get(".", "src-web");

            Files.walk(root)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(source -> compileFile(root, targetRoot, source));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void compileFile(Path root, Path targetRoot, Path source) {
        try {
            final var relative = root.relativize(source);
            Path target = targetRoot.resolve(relative);
            target = target.resolveSibling(
                    target.getFileName().toString().replaceFirst("\\.java$", ".ts"));

            Files.createDirectories(target.getParent());

            final var input = Files.readString(source);
            final var outputResult = compile(input);
            final var output = outputResult.isOk()
                    ? outputResult.unwrap()
                    : Generator.generatePlaceholder(input);
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static Result<String, String> compile(String input) {
        return compileStatements(input, input1 -> compileRootSegment(input1, new CompileState()));
    }

    private static Result<String, String> compileStatements(String input, Function<String, Result<String, String>> mapper) {
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }

    private static Result<String, String> compileAll(
            String input,
            Function<String, Result<String, String>> mapper,
            BiFunction<DivideState, Character, DivideState> folder,
            BiFunction<StringBuilder, String, StringBuilder> merger) {
        Result<StringBuilder, String> result = new Ok<>(new StringBuilder());

        var iterator = Parser.divide(input, folder).iter();
        while (true) {
            var maybeElement = iterator.next();
            if (maybeElement.isEmpty()) {
                break;
            }
            String element = maybeElement.get();
            if (result.isErr()) {
                break;
            }

            var compiled = mapper.apply(element);
            if (compiled.isErr()) {
                result = new Err<StringBuilder, String>(compiled.unwrapErr());
            } else {
                StringBuilder current = result.unwrap();
                String value = compiled.unwrap();
                result = new Ok<>(merger.apply(current, value));
            }
        }

        if (result.isOk()) {
            return new Ok<>(result.unwrap().toString());
        }

        return new Err<String, String>(result.unwrapErr());
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }


    private static DivideState foldStatements(DivideState current, char c) {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}' && appended.isShallow()) {
            return appended.advance().exit();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended;
    }

    private static Result<String, String> compileRootSegment(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return new Ok<>("");
        }

        var maybe = compileRootStructure(input, state);
        if (maybe.isPresent()) {
            return new Ok<>(maybe.get());
        }
        return new Err<String, String>(input);
    }

    private static Option<String> compileRootStructure(String input, CompileState state) {
        return compileStructure(input, "class ", "class", state).map(tuple -> {
            final var joined = join(tuple.right().structures);
            return tuple.left() + joined;
        });
    }

    private static String join(List<String> list) {
        return list.iter()
                .collect(new Joiner())
                .orElse("");
    }

    private static Option<Tuple<String, CompileState>> compileStructure(String input, String keyword, String targetInfix, CompileState state) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return new None<>();
        }

        final var modifiersString = input.substring(0, classIndex);
        final var afterClass = input.substring(classIndex + keyword.length());
        final var contentStart = afterClass.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }

        final var beforeContent = afterClass.substring(0, contentStart).strip();
        final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        final var modifiers = modifiersString.contains("public") ? "export " : "";

        return assembleStructureWithImplements(targetInfix, state, inputContent, beforeContent, modifiers);
    }

    private static Option<Tuple<String, CompileState>> assembleStructureWithImplements(String targetInfix, CompileState state, String inputContent, String beforeContent, String modifiers) {
        final var implementsIndex = beforeContent.lastIndexOf(" implements ");
        if (implementsIndex >= 0) {
            final var beforeImplements = beforeContent.substring(0, implementsIndex);
            final var implementsString = beforeContent.substring(implementsIndex + " implements ".length());
            final var implementsTypes = Parser.parseValuesString(implementsString, input -> Parser.parseType(input, state));

            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeImplements, implementsTypes);
        }
        else {
            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeContent, Lists.empty());
        }
    }

    private static Option<Tuple<String, CompileState>> assembleStructureWithParameters(String targetInfix, CompileState state, String inputContent, String modifiers, String beforeContent, List<Type> implementsTypes) {
        if (beforeContent.endsWith(")")) {
            final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
            final var paramStart = withoutParamEnd.indexOf("(");
            if (paramStart >= 0) {
                final var name = withoutParamEnd.substring(0, paramStart).strip();
                final var inputParams = withoutParamEnd.substring(paramStart + "(".length());

                // Parse type parameters before parsing record parameters so that
                // parseType can resolve them when used in parameter types.
                final var typeParams = parseTypeParamsFromName(name);
                final var withFrame = state.enter(new StructureFrame(typeParams));
                final var fields = getCollect(withFrame, inputParams);
                if (!fields.isEmpty()) {
                    return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new Some<>(fields), name);
                }
            }
        }

        return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new None<>(), beforeContent);
    }

    private static List<Definition> getCollect(CompileState state, String inputParams) {
        return Parser.divide(inputParams, Parser::foldValues)
                .iter()
                .map(input -> Parser.parseParameter(input, state))
                .map(Main::retainDefinition)
                .flatMap(Iterators::fromOptional)
                .collect(new ListCollector<>());
    }

    private static Option<Tuple<String, CompileState>> getTupleOption(
            String targetInfix,
            CompileState state,
            String inputContent,
            String modifiers,
            List<Type> implementsTypes,
            Option<List<Definition>> maybeFields,
            String name) {
        final String beforeBody;
        if (maybeFields.isPresent()) {
            final var parameters = maybeFields.get();
            beforeBody = convertParametersToBeforeBody(parameters);
        }
        else {
            beforeBody = "";
        }

        final var stripped = name.strip();
        if (stripped.endsWith(">")) {
            final var stripped1 = stripped.substring(0, stripped.length() - ">".length());
            final var typeParamsStart = stripped1.indexOf("<");
            if (typeParamsStart >= 0) {
                final var name1 = stripped1.substring(0, typeParamsStart);
                final var substring = stripped1.substring(typeParamsStart + "<".length());
                final var typeParams = parseTypeParameters(substring);
                return assembleStructure(targetInfix, state, new StructurePrototype(modifiers, name1, new TypeParamSet(typeParams), implementsTypes, maybeFields, beforeBody, inputContent));
            }
        }

        return assembleStructure(targetInfix, state, new StructurePrototype(modifiers, name, new TypeParamSet(), implementsTypes, maybeFields, beforeBody, inputContent));
    }

    private static List<TypeParam> parseTypeParameters(String substring) {
        return Parser.divide(substring, Parser::foldValues)
                .iter()
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .map(TypeParam::new)
                .collect(new ListCollector<>());
    }

    private static TypeParamSet parseTypeParamsFromName(String name) {
        if (name.endsWith(">")) {
            final var withoutEnd = name.substring(0, name.length() - ">".length());
            final var typeParamsStart = withoutEnd.indexOf("<");
            if (typeParamsStart >= 0) {
                final var substring = withoutEnd.substring(typeParamsStart + "<".length());
                final var typeParams = parseTypeParameters(substring);
                return new TypeParamSet(typeParams);
            }
        }

        return new TypeParamSet();
    }

    private static String convertParametersToBeforeBody(List<Definition> parameters) {
        final var outputParams = joinParameters(parameters);
        final var generatedFields = convertParametersToFields(parameters);
        final var assignments = joinConstructorAssignments(parameters);
        return generatedFields + "\n\tconstructor (" + outputParams + ") {" + assignments + "\n\t}";
    }

    private static String joinParameters(List<Definition> fields) {
        return fields.iter()
                .map(Definition::generate)
                .collect(new Joiner(", "))
                .orElse("");
    }

    private static String convertParametersToFields(List<Definition> fields) {
        return fields.iter()
                .map(Definition::generate)
                .map(element -> "\n\t" + element + ";")
                .collect(new Joiner())
                .orElse("");
    }

    private static Option<Tuple<String, CompileState>> assembleStructure(
            String targetInfix,
            CompileState state, StructurePrototype structurePrototype) {
        final var strippedName = structurePrototype.name().strip();
        if (!Parser.isSymbol(strippedName)) {
            return new None<>();
        }

        final var maybeWithConstructorType1 = getMaybeWithConstructorType(structurePrototype.name(), structurePrototype.maybeFields(), Maps.empty());
        final var frame = new StructureFrame(structurePrototype.typeParams()).defineStructureType(new StructureType(strippedName, maybeWithConstructorType1));
        final var classSegmentsTuple = joinClassSegments(structurePrototype.inputBody(), state.enter(frame));
        final var classSegmentsOutput = classSegmentsTuple.left().toString();
        final var classSegmentsState = classSegmentsTuple.right();

        final var maybeExited = classSegmentsState.exit();
        if (maybeExited.isPresent()) {
            final var exited = maybeExited.get();
            final var right = exited.right()
                    .iterDefinitions()
                    .map(definition -> new Tuple<>(definition.name, definition))
                    .collect(new MapCollector<>());

            final var maybeWithConstructorType = getMaybeWithConstructorType(structurePrototype.name(), structurePrototype.maybeFields(), right);
            final var structureType = new StructureType(strippedName, maybeWithConstructorType);
            final var defined = exited.left().mapLast(last -> {
                if (last instanceof StructureContainerFrame structureContainerFrame) {
                    return structureContainerFrame.defineStructureType(structureType);
                }
                else {
                    return last;
                }
            });

            final var outputContent = structurePrototype.beforeBody() + classSegmentsOutput;
            final var joinedImplements = structurePrototype.implementsTypes().isEmpty() ? "" : " implements " + Generator.generateNodes(structurePrototype.implementsTypes());
            var generated = structurePrototype.modifiers() + targetInfix + " " + strippedName + joinedImplements + " {" + outputContent + "\n}\n";

            return new Some<>(new Tuple<>("", defined.addStructure(generated)));
        }
        else {
            return new None<>();
        }
    }

    private static Map<String, Definition> getMaybeWithConstructorType(String name, Option<List<Definition>> maybeFields, Map<String, Definition> right) {
        return maybeFields.map(fields -> {
            final var constructorTypes = fields.iter()
                    .map(Definition::type)
                    .collect(new ListCollector<>());

            return right.put("new", new Definition(new FunctionType(constructorTypes, new StructureRefType(name)), "new"));
        }).orElse(right);
    }

    private static String joinConstructorAssignments(List<Definition> fields) {
        return fields.iter()
                .map(field -> {
                    final var fieldName = field.name;
                    final var content = "this." + fieldName + " = " + fieldName;
                    return generateStatement(content);
                })
                .collect(new Joiner())
                .orElse("");
    }

    private static Tuple<StringBuilder, CompileState> joinClassSegments(String inputContent, CompileState state) {
        return Parser.divide(inputContent, Main::foldStatements)
                .iter()
                .fold(new Tuple<>(new StringBuilder(), state), (tuple, element) -> {
                    final var compiled = compileClassSegment(element, tuple.right());
                    final var append = tuple.left().append(compiled.left());
                    return new Tuple<>(append, compiled.right());
                });
    }

    private static Option<Definition> retainDefinition(Parameter parameter) {
        if (parameter instanceof Definition definition) {
            return new Some<>(definition);
        }
        else {
            return new None<>();
        }
    }

    private static String generateStatement(String content) {
        return "\n" + "\t".repeat(2) + content + ";";
    }

    private static Tuple<String, CompileState> compileClassSegment(String input, CompileState state) {
        return compileWhitespaceWithState(input, state)
                .or(() -> compileStructure(input, "record ", "class", state))
                .or(() -> compileStructure(input, "class ", "class", state))
                .or(() -> compileStructure(input, "interface ", "interface", state))
                .or(() -> compileField(input, state))
                .or(() -> compileMethod(input, state))
                .orElseGet(() -> new Tuple<>(Generator.generatePlaceholder(input), state));
    }

    private static Option<Tuple<String, CompileState>> compileField(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return compileSimpleDefinition(content, state).map(definition -> new Tuple<>("\n\t" + definition + ";", state));
    }

    private static Option<String> compileSimpleDefinition(String content, CompileState state) {
        return Parser.parseDefinition(content, state).map(Definition::generate);
    }

    private static Option<Tuple<String, CompileState>> compileWhitespaceWithState(String input, CompileState state) {
        return compileWhitespace(input).map(node -> new Tuple<>(node, state));
    }

    private static Option<String> compileWhitespace(String input) {
        return Parser.parseWhitespace(input).map(Whitespace::generate);
    }

    private static Option<Tuple<String, CompileState>> compileMethod(String input, CompileState state) {
        final var paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        final var inputDefinition = input.substring(0, paramStart);
        final var withParams = input.substring(paramStart + "(".length());
        final var paramEnd = withParams.indexOf(")");
        if (paramEnd < 0) {
            return new None<>();
        }

        final var inputParams = withParams.substring(0, paramEnd);
        final var inputAfterParams = withParams.substring(paramEnd + ")".length()).strip();

        final var maybeOutputDefinition = Parser.parseDefinition(inputDefinition, state);
        if (!maybeOutputDefinition.isPresent()) {
            return new None<>();
        }

        final var outputDefinition = maybeOutputDefinition.get();
        final var parameters = Parser.parseValuesString(inputParams, input2 -> Parser.parseParameter(input2, state))
                .iter()
                .map(Main::retainDefinition)
                .flatMap(Iterators::fromOptional)
                .collect(new ListCollector<>());

        final var paramTypes = parameters.iter()
                .map(Definition::type)
                .collect(new ListCollector<>());

        final var outputParams = Generator.generateNodes(parameters);
        if (inputAfterParams.equals(";")) {
            return assembleMethod(outputDefinition, outputParams, ";", state, paramTypes);
        }

        if (!inputAfterParams.startsWith("{") || !inputAfterParams.endsWith("}")) {
            return new None<>();
        }

        final var content = inputAfterParams.substring(1, inputAfterParams.length() - 1);
        final CompileState defined = state.enter(new MethodFrame(new DefinitionSet(parameters)));
        final var outputAfterParamsResult = compileStatements(content, input1 -> compileFunctionSegments(input1, defined));
        if (outputAfterParamsResult.isErr()) {
            return new None<>();
        }
        final String outputAfterParams = outputAfterParamsResult.unwrap();
        return assembleMethod(outputDefinition, outputParams, " {" + outputAfterParams + "\n\t}", state, paramTypes);
    }

    private static Option<Tuple<String, CompileState>> assembleMethod(Definition outputDefinition, String outputParams, String outputAfterParams, CompileState state, List<Type> paramTypes) {
        final var header = outputDefinition.generateWithAfterName("(" + outputParams + ")");
        final var generated = "\n\t" + header + outputAfterParams;
        final var definition = outputDefinition.mapType(type -> new FunctionType(paramTypes, type));

        final var withLast = state.mapLast(last -> {
            if (last instanceof StructureFrame structureFrame) {
                return structureFrame.define(definition);
            }
            else {
                return last;
            }
        });

        return new Some<>(new Tuple<>(generated, withLast));
    }

    private static Result<String, String> compileFunctionSegments(String input, CompileState state) {
        var maybeWhitespace = compileWhitespace(input);
        if (maybeWhitespace.isPresent()) {
            return new Ok<>(maybeWhitespace.get());
        }

        var maybeStatement = compileFunctionStatement(input, state);
        if (maybeStatement.isPresent()) {
            return new Ok<>(maybeStatement.get());
        }

        return new Err<String, String>(input);
    }

    private static Option<String> compileFunctionStatement(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        return compileFunctionStatementValue(withoutEnd, state).map(value -> "\n\t\t" + value + ";");
    }

    private static Option<String> compileFunctionStatementValue(String withoutEnd, CompileState state) {
        if (withoutEnd.startsWith("return ")) {
            final var value = withoutEnd.substring("return ".length());
            final var generated = Parser.parseValue(value, state);
            return new Some<>("return " + generated.generate());
        }
        else {
            return new None<>();
        }
    }


}