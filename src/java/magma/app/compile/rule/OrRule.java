package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.Rule;
import magma.app.compile.error.Context;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.rule.or.OrState;
import magma.app.compile.rule.or.SimpleOrState;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.RuleResult.RuleResultErr;
import magma.app.compile.rule.result.RuleResult.RuleResultOk;

import java.util.List;
import java.util.function.Function;

public record OrRule<Node extends DisplayableNode>(
        List<Rule<Node, RuleResult<Node>, RuleResult<String>>> rules) implements Rule<Node, RuleResult<Node>, RuleResult<String>> {
    @Override
    public RuleResult<Node> lex(String input) {
        return this.or(rule1 -> rule1.lex(input), new StringContext(input));
    }

    private <Value> RuleResult<Value> or(Function<Rule<Node, RuleResult<Node>, RuleResult<String>>, RuleResult<Value>> mapper, Context context) {
        final var result = this.getResult(mapper, context);
        return switch (result) {
            case Err<Value, CompileError> v -> new RuleResultErr<>(v.error());
            case Ok<Value, CompileError> v -> new RuleResultOk<>(v.value());
        };
    }

    private <Value> Result<Value, CompileError> getResult(Function<Rule<Node, RuleResult<Node>, RuleResult<String>>, RuleResult<Value>> mapper, Context context) {
        return this.rules.stream().map(mapper).<OrState<Value>>reduce(new SimpleOrState<>(), (state, result) -> switch (result) {
            case RuleResultErr<Value>(var error) -> state.withError(error);
            case RuleResultOk<Value>(Value value) -> state.withValue(value);
        }, (_, next) -> next).toResult(context);
    }

    @Override
    public RuleResult<String> generate(Node node) {
        return this.or(rule -> rule.generate(node), new NodeContext(node));
    }
}
