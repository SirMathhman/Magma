package magma.app.rule;

import magma.CompileError;
import magma.api.Result;
import magma.app.node.Node;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String segment) {
        return this.rule.lex(segment.strip());
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.rule.generate(node);
    }
}