package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.node.attribute.NodeWithType;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public final class TypeRule<Node extends NodeWithType<Node>> implements Rule<Node> {
    private final String type;
    private final Rule<Node> rule;

    public TypeRule(String type, Rule<Node> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public Optional<String> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);

        return Optional.empty();
    }

    private Optional<Node> lex0(String input) {
        return (this.rule).lex(input)
                .findValue()
                .map(node -> node.retype(this.type));
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return CompileResults.fromOption(this.lex0(input), input);
    }
}
