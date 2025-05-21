package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.Context;
import magma.app.compile.Registry;
import magma.app.compile.Stack;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.SplitComposable;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.define.Definition;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.symbol.Symbols;
import magma.app.compile.type.Type;

public final class RootCompiler {
    private static Tuple2<CompileState, String> compileRootSegment(CompileState state, String input) {
        return OrRule.compileOrPlaceholder(state, input, Lists.of(
                WhitespaceCompiler::compileWhitespace,
                RootCompiler::compileNamespaced,
                RootCompiler.createStructureRule("class ", "class "),
                RootCompiler.createStructureRule("interface ", "interface "),
                RootCompiler.createStructureRule("record ", "class "),
                RootCompiler.createStructureRule("enum ", "class ")
        ));
    }

    private static Rule<String> createStructureRule(String sourceInfix, String targetInfix) {
        return (CompileState state, String input1) -> new SplitComposable<Tuple2<CompileState, String>>(new LocatingSplitter(sourceInfix, new FirstLocator()), Composable.toComposable((String beforeInfix, String afterInfix) -> new SplitComposable<Tuple2<CompileState, String>>(new LocatingSplitter("{", new FirstLocator()), Composable.toComposable((String beforeContent, String withEnd) -> new SuffixComposable<Tuple2<CompileState, String>>("}", (String inputContent) -> SplitComposable.compileLast(beforeInfix, "\n", (String s, String s2) -> {
            var annotations = DefiningCompiler.parseAnnotations(s);
            if (annotations.contains("Actual")) {
                return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(state, ""));
            }

            return RootCompiler.compileStructureWithImplementing(state, annotations, DefiningCompiler.parseModifiers(s2), targetInfix, beforeContent, inputContent);
        }).or(() -> {
            var modifiers = DefiningCompiler.parseModifiers(beforeContent);
            return RootCompiler.compileStructureWithImplementing(state, Lists.empty(), modifiers, targetInfix, beforeContent, inputContent);
        })).apply(Strings.strip(withEnd)))).apply(afterInfix))).apply(input1);
    }

    private static Option<Tuple2<CompileState, String>> compileStructureWithImplementing(CompileState state, List<String> annotations, List<String> modifiers, String targetInfix, String beforeContent, String content) {
        return SplitComposable.compileLast(beforeContent, " implements ", (String s, String s2) -> TypeCompiler.parseType(state, s2).flatMap((Tuple2<CompileState, Type> implementingTuple) -> RootCompiler.compileStructureWithExtends(implementingTuple.left(), annotations, modifiers, targetInfix, s, new Some<Type>(implementingTuple.right()), content))).or(() -> RootCompiler.compileStructureWithExtends(state, annotations, modifiers, targetInfix, beforeContent, new None<Type>(), content));
    }

    private static Option<Tuple2<CompileState, String>> compileStructureWithExtends(CompileState state, List<String> annotations, List<String> modifiers, String targetInfix, String beforeContent, Option<Type> maybeImplementing, String inputContent) {
        Splitter splitter = new LocatingSplitter(" extends ", new FirstLocator());
        return new SplitComposable<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String beforeExtends, String afterExtends) -> ValueCompiler.values((CompileState inner0, String inner1) -> TypeCompiler.parseType(inner0, inner1)).apply(state, afterExtends)
                .flatMap((Tuple2<CompileState, List<Type>> compileStateListTuple2) -> RootCompiler.compileStructureWithParameters(compileStateListTuple2.left(), annotations, modifiers, targetInfix, beforeExtends, compileStateListTuple2.right(), maybeImplementing, inputContent)))).apply(beforeContent).or(() -> RootCompiler.compileStructureWithParameters(state, annotations, modifiers, targetInfix, beforeContent, Lists.empty(), maybeImplementing, inputContent));
    }

    private static Option<Tuple2<CompileState, String>> compileStructureWithParameters(CompileState state, List<String> annotations, List<String> modifiers, String targetInfix, String beforeContent, Iterable<Type> maybeSuperType, Option<Type> maybeImplementing, String inputContent) {
        Splitter splitter1 = new LocatingSplitter("(", new FirstLocator());
        return new SplitComposable<Tuple2<CompileState, String>>(splitter1, Composable.toComposable((String rawName, String withParameters) -> {
            Splitter splitter = new LocatingSplitter(")", new FirstLocator());
            return new SplitComposable<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String parametersString, String _) -> {
                var name = Strings.strip(rawName);

                var parametersTuple = DefiningCompiler.parseParameters(state, parametersString);
                var parameters = DefiningCompiler.retainDefinitionsFromParameters(parametersTuple.right());

                return RootCompiler.compileStructureWithTypeParams(parametersTuple.left(), targetInfix, inputContent, name, parameters, maybeImplementing, annotations, modifiers, maybeSuperType);
            })).apply(withParameters);
        })).apply(beforeContent).or(() -> RootCompiler.compileStructureWithTypeParams(state, targetInfix, inputContent, beforeContent, Lists.empty(), maybeImplementing, annotations, modifiers, maybeSuperType));
    }

    private static Option<Tuple2<CompileState, String>> compileStructureWithTypeParams(CompileState state, String infix, String content, String beforeParams, Iterable<Definition> parameters, Option<Type> maybeImplementing, List<String> annotations, List<String> modifiers, Iterable<Type> maybeSuperType) {
        return new SuffixComposable<Tuple2<CompileState, String>>(">", (String withoutTypeParamEnd) -> {
            Splitter splitter = new LocatingSplitter("<", new FirstLocator());
            return new SplitComposable<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String name, String typeParamsString) -> {
                var typeParams = DefiningCompiler.divideValues(typeParamsString);
                return RootCompiler.assembleStructure(state, annotations, modifiers, infix, name, typeParams, parameters, maybeImplementing, content, maybeSuperType);
            })).apply(withoutTypeParamEnd);
        }).apply(Strings.strip(beforeParams)).or(() -> RootCompiler.assembleStructure(state, annotations, modifiers, infix, beforeParams, Lists.empty(), parameters, maybeImplementing, content, maybeSuperType));
    }

    private static Option<Tuple2<CompileState, String>> assembleStructure(
            CompileState state,
            List<String> annotations,
            List<String> oldModifiers,
            String infix,
            String rawName,
            Iterable<String> typeParams,
            Iterable<Definition> parameters,
            Option<Type> maybeImplementing,
            String content,
            Iterable<Type> maybeSuperType
    ) {
        var name = Strings.strip(rawName);
        if (!Symbols.isSymbol(name)) {
            return new None<Tuple2<CompileState, String>>();
        }

        var outputContentTuple = FunctionSegmentCompiler.compileStatements(state.mapStack((Stack stack) -> stack.pushStructureName(name)), content, RootCompiler::compileClassSegment);
        var outputContentState = outputContentTuple.left().mapStack((Stack stack1) -> stack1.popStructureName());
        var outputContent = outputContentTuple.right();

        var constructorString = RootCompiler.generateConstructorFromRecordParameters(parameters);
        var joinedTypeParams = RootCompiler.joinTypeParams(typeParams);
        var implementingString = RootCompiler.generateImplementing(maybeImplementing);

        var newModifiers = Lists.of("export");
        var joinedModifiers = newModifiers
                .iter()
                .map((String value) -> value + " ")
                .collect(Joiner.empty())
                .orElse("");

        if (outputContentState.context().hasPlatform(Platform.PlantUML)) {
            var joinedImplementing = maybeImplementing
                    .map((Type type) -> type.generateSimple())
                    .map((String generated) -> name + " <|.. " + generated + "\n")
                    .orElse("");

            var joinedSuperTypes = maybeSuperType.iter()
                    .map((Type type) -> type.generateSimple())
                    .map((String generated) -> name + " <|-- " + generated + "\n")
                    .collect(new Joiner(""))
                    .orElse("");

            var generated = infix + name + joinedTypeParams + " {\n}\n" + joinedSuperTypes + joinedImplementing;
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(outputContentState.mapRegistry((Registry registry) -> registry.append(generated)), ""));
        }

        if (annotations.contains("Namespace")) {
            String actualInfix = "interface ";
            String newName = name + "Instance";

            var generated = joinedModifiers + actualInfix + newName + joinedTypeParams + implementingString + " {" + joinParameters(parameters) + constructorString + outputContent + "\n}\n";
            CompileState compileState = outputContentState.mapRegistry((Registry registry) -> registry.append(generated));
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(compileState.mapRegistry((Registry registry1) -> registry1.append("export declare const " + name + ": " + newName + ";\n")), ""));
        }
        else {
            var extendsString = RootCompiler.joinExtends(maybeSuperType);
            var generated = joinedModifiers + infix + name + joinedTypeParams + extendsString + implementingString + " {" + joinParameters(parameters) + constructorString + outputContent + "\n}\n";
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(outputContentState.mapRegistry((Registry registry) -> registry.append(generated)), ""));
        }
    }

    private static String joinExtends(Iterable<Type> maybeSuperType) {
        return maybeSuperType.iter()
                .map((Type type) -> TypeCompiler.generateType(type))
                .collect(new Joiner(", "))
                .map((String inner) -> " extends " + inner)
                .orElse("");
    }

    private static String generateImplementing(Option<Type> maybeImplementing) {
        return maybeImplementing.map((Type type) -> TypeCompiler.generateType(type))
                .map((String inner) -> " implements " + inner)
                .orElse("");
    }

    public static String joinTypeParams(Iterable<String> typeParams) {
        return typeParams.iter()
                .collect(new Joiner(", "))
                .map((String inner) -> "<" + inner + ">")
                .orElse("");
    }

    private static String generateConstructorFromRecordParameters(Iterable<Definition> parameters) {
        return parameters.iter()
                .map((Definition definition) -> definition.generate())
                .collect(new Joiner(", "))
                .map((String generatedParameters) -> RootCompiler.generateConstructorWithParameterString(parameters, generatedParameters))
                .orElse("");
    }

    private static String generateConstructorWithParameterString(Iterable<Definition> parameters, String parametersString) {
        var constructorAssignments = RootCompiler.generateConstructorAssignments(parameters);

        return "\n\tconstructor (" + parametersString + ") {" +
                constructorAssignments +
                "\n\t}";
    }

    private static String generateConstructorAssignments(Iterable<Definition> parameters) {
        return parameters.iter()
                .map((Definition definition) -> "\n\t\tthis." + definition.name() + " = " + definition.name() + ";")
                .collect(Joiner.empty())
                .orElse("");
    }

    private static Option<Tuple2<CompileState, String>> compileNamespaced(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(state, ""));
        }

        return new None<Tuple2<CompileState, String>>();
    }

    private static Tuple2<CompileState, String> compileClassSegment(CompileState state1, String input1) {
        return OrRule.compileOrPlaceholder(state1, input1, Lists.of(
                WhitespaceCompiler::compileWhitespace,
                RootCompiler.createStructureRule("class ", "class "),
                RootCompiler.createStructureRule("interface ", "interface "),
                RootCompiler.createStructureRule("record ", "class "),
                RootCompiler.createStructureRule("enum ", "class "),
                FieldCompiler::compileMethod,
                FieldCompiler::compileFieldDefinition,
                FieldCompiler::compileEnumValues
        ));
    }

    public static Tuple2<CompileState, String> compileRoot(CompileState state, String input, Location location) {
        return FunctionSegmentCompiler.compileStatements(state.mapContext((Context context2) -> context2.withLocation(location)), input, RootCompiler::compileRootSegment);
    }

    public static String joinParameters(Iterable<Definition> parameters) {
        return parameters.iter()
                .map((Definition definition) -> definition.generate())
                .map((String generated) -> "\n\t" + generated + ";")
                .collect(Joiner.empty())
                .orElse("");
    }
}