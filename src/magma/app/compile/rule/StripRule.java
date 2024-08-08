package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class StripRule implements Rule {
    private final Rule child;
    private final String before;
    private final String after;

    public StripRule(Rule child, String before, String after) {
        this.child = child;
        this.before = before;
        this.after = after;
    }

    public StripRule(Rule child) {
        this(child, "", "");
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return child.parse(input.strip())
                .wrapErr(() -> new ParseError("Cannot strip", input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return child.generate(node).wrapValue(value -> {
            var beforeSlice = node.findString(before).orElse("");
            var afterSlice = node.findString(after).orElse("");
            return beforeSlice + value + afterSlice;
        });
    }
}
