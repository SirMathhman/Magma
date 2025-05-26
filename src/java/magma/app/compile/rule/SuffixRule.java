package magma.app.compile.rule;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.compile.node.Node;

public record SuffixRule(String suffix, Rule rule) implements Rule {
    @Override
    public Option<Node> lex(String input) {
        if (input.endsWith(this.suffix)) {
            return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));
        }
        else {
            return new None<>();
        }
    }
}
