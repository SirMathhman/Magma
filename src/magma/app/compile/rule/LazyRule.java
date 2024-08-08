package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.Optional;
import java.util.Stack;

import static magma.api.Err.Err;
import static magma.app.compile.rule.RuleResult.RuleResult;

public class LazyRule implements Rule {
    private final Stack<String> inputs = new Stack<>();
    private Optional<Rule> current = Optional.empty();

    public void set(Rule rule) {
        this.current = Optional.of(rule);
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return current
                .map(inner -> parseAvoidingCycles(input, inner))
                .orElseGet(() -> RuleResult(Err(new ParseError("No child set", input))));
    }

    private RuleResult<Node, ParseError> parseAvoidingCycles(String input, Rule inner) {
        if (inputs.contains(input)) {
            return RuleResult(Err(new ParseError("Infinite loop.", input)));
        }

        inputs.push(input);
        var parsed = inner.parse(input);
        inputs.pop();
        return parsed;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return current.map(inner -> inner.generate(node))
                .orElseGet(() -> RuleResult(Err((new GenerateError("No child set", node)))));
    }
}
