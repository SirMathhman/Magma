package magma.app.compile.rule.divide;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
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
    public Result<Node, CompileError> lex(String input) {
        return Divider.divide(input)
                .stream()
                .map(this.rule::lex)
                .reduce(new Ok<>(new ArrayList<>()), this::foldList, (_, next) -> next)
                .mapValue(children -> this.factory.create()
                        .nodeLists()
                        .with(this.key, children));
    }

    private Result<List<Node>, CompileError> foldList(Result<List<Node>, CompileError> maybeCurrent, Result<Node, CompileError> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.mapValue(element -> {
            current.add(element);
            return current;
        }));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        final var children = node.nodeLists()
                .find(this.key)
                .orElse(new ArrayList<>());

        return children.stream()
                .map(this.rule::generate)
                .reduce(new Ok<>(new StringBuilder()), this::foldString, (_, next) -> next)
                .mapValue(StringBuilder::toString);
    }

    private Result<StringBuilder, CompileError> foldString(Result<StringBuilder, CompileError> maybeCurrent, Result<String, CompileError> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.mapValue(current::append));
    }
}