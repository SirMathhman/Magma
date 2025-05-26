package magma.app.compile.rule;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public record StringRule(String key) implements Rule {
    @Override
    public Option<Node> lex(String input) {
        return new Some<>(new MapNode().withString(this.key, input));
    }
}