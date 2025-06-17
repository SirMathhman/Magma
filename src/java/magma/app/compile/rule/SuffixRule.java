package magma.app.compile.rule;

import magma.app.compile.error.AppendableStringResult;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;

public final class SuffixRule<Node, NodeResult, StringResult extends AppendableStringResult<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> rule;
    private final String suffix;
    private final ResultFactory<Node, FormattedError, NodeResult, StringResult> factory;

    public SuffixRule(Rule<Node, NodeResult, StringResult> rule, String suffix, ResultFactory<Node, FormattedError, NodeResult, StringResult> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        if (!input.endsWith(this.suffix))
            return this.factory.fromStringErr("Suffix '" + this.suffix + "' not present", input);

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutEnd);
    }

    @Override
    public StringResult generate(Node node) {
        return this.rule.generate(node)
                .appendSlice(this.suffix);
    }
}