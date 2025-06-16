package magma.app.compile;

import magma.app.compile.error.CompileResult;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.Map;

public class CompilerImpl implements Compiler {
    private final Rule<NodeWithEverything> targetRule;
    private final Rule<NodeWithEverything> sourceRule;

    public CompilerImpl(Rule<NodeWithEverything> sourceRule, Rule<NodeWithEverything> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
    }

    private CompileResult<String> parseAndGenerate(NodeWithNodeLists<NodeWithEverything> tree, String name) {
        final var children1 = this.transform(tree, name);
        return this.targetRule.generate(children1)
                .mapValue(joined -> this.generate(name, joined));
    }

    private String generate(String name, String joined) {
        return "class " + name + "\n" + joined;
    }

    private NodeWithEverything transform(NodeWithNodeLists<NodeWithEverything> tree, String name) {
        final var list = tree.nodeLists()
                .find("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(segment -> segment.strings()
                        .with("parent", name))
                .toList();

        return new MapNode().nodeLists()
                .with("children", list);
    }

    @Override
    public CompileResult<String> compile(Map<String, String> inputs) {
        CompileResult<StringBuilder> buffer = CompileResult.from(new StringBuilder());
        for (var input : inputs.entrySet())
            buffer = buffer.flatMap(inner -> {
                final var result = this.sourceRule.lex(input.getValue())
                        .flatMap(tree -> this.parseAndGenerate(tree, input.getKey()));

                return result.mapValue(inner::append);
            });

        return buffer.mapValue(inner -> "@startuml\nskinparam linetype ortho\n" + inner + "@enduml");
    }
}
