package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class SuffixRule implements Rule {
    private final Rule child;
    private final String suffix;

    public SuffixRule(Rule child, String suffix) {
        this.child = child;
        this.suffix = suffix;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (!input.endsWith(suffix))
            return RuleResult.RuleResult(Err.Err(new ParseError("Suffix not present '" + suffix + "'", input)));
        var name = input.substring(0, input.length() - suffix.length());
        return child.parse(name);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node).wrapValue(inner -> inner + suffix);
    }
}