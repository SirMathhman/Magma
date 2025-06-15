package magma.app.compile;

import magma.app.compile.lang.Lang;
import magma.app.compile.node.MapNode;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;

import java.util.List;

public class Compiler {
    public static StringResult<CompileError> compileRoot(String input, String name) {
        return new DivideRule("children", createJavaRootSegmentRule()).lex(input)
                .findNodeList("children")
                .transform(children -> transform(name, children))
                .generate(children1 -> new DivideRule("children", createPlantUMLRootSegmentRule()).generate(new MapNode().withNodeList("children", children1)));
    }

    private static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createJavaRootSegmentRule() {
        return new OrRule(List.of(Lang.createImportRule(), new StringRule("value")));
    }

    private static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createPlantUMLRootSegmentRule() {
        return new OrRule(List.of(Lang.createDependencyRule(), new EmptyRule()));
    }

    public static List<Node> transform(String name, List<Node> list) {
        return list.stream()
                .map(node -> node.withString("source", name))
                .toList();
    }
}