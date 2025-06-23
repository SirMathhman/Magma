package magma.app.compile.rule;

import magma.app.compile.factory.ParentNodeResultFactory;
import magma.app.compile.string.Appending;

public record SuffixRule<Node, StringResult extends Appending<StringResult>, NodeResult, Factory extends ParentNodeResultFactory<Node, NodeResult, ?>>(
        Rule<Node, NodeResult, StringResult> rule, String suffix,
        Factory factory) implements Rule<Node, NodeResult, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        if (input.endsWith(suffix))
            return rule.lex(input.substring(0, input.length() - suffix.length()));

        return factory.fromNodeError("Suffix '" + suffix + "' not present", input);
    }

    @Override
    public StringResult generate(final Node node) {
        return rule.generate(node)
                .appendSlice(suffix);
    }
}
