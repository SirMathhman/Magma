package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class PrefixRule implements Rule {
    private final String prefix;
    private final Rule child;

    public PrefixRule(String prefix, Rule child) {
        this.prefix = prefix;
        this.child = child;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (!input.startsWith(this.prefix))
            return RuleResult.RuleResult(Err.Err(new ParseError("Prefix '" + prefix + "' not present", input)));

        var truncatedRight = input.substring(this.prefix.length());
        return this.child.parse(truncatedRight);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node).wrapValue(inner -> prefix + inner);
    }
}