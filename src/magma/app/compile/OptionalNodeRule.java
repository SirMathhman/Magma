package magma.app.compile;

import magma.api.Ok;
import magma.api.Result;

import java.util.List;

public final class OptionalNodeRule implements Rule {
    private final String propertyKey;
    private final Rule rule;
    private final DisjunctionRule parser;

    public OptionalNodeRule(String propertyKey, Rule rule) {
        this.propertyKey = propertyKey;
        this.rule = rule;
        parser = new DisjunctionRule(List.of(rule, EmptyRule.EMPTY));
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parser.parse(input);
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        if (node.hasNode(propertyKey)) {
            return rule.generate(node).mapErr(err -> new NodeException("Property '" + propertyKey + "' was present, but could not be generated", node, err));
        }
        return new Ok<>("");
    }
}
