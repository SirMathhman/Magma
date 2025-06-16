package magma.app.compile.rule.divide;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.list.NodeListResult;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;

public final class NodeListRule<Node extends NodeWithNodeLists<Node> & DisplayableNode> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory;

    public NodeListRule(String key, Rule<Node> rule, CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory) {
        this.key = key;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(String input) {
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