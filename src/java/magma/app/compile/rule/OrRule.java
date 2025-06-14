package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.error.Context;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.rule.or.OrState;
import magma.app.compile.rule.or.SimpleOrState;
import magma.app.compile.rule.result.RuleResult;

import java.util.List;
import java.util.function.Function;

public record OrRule<Node extends DisplayableNode>(
        List<Rule<Node, RuleResult<Node>, RuleResult<String>>> rules) implements Rule<Node, RuleResult<Node>, RuleResult<String>> {
    @Override
    public RuleResult<Node> lex(String input) {
        return this.or(rule1 -> rule1.lex(input), new StringContext(input));
    }

    private <Value> RuleResult<Value> or(Function<Rule<Node, RuleResult<Node>, RuleResult<String>>, RuleResult<Value>> mapper, Context context) {
        return this.rules.stream().map(mapper).<OrState<Value>>reduce(new SimpleOrState<Value>(), (state, result) -> result.match(state::withValue, state::withError), (_, next) -> next).toResult(context);
    }

    @Override
    public RuleResult<String> generate(Node node) {
        return this.or(rule -> rule.generate(node), new NodeContext(node));
    }
}
