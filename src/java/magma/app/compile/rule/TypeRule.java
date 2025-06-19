package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.attribute.NodeWithType;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public final class TypeRule<Node extends NodeWithType<Node>> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult<Node>, StringResult> rule;

    public TypeRule(String type, Rule<Node, NodeResult<Node>, StringResult> rule) {
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
    public NodeResult<Node> lex(String input) {
        Optional<Node> option = this.lex0(input);
        return option.map(CompileResults::fromNodeValue)
                .orElseGet(() -> CompileResults.fromNodeError(input, "Invalid value"));
    }

    @Override
    public StringResult generate(Node node) {
        Optional<String> option = this.generate0(node);
        return option.map(CompileResults::fromStringValue)
                .orElseGet(() -> CompileResults.fromStringError(node));
    }
}
