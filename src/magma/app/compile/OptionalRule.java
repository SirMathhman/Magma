package magma.app.compile;

import magma.api.Ok;

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

    @Override
    public CompileResult<Node> parse(String input) {
        return parser.parse(input);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        if (!node.has(propertyKey)) return new CompileResult<>(new Ok<>(""));
        return rule.generate(node).wrapErr(() -> new NodeException("Property '" + propertyKey + "' was present, but could not be generated", node));
    }
}
