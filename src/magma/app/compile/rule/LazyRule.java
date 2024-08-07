package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.Optional;

public class LazyRule implements Rule {
    private Optional<Rule> current = Optional.empty();

    public void set(Rule rule) {
        this.current = Optional.of(rule);
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return current.map(inner -> inner.parse(input))
                .orElseGet(() -> new RuleResult<>(Err.Err(new ParseError("No child set", input))));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return current.map(inner -> inner.generate(node))
                .orElseGet(() -> new RuleResult<>(Err.Err((new GenerateError("No child set", node)))));
    }
}
