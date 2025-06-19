package magma.app.compile.rule;

import magma.api.collect.list.Lists;
import magma.api.collect.seq.Sequence;
import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

public record OrRule<Node>(
        Sequence<Rule<Node, NodeResult<Node>, StringResult>> rules) implements Rule<Node, NodeResult<Node>, StringResult> {

    @Override
    public NodeResult<Node> lex(String input) {
        var errors = Lists.<CompileError>empty();
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            switch (rule.lex(input)) {
                case NodeErr<Node>(var error) -> {
                    errors = errors.add(error);
                }
                case NodeResult<Node> result -> {
                    return result;
                }
            }
        }

        return CompileResults.fromNodeErrorWithChildren("No valid rule", input, errors);
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
