package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.result.ResultCreator;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.function.Function;

public final class OrRule<Node extends DisplayNode> implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final List<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>> rules;
    private final ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory;

    public OrRule(List<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>> rules, ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.or(this.factory.createNodeCreator(), rule1 -> rule1.lex(input), new StringContext(input));
    }

    private <Value> Result<Value, FormattedError> or(ResultCreator<Value, Result<Value, FormattedError>> factory, Function<Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>>, Result<Value, FormattedError>> mapper, Context context) {
        return this.rules.stream()
                .<OrState<Value, FormattedError, Result<Value, FormattedError>>>reduce(new MutableOrState<>(factory),
                        (state, rule) -> mapper.apply(rule)
                                .match(state::withValue, state::withError),
                        (_, next) -> next)
                .toResult(context);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.or(this.factory.createStringCreator(), rule1 -> rule1.generate(node), new NodeContext(node));
    }
}
