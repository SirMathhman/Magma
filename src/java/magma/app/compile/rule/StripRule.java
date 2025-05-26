package magma.app.compile.rule;

import magma.api.option.Option;
import magma.api.text.Strings;
import magma.app.compile.node.Node;

public record StripRule(Rule<Node> childRule) implements Rule<Node> {
    @Override
    public Option<Node> lex(String input) {
        return this.childRule.lex(Strings.strip(input));
    }
}