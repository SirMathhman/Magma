package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Rule;
import magma.app.compile.NodeResult;
import magma.app.compile.node.NodeResults;
import magma.app.compile.string.Prepend;

public record PrefixRule<Node, Generate extends Prepend<Generate>>(String prefix,
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