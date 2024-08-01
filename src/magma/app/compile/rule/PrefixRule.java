package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record PrefixRule(String prefix, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        if (!input.startsWith(prefix())) return Optional.empty();
        var truncatedRight = input.substring(prefix().length());
        return this.child().parse(truncatedRight).result().findValue();
    }

    private Optional<String> generate0(Node node) {
        return child.generate(node).result().findValue().map(inner -> prefix + inner);
    }

    private Result<Node, ParseException> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    private Result<String, GenerateException> generate1(Node node) {
        return generate0(node)
                .<Result<String, GenerateException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new GenerateException("Invalid node", node)));
    }

    @Override
    public RuleResult<Node, ParseException> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateException> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}