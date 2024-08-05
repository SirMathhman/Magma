package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.List;
import java.util.Objects;

public final class OptionalRule implements Rule {
    private final String propertyKey;
    private final Rule onPresent;
    private final Rule onEmpty;
    private DisjunctionRule parsingRule;

    public OptionalRule(String propertyKey, Rule onPresent, Rule onEmpty) {
        this.propertyKey = propertyKey;
        this.onPresent = onPresent;
        this.onEmpty = onEmpty;
        parsingRule = new DisjunctionRule(List.of(onPresent, onEmpty));
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return parsingRule.parse(input);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        if(node.has(propertyKey)) {
            return onPresent.generate(node);
        } else {
            return onEmpty.generate(node);
        }
    }
}
