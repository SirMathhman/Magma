package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.app.compile.CompileState;
import magma.app.compile.Context;
import magma.app.compile.namespace.NamespacedRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.structure.StructureCompiler;
import magma.app.compile.value.Placeholder;

public final class RootCompiler {
    private static Rule<String> createRootSegmentRule() {
        return new OrRule<String>(Lists.of(
                WhitespaceCompiler.createWhitespaceRule(),
                new NamespacedRule(),
                StructureCompiler.createStructureRule("class ", "class "),
                StructureCompiler.createStructureRule("interface ", "interface "),
                StructureCompiler.createStructureRule("record ", "class "),
                StructureCompiler.createStructureRule("enum ", "class ")
        ));
    }

    public static Tuple2<CompileState, String> compileClassSegment(CompileState state1, String input1) {
        return OrRule.compileOrPlaceholder(state1, input1, Lists.of(
                WhitespaceCompiler.createWhitespaceRule(),
                StructureCompiler.createStructureRule("class ", "class "),
                StructureCompiler.createStructureRule("interface ", "interface "),
                StructureCompiler.createStructureRule("record ", "class "),
                StructureCompiler.createStructureRule("enum ", "class "),
                FieldCompiler.createMethodRule(),
                FieldCompiler.createFieldDefinitionRule(),
                FieldCompiler.createEnumValuesRule()
        ));
    }

    static Tuple2<CompileState, String> compileRoot(CompileState state, String input, Location location) {
        return FunctionSegmentCompiler.compileStatements(state.mapContext((Context context2) -> context2.withLocation(location)), input,
                (CompileState state1, String input1) -> RootCompiler.createRootSegmentRule().apply(state1, input1).orElseGet(() -> new Tuple2Impl<CompileState, String>(state1, Placeholder.fromValue(input1))));
    }
}