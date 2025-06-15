package magma.app.compile.lang;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class PlantUMLLang {
    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createPlantUMLRootRule() {
        return new DivideRule("children", createPlantUMLRootSegmentRule());
    }

    private static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createPlantUMLRootSegmentRule() {
        return new OrRule(List.of(createDependencyRule(), new EmptyRule()));
    }
}
