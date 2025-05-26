package magma.app.compile.rule;

import magma.api.option.Option;
import magma.app.compile.node.Node;

public record TypeRule(String type, Rule childRule) implements Rule {
    @Override
    public Option<Node> lex(String input) {
        return childRule.lex(input).map(result -> result.retype(type));
    }
}
