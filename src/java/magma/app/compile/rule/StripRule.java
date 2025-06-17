package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.node.Node;

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