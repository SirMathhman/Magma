package magma.app.rule;

import magma.app.CompileError;
import magma.app.Rule;
import magma.app.NodeResult;
import magma.app.node.NodeResults;
import magma.app.string.Prependable;

public record PrefixRule<Node, Generate extends Prependable<Generate>>(String prefix,
                                                                       Rule<Node, NodeResult<Node, CompileError>, Generate> rule) implements Rule<Node, NodeResult<Node, CompileError>, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).prependString(this.prefix);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        if (!input.startsWith(this.prefix))
            return NodeResults.createFromString("Prefix '" + this.prefix + "' not present", input);

        final var substring = input.substring(this.prefix.length());
        return this.rule.lex(substring);
    }
}