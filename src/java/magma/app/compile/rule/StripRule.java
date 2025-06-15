package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;

public record StripRule(Rule rule) implements Rule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return this.rule.generate(node);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        return this.rule.lex(input.strip());
    }
}