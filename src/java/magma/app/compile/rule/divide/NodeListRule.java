package magma.app.compile.rule.divide;

import magma.app.compile.CompileResult;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public final class NodeListRule<Node extends NodeWithNodeLists<Node> & DisplayableNode> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final NodeFactory<Node> factory;

    public NodeListRule(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        this.key = key;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return Divider.divide(input)
                .stream()
                .map(this.rule::lex)
                .reduce(CompileResult.from(new ArrayList<>()), this::foldList, (_, next) -> next)
                .mapValue(children -> this.factory.create()
                        .nodeLists()
                        .with(this.key, children));
    }

    private CompileResult<List<Node>> foldList(CompileResult<List<Node>> maybeCurrent, CompileResult<Node> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.mapValue(element -> {
            current.add(element);
            return current;
        }));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        final var children = node.nodeLists()
                .find(this.key)
                .orElse(new ArrayList<>());

        return children.stream()
                .map(this.rule::generate)
                .reduce(CompileResult.from(new StringBuilder()), this::foldString, (_, next) -> next)
                .mapValue(StringBuilder::toString);
    }

    private CompileResult<StringBuilder> foldString(CompileResult<StringBuilder> maybeCurrent, CompileResult<String> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.mapValue(current::append));
    }
}