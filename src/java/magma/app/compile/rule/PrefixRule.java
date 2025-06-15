package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.node.NodeResults;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return this.rule.generate(node)
                .prependString(this.prefix);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        if (!input.startsWith(this.prefix))
            return NodeResults.createFromString("Prefix '" + this.prefix + "' not present", input);

        final var substring = input.substring(this.prefix.length());
        return this.rule.lex(substring);
    }
}