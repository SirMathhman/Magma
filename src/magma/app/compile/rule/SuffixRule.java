package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (!input.endsWith(this.suffix())) return RuleResult.RuleResult(Err.Err(new ParseError("Suffix not present '" + suffix + "'", input)));
        var name = input.substring(0, input.length() - this.suffix().length());
        return this.child().parse(name);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node).wrapValue(inner -> inner + suffix);
    }
}