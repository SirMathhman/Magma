package magma.app.compile.rule.or;

import magma.api.result.Matchable;
import magma.app.compile.context.Context;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.result.ResultCreator;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;

public final class OrRule<Node extends DisplayNode, Error, NodeResult extends Matchable<Node, Error>, StringResult extends Matchable<String, Error>> implements Rule<Node, NodeResult, StringResult> {
    private final List<Rule<Node, NodeResult, StringResult>> rules;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public OrRule(List<Rule<Node, NodeResult, StringResult>> rules, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.or(this.factory.createNodeCreator(), rule -> rule.lex(input), new StringContext(input));
    }

    private <Value, ValueResult extends Matchable<Value, Error>> ValueResult or(ResultCreator<Value, ValueResult> creator, Function<Rule<Node, NodeResult, StringResult>, ValueResult> mapper, Context context) {
        return this.rules.stream()
                .<OrState<Value, Error, ValueResult>>reduce(new MutableOrState<>(creator),
                        (state, rule) -> mapper.apply(rule)
                                .match(state::withValue, state::withError),
                        (_, next) -> next)
                .toResult(context);
    }

    @Override
    public StringResult generate(Node node) {
        return this.or(this.factory.createStringCreator(), rule -> rule.generate(node), new NodeContext(node));
    }
}
