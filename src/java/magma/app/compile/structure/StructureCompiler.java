package magma.app.compile.structure;

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
import magma.app.DefiningCompiler;
import magma.app.FunctionSegmentCompiler;
import magma.app.Platform;
import magma.app.RootCompiler;
import magma.app.TypeCompiler;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.Registry;
import magma.app.compile.Stack;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.Split;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.define.Definition;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.rule.Rule;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.symbol.Symbols;
import magma.app.compile.type.Type;

public class StructureCompiler {
    private record StructureRule(String sourceInfix, String targetInfix) implements Rule<String> {
        public static Option<Tuple2<CompileState, String>> compileStructureWithImplementing(CompileState state, List<String> annotations, List<String> modifiers, String targetInfix, String beforeContent, String content, String sourceInfix) {
            return Split.last(" implements ", (String s, String s2) -> TypeCompiler.createTypeRule().apply(state, s2).flatMap((Tuple2<CompileState, Type> implementingTuple) -> StructureRule.compileStructureWithExtends(implementingTuple.left(), annotations, targetInfix, s, new Some<Type>(implementingTuple.right()), content, sourceInfix))).apply(beforeContent).or(() -> StructureRule.compileStructureWithExtends(state, annotations, targetInfix, beforeContent, new None<Type>(), content, sourceInfix));
        }

        public static Option<Tuple2<CompileState, String>> compileStructureWithExtends(CompileState state, List<String> annotations, String targetInfix, String beforeContent, Option<Type> maybeImplementing, String inputContent, String sourceInfix) {
            Splitter splitter = new LocatingSplitter(" extends ", new FirstLocator());
            return new Split<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String beforeExtends, String afterExtends) -> ValueCompiler.values((CompileState inner0, String inner1) -> TypeCompiler.createTypeRule().apply(inner0, inner1)).apply(state, afterExtends)
                    .flatMap((Tuple2<CompileState, List<Type>> compileStateListTuple2) -> StructureRule.compileStructureWithParameters(compileStateListTuple2.left(), annotations, targetInfix, beforeExtends, compileStateListTuple2.right(), maybeImplementing, inputContent, sourceInfix)))).apply(beforeContent).or(() -> StructureRule.compileStructureWithParameters(state, annotations, targetInfix, beforeContent, Lists.empty(), maybeImplementing, inputContent, sourceInfix));
        }

        public static Option<Tuple2<CompileState, String>> compileStructureWithParameters(CompileState state, List<String> annotations, String targetInfix, String beforeContent, Iterable<Type> maybeSuperType, Option<Type> maybeImplementing, String inputContent, String sourceInfix) {
            Splitter splitter1 = new LocatingSplitter("(", new FirstLocator());
            return new Split<Tuple2<CompileState, String>>(splitter1, Composable.toComposable((String rawName, String withParameters) -> {
                Splitter splitter = new LocatingSplitter(")", new FirstLocator());
                return new Split<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String parametersString, String _) -> {
                    var name = Strings.strip(rawName);

                    var parametersTuple = DefiningCompiler.parseParameters(state, parametersString);
                    var parameters = DefiningCompiler.retainDefinitionsFromParameters(parametersTuple.right());

                    return StructureRule.compileStructureWithTypeParams(parametersTuple.left(), targetInfix, inputContent, name, parameters, maybeImplementing, annotations, maybeSuperType, sourceInfix);
                })).apply(withParameters);
            })).apply(beforeContent).or(() -> StructureRule.compileStructureWithTypeParams(state, targetInfix, inputContent, beforeContent, Lists.empty(), maybeImplementing, annotations, maybeSuperType, sourceInfix));
        }

        public static Option<Tuple2<CompileState, String>> compileStructureWithTypeParams(CompileState state, String infix, String content, String beforeParams, Iterable<Definition> parameters, Option<Type> maybeImplementing, List<String> annotations, Iterable<Type> maybeSuperType, String sourceInfix) {
            return new SuffixComposable<Tuple2<CompileState, String>>(">", (String withoutTypeParamEnd) -> {
                Splitter splitter = new LocatingSplitter("<", new FirstLocator());
                return new Split<Tuple2<CompileState, String>>(splitter, Composable.toComposable((String name, String typeParamsString) -> {
                    var typeParams = DefiningCompiler.divideValues(typeParamsString);
                    return StructureRule.assembleStructure(state, annotations, infix, name, typeParams, parameters, maybeImplementing, content, maybeSuperType, sourceInfix);
                })).apply(withoutTypeParamEnd);
            }).apply(Strings.strip(beforeParams)).or(() -> StructureRule.assembleStructure(state, annotations, infix, beforeParams, Lists.empty(), parameters, maybeImplementing, content, maybeSuperType, sourceInfix));
        }

        public static Option<Tuple2<CompileState, String>> assembleStructure(
                CompileState state,
                List<String> annotations,
                String targetInfix,
                String rawName,
                Iterable<String> typeParams,
                Iterable<Definition> parameters,
                Option<Type> maybeImplementing,
                String content,
                Iterable<Type> maybeSuperType,
                String sourceInfix) {
            var name = Strings.strip(rawName);
            if (!Symbols.isSymbol(name)) {
                return new None<Tuple2<CompileState, String>>();
            }

            var outputContentTuple = FunctionSegmentCompiler.compileStatements(state.mapStack((Stack stack) -> stack.pushStructureName(name)), content, RootCompiler::compileClassSegment);
            var outputContentState = outputContentTuple.left().mapStack((Stack stack1) -> stack1.popStructureName());
            var outputContent = outputContentTuple.right();

            var constructorString = StructureRule.generateConstructorFromRecordParameters(parameters);
            var joinedTypeParams = Definition.joinTypeParams(typeParams);
            var implementingString = StructureRule.generateImplementing(maybeImplementing);

            var newModifiers = Lists.of("export");
            var joinedModifiers = newModifiers
                    .iter()
                    .map((String value) -> value + " ")
                    .collect(Joiner.empty())
                    .orElse("");

            if (outputContentState.findContext().hasPlatform(Platform.PlantUML)) {
                var joinedImplementing = maybeImplementing
                        .map((Type type) -> type.generateSimple())
                        .map((String generated) -> name + " <|.. " + generated + "\n")
                        .orElse("");

                var joinedSuperTypes = maybeSuperType.iter()
                        .map((Type type) -> type.generateSimple())
                        .map((String generated) -> name + " <|-- " + generated + "\n")
                        .collect(new Joiner(""))
                        .orElse("");

                var generated = targetInfix + name + joinedTypeParams + " {\n}\n" + joinedSuperTypes + joinedImplementing;
                return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(outputContentState.mapRegistry((Registry registry) -> registry.append(generated)), ""));
            }

            if (annotations.contains("Namespace")) {
                String actualInfix = "interface ";
                String newName = name + "Instance";

                var generated = joinedModifiers + actualInfix + newName + joinedTypeParams + implementingString + " {" + StructureRule.joinParameters(parameters) + constructorString + outputContent + "\n}\n";
                var compileState = outputContentState.mapRegistry((Registry registry) -> registry.append(generated));
                return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(compileState.mapRegistry((Registry registry1) -> registry1.append("export declare const " + name + ": " + newName + ";\n")), ""));
            }

            var maybeValuesMethod = StructureRule.generateValuesMethod(sourceInfix, outputContentState, name);

            var extendsString = StructureRule.joinExtends(maybeSuperType);
            var generated = joinedModifiers + targetInfix + name + joinedTypeParams + extendsString + implementingString + " {" + StructureRule.joinParameters(parameters) + constructorString + outputContent + maybeValuesMethod + "\n}\n";
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(outputContentState.mapRegistry((Registry registry) -> registry.append(generated)), ""));
        }

        public static String generateValuesMethod(String sourceInfix, CompileState outputContentState, String name) {
            if (!"enum ".equals(sourceInfix)) {
                return "";
            }

            var joined = StructureRule.mergeEnumValues(outputContentState, name);
            return "\n\tstatic values(): " + name + "[] {\n\t\treturn [" + joined + "];\n\t}";
        }

        private static String mergeEnumValues(CompileState outputContentState, String name) {
            return outputContentState.findStack()
                    .findLastDefinitions()
                    .iter()
                    .map((Definition definition) -> name + "." + definition.name())
                    .collect(new Joiner(", "))
                    .orElse("");
        }

        public static String joinExtends(Iterable<Type> maybeSuperType) {
            return maybeSuperType.iter()
                    .map((Type type) -> TypeCompiler.generateType(type))
                    .collect(new Joiner(", "))
                    .map((String inner) -> " extends " + inner)
                    .orElse("");
        }

        public static String generateImplementing(Option<Type> maybeImplementing) {
            return maybeImplementing.map((Type type) -> TypeCompiler.generateType(type))
                    .map((String inner) -> " implements " + inner)
                    .orElse("");
        }

        public static String generateConstructorFromRecordParameters(Iterable<Definition> parameters) {
            return parameters.iter()
                    .map((Definition definition) -> definition.generate())
                    .collect(new Joiner(", "))
                    .map((String generatedParameters) -> StructureRule.generateConstructorWithParameterString(parameters, generatedParameters))
                    .orElse("");
        }

        private static String generateConstructorWithParameterString(Iterable<Definition> parameters, String parametersString) {
            var constructorAssignments = StructureRule.generateConstructorAssignments(parameters);

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

        public static String joinParameters(Iterable<Definition> parameters) {
            return parameters.iter()
                    .map((Definition definition) -> definition.generate())
                    .map((String generated) -> "\n\t" + generated + ";")
                    .collect(Joiner.empty())
                    .orElse("");
        }

        @Override
        public Option<Tuple2<CompileState, String>> apply(CompileState state, String input1) {
            return new Split<Tuple2<CompileState, String>>(new LocatingSplitter(this.sourceInfix, new FirstLocator()), Composable.toComposable((String beforeInfix, String afterInfix) -> new Split<Tuple2<CompileState, String>>(new LocatingSplitter("{", new FirstLocator()), Composable.toComposable((String beforeContent, String withEnd) -> new SuffixComposable<Tuple2<CompileState, String>>("}", (String inputContent) -> Split.last("\n", (String s, String s2) -> {
                var annotations = DefiningCompiler.parseAnnotations(s);
                if (annotations.contains("Actual")) {
                    return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(state, ""));
                }

                return StructureRule.compileStructureWithImplementing(state, annotations, DefiningCompiler.parseModifiers(s2), this.targetInfix, beforeContent, inputContent, this.sourceInfix);
            }).apply(beforeInfix).or(() -> {
                var modifiers = DefiningCompiler.parseModifiers(beforeContent);
                return StructureRule.compileStructureWithImplementing(state, Lists.empty(), modifiers, this.targetInfix, beforeContent, inputContent, this.sourceInfix);
            })).apply(Strings.strip(withEnd)))).apply(afterInfix))).apply(input1);
        }
    }

    public static Rule<String> createStructureRule(String sourceInfix, String targetInfix) {
        return new StructureRule(sourceInfix, targetInfix);
    }
}