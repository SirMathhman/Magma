package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.List;
import java.util.Optional;

public final class OptionalNodeRule implements Rule {
    private final String propertyKey;
    private final Rule rule;
    private final DisjunctionRule parser;

    public OptionalNodeRule(String propertyKey, Rule rule) {
        this.propertyKey = propertyKey;
        this.rule = rule;
        parser = new DisjunctionRule(List.of(rule, EmptyRule.EMPTY));
    }

    private Optional<Node> parse0(String input) {
        return parser.parse(input).findValue();
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        if (!node.hasNode(propertyKey)) return new Ok<>("");
        return rule.generate(node).mapErr(err -> new NodeException("Property '" + propertyKey + "' was present, but could not be generated", node, err));
    }
}
