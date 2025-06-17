package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;

public record StripRule<Node>(
        Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    @Override
    public Result<Node, FormattedError> lex(String segment) {
        return this.rule.lex(segment.strip());
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.rule.generate(node);
    }
}