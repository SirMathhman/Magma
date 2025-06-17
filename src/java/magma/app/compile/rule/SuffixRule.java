package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.Node;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return new Err<>(new CompileError("Suffix '" + this.suffix + "' not present", new StringContext(input)));

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule()
                .lex(withoutEnd);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> result + this.suffix);
    }
}