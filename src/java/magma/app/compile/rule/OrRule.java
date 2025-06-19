package magma.app.compile.rule;

import magma.api.collect.list.Lists;
import magma.api.collect.seq.Sequence;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public record OrRule<Node>(
        Sequence<Rule<Node, NodeResult<Node>, StringResult>> rules) implements Rule<Node, NodeResult<Node>, StringResult> {

    private Optional<Node> lex0(String input) {
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var maybeLex = rule.lex(input)
                    .findValue();
            if (maybeLex.isPresent())
                return maybeLex;
        }

        return Optional.empty();
    }

    @Override
    public NodeResult<Node> lex(String input) {
        Optional<Node> option = this.lex0(input);
        return option.map(CompileResults::fromNodeValue)
                .orElseGet(() -> CompileResults.fromNodeError(input, "Invalid value"));
    }

    @Override
    public StringResult generate(Node node) {
        var errors = Lists.<CompileError>empty();
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var result = rule.generate(node);
            switch (result) {
                case StringErr(var error) -> {
                    errors = errors.add(error);
                }
                case StringResult value -> {
                    return value;
                }
            }
        }

        return CompileResults.fromStringErrorWithChildren("No valid rule", node, errors);
    }
}
