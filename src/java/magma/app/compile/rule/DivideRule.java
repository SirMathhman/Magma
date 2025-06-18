package magma.app.compile.rule;

import magma.api.list.Lists;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.divide.Divider;

import java.util.Objects;
import java.util.Optional;

public final class DivideRule implements Rule<NodeWithEverything> {
    private final String key;
    private final Rule<NodeWithEverything> rule;
    private final NodeFactory<NodeWithEverything> nodeFactory;

    public DivideRule(String key, Rule<NodeWithEverything> rule, NodeFactory<NodeWithEverything> nodeFactory) {
        this.key = key;
        this.rule = rule;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        final var segments = Divider.divide(input);
        var oldChildren = Lists.<NodeWithEverything>empty();
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
    public Optional<String> generate(NodeWithEverything root) {
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

    public String key() {
        return this.key;
    }

    public Rule<NodeWithEverything> rule() {
        return this.rule;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (DivideRule) obj;
        return Objects.equals(this.key, that.key) && Objects.equals(this.rule, that.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.rule);
    }

    @Override
    public String toString() {
        return "DivideRule[" + "key=" + this.key + ", " + "rule=" + this.rule + ']';
    }

}