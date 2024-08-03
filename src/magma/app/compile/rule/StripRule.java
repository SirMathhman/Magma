package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record StripRule(Rule child) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return child.parse(input.strip())
                .wrapErr(() -> new ParseError("Cannot strip", input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node);
    }
}
