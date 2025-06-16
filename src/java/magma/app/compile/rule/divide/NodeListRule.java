package magma.app.compile.rule.divide;

import magma.app.compile.error.Appendable;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;

public final class NodeListRule<Node extends NodeWithNodeLists<Node> & DisplayableNode, Error, StringResult extends Appendable<StringResult>> implements Rule<Node, NodeResult<Node, Error>, StringResult> {
    private final String key;
    private final Rule<Node, NodeResult<Node, Error>, StringResult> rule;
    private final CompileResultFactory<Node, Error, StringResult, NodeResult<Node, Error>, NodeListResult<Node, Error, NodeResult<Node, Error>>> resultFactory;

    public NodeListRule(String key, Rule<Node, NodeResult<Node, Error>, StringResult> rule, CompileResultFactory<Node, Error, StringResult, NodeResult<Node, Error>, NodeListResult<Node, Error, NodeResult<Node, Error>>> resultFactory) {
        this.key = key;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error> lex(String input) {
        return Divider.divide(input)
                .stream()
                .reduce(this.resultFactory.fromEmptyNodeList(), (maybeCurrent, maybeElement) -> maybeCurrent.add(() -> this.rule.lex(maybeElement)), (_, next) -> next)
                .toNode(this.key);
    }

    @Override
    public StringResult generate(Node node) {
        final var children = node.nodeLists()
                .find(this.key)
                .orElse(new ArrayList<>());

        return children.stream()
                .reduce(this.resultFactory.fromEmptyString(), (maybeCurrent, maybeElement) -> maybeCurrent.appendResult(() -> this.rule.generate(maybeElement)), (_, next) -> next);
    }
}