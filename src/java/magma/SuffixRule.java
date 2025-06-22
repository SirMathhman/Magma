package magma;

import magma.node.Node;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.option.Option;
import magma.rule.Rule;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        if (input.endsWith(this.suffix))
            return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));

        return new NodeErr();
    }

    @Override
    public Option<String> generate(final Node node) {
        return this.rule.generate(node)
                .map(value -> value + this.suffix);
    }
}
