package magma.app.compile;

import magma.api.Ok;
import magma.api.Result;

import java.util.List;

public final class OptionalRule implements Rule {
    private final String propertyKey;
    private final Rule rule;
    private final DisjunctionRule parser;

    public OptionalRule(String propertyKey, Rule rule) {
        this.propertyKey = propertyKey;
        this.rule = rule;
        parser = new DisjunctionRule(List.of(rule, EmptyRule.EMPTY));
    }

    private Result<Node, CompileException> parse1(String input) {
        return parser.parse(input).result();
    }

    private Result<String, CompileException> generate1(Node node) {
        if (node.has(propertyKey)) {
            return rule.generate(node).result().mapErr(err -> new NodeException("Property '" + propertyKey + "' was present, but could not be generated", node, err));
        }
        return new Ok<>("");
    }

    @Override
    public CompileResult<Node> parse(String input) {
        return new CompileResult<>(parse1(input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return new CompileResult<>(generate1(node));
    }
}
