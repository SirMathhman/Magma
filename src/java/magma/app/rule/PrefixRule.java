package magma.app.rule;

import magma.CompileError;
import magma.StringContext;
import magma.api.Err;
import magma.api.Result;
import magma.app.node.Node;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        if (!input.startsWith(this.prefix))
            return new Err<>(new CompileError("Prefix '" + this.prefix + "' not present", new StringContext(input)));

        final var slice = input.substring(this.prefix.length());
        return this.rule()
                .lex(slice);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> this.prefix + result);
    }
}