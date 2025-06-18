package magma.app.compile.rule;

import magma.api.list.Lists;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.divide.Divider;

import java.util.Optional;

public record DivideRule(String key, Rule<NodeWithEverything> rule) implements Rule<NodeWithEverything> {
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
        return Optional.of(new MapNodeFactory().create()
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
}