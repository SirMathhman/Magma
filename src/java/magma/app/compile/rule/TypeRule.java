package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.node.attribute.NodeWithType;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public final class TypeRule<Node extends NodeWithType<Node>> implements Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> {
    private final String type;
    private final Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule;

    public TypeRule(String type, Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule) {
        this.type = type;
        this.rule = rule;
    }

    private Optional<String> generate0(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node)
                    .findValue();

        return Optional.empty();
    }

    private Optional<Node> lex0(String input) {
        return (this.rule).lex(input)
                .findValue()
                .map(node -> node.retype(this.type));
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}
