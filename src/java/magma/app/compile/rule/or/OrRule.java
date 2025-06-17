package magma.app.compile.rule.or;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.rule.Rule;

import java.util.List;

public final class OrRule<Node extends DisplayNode> implements Rule<Node, Result<Node, FormattedError>, StringResult> {
    private final List<Rule<Node, Result<Node, FormattedError>, StringResult>> rules;
    private final ResultFactory<Node, Result<Node, FormattedError>, StringResult> factory;

    public OrRule(List<Rule<Node, Result<Node, FormattedError>, StringResult>> rules, ResultFactory<Node, Result<Node, FormattedError>, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.rules.stream()
                .<OrState<Node, FormattedError, Result<Node, FormattedError>>>reduce(new MutableOrState<>(),
                        (state, rule) -> switch ((Result<Node, FormattedError>) rule.lex(input)) {
                            case Ok(Node value) -> state.withValue(value);
                            case Err(FormattedError error) -> state.withError(error);
                        },
                        (_, next) -> next)
                .maybeValue()
                .map(this.factory::fromNode)
                .orElseGet(() -> this.factory.fromStringErr("No combination present", input));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rules.stream()
                .<OrState<String, FormattedError, Result<String, FormattedError>>>reduce(new MutableOrState<>(),
                        (state, rule) -> switch (rule.generate(node)) {
                            case StringOk(String value) -> state.withValue(value);
                            case StringErr(var error) -> state.withError(error);
                        },
                        (_, next) -> next)
                .maybeValue()
                .map(this.factory::fromString)
                .orElseGet(() -> this.factory.fromNodeErr("No combination present", node));
    }
}
