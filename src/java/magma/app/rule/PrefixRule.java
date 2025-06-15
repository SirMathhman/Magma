package magma.app.rule;

import magma.app.CompileError;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.node.ErrNodeResult;
import magma.app.maybe.string.Prependable;

public record PrefixRule<Node, Generate extends Prependable<Generate>>(String prefix,
                                                                       Rule<Node, NodeResult, Generate> rule) implements Rule<Node, NodeResult, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).prependString(this.prefix);
    }

    @Override
    public NodeResult lex(String input) {
        if (!input.startsWith(this.prefix))
            return new ErrNodeResult(new CompileError("Prefix '" + this.prefix + "' not present", new StringContext(input)));

        final var substring = input.substring(this.prefix.length());
        return this.rule.lex(substring);
    }
}