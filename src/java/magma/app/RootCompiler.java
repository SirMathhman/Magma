package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.Context;
import magma.app.compile.rule.OrRule;
import magma.app.compile.structure.StructureCompiler;

public final class RootCompiler {
    private static Tuple2<CompileState, String> compileRootSegment(CompileState state, String input) {
        return OrRule.compileOrPlaceholder(state, input, Lists.of(
                WhitespaceCompiler::compileWhitespace,
                RootCompiler::compileNamespaced,
                StructureCompiler.createStructureRule("class ", "class "),
                StructureCompiler.createStructureRule("interface ", "interface "),
                StructureCompiler.createStructureRule("record ", "class "),
                StructureCompiler.createStructureRule("enum ", "class ")
        ));
    }

    private static Option<Tuple2<CompileState, String>> compileNamespaced(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return new Some<Tuple2<CompileState, String>>(new Tuple2Impl<CompileState, String>(state, ""));
        }

        return new None<Tuple2<CompileState, String>>();
    }

    public static Tuple2<CompileState, String> compileClassSegment(CompileState state1, String input1) {
        return OrRule.compileOrPlaceholder(state1, input1, Lists.of(
                WhitespaceCompiler::compileWhitespace,
                StructureCompiler.createStructureRule("class ", "class "),
                StructureCompiler.createStructureRule("interface ", "interface "),
                StructureCompiler.createStructureRule("record ", "class "),
                StructureCompiler.createStructureRule("enum ", "class "),
                FieldCompiler::compileMethod,
                FieldCompiler::compileFieldDefinition,
                FieldCompiler::compileEnumValues
        ));
    }

    static Tuple2<CompileState, String> compileRoot(CompileState state, String input, Location location) {
        return FunctionSegmentCompiler.compileStatements(state.mapContext((Context context2) -> context2.withLocation(location)), input, RootCompiler::compileRootSegment);
    }
}