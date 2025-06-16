package magma.app.compile;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.NodeResults;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.Map;

public class RuleCompiler implements Compiler {
    private final Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> targetRule;
    private final Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> sourceRule;

    public RuleCompiler(Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> sourceRule, Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
    }

    private NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>> transform(NodeWithNodeLists<NodeWithEverything> tree, String name) {
        final var list = tree.nodeLists()
                .find("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(segment -> segment.strings()
                        .with("parent", name))
                .toList();

        return NodeResults.Ok(new MapNode().nodeLists()
                .with("children", list));
    }

    @Override
    public StringResult<FormattedError> compile(Map<String, String> inputs) {
        StringResult<FormattedError> currentResult = DefaultCompileResultFactory.create()
                .fromEmptyString();

        for (var input : inputs.entrySet())
            currentResult = currentResult.appendResult(() -> {
                final var name = input.getKey();
                return this.sourceRule.lex(input.getValue())
                        .transform(tree -> this.transform(tree, name))
                        .generate(tree -> this.targetRule.generate(tree)
                                .prependSlice("class " + name + "\n"));
            });

        return currentResult.prependSlice("@startuml\nskinparam linetype ortho\n")
                .appendSlice("@enduml");
    }
}
