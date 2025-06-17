package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;

public record OrRule(List<Rule> rules) implements Rule {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.or(rule1 -> rule1.lex(input), new StringContext(input));
    }

    private <Value> Result<Value, FormattedError> or(Function<Rule, Result<Value, FormattedError>> mapper, Context context) {
        return this.rules.stream()
                .<OrState<Value>>reduce(new MutableOrState<Value>(),
                        (state, rule) -> mapper.apply(rule)
                                .match(state::withValue, state::withError),
                        (_, next) -> next)
                .toResult(context);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.or(rule1 -> rule1.generate(node), new NodeContext(node));
    }
}
