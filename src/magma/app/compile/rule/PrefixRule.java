package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public record PrefixRule(String prefix, Rule child) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (!input.startsWith(this.prefix))
            return new RuleResult<>(new Err<>(new ParseError("Prefix '" + prefix + "' not present", input)));

        var truncatedRight = input.substring(this.prefix.length());
        return this.child.parse(truncatedRight);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node).wrapValue(inner -> prefix + inner);
    }
}