package magma.app.compile.rule.divide;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(input1 -> this.rule.lex(input1)
                        .findValue())
                .reduce(Optional.of(new ArrayList<>()), this::fold, (_, next) -> next)
                .map(children -> this.factory.create()
                        .nodeLists()
                        .with(this.key, children))
                .<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new StringContext(""))));
    }

    private Optional<List<Node>> fold(Optional<List<Node>> maybeCurrent, Optional<Node> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.map(element -> {
            current.add(element);
            return current;
        }));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        final var children = node.nodeLists()
                .find(this.key)
                .orElse(new ArrayList<>());

        return Optional.of(children.stream()
                        .map(node1 -> this.rule.generate(node1)
                                .findValue())
                        .flatMap(Optional::stream)
                        .collect(Collectors.joining()))
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new NodeContext(node))));
    }
}