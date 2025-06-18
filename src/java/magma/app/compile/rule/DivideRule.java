package magma.app.compile.rule;

import magma.api.list.Lists;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.divide.Divider;

import java.util.Optional;

public final class DivideRule<Node extends NodeWithNodeLists<Node>> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final NodeFactory<Node> nodeFactory;

    public DivideRule(String key, Rule<Node> rule, NodeFactory<Node> nodeFactory) {
        this.key = key;
        this.rule = rule;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Optional<Node> lex(String input) {
        final var segments = Divider.divide(input);
        var oldChildren = Lists.<Node>empty();
        for (var i = 0; i < segments.size(); i++) {
            final var segment = segments.get(i);
            oldChildren = this.rule.lex(segment)
                    .map(oldChildren::add)
                    .orElse(oldChildren);
        }
        return Optional.of(this.nodeFactory.create()
                .withNodeList(this.key, oldChildren));
    }

    @Override
    public Optional<String> generate(Node root) {
        final var children = root.findNodeList(this.key)
                .orElse(Lists.empty());

        var output = new StringBuilder();
        for (var i = 0; i < children.size(); i++) {
            final var newChild = children.get(i);
            this.rule.generate(newChild)
                    .map(output::append);
        }

        return Optional.of(output.toString());
    }
}