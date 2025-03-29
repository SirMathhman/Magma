package magma.compile.rule;

import magma.compile.CompileError;
import magma.compile.Node;
import magma.result.Result;

public record StripRule(Rule childRule) implements Rule {
    @Override
    public Result<Node, CompileError> parse(String input) {
        return childRule().parse(input.strip());
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return childRule.generate(node);
    }
}