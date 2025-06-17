package magma;

import magma.api.Err;
import magma.api.Result;
import magma.app.node.Node;
import magma.app.rule.Rule;

public record TypeRule(String type, Rule rule) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        return this.rule.lex(input)
                .mapValue(result -> result.retype(this.type));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return new Err<>(new CompileError("Not of type '" + this.type + "'", new NodeContext(node)));
    }
}
