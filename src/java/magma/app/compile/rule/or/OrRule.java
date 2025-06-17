package magma.app.compile.rule.or;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

import java.util.List;

public final class OrRule implements Rule<NodeResult, StringResult> {
    private final List<Rule<NodeResult, StringResult>> rules;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public OrRule(List<Rule<NodeResult, StringResult>> rules, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.rules = rules;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.rules.stream()
                .<OrState<Node, FormattedError>>reduce(new MutableOrState<>(),
                        (state, rule) -> switch (rule.lex(input)) {
                            case NodeOk(var value) -> state.withValue(value);
                            case NodeErr(var error) -> state.withError(error);
                        },
                        (_, next) -> next)
                .maybeValue()
                .map(this.factory::fromNode)
                .orElseGet(() -> this.factory.fromStringErr("No combination present", input));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rules.stream()
                .<OrState<String, FormattedError>>reduce(new MutableOrState<>(),
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
