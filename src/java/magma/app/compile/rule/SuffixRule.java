package magma.app.compile.rule;

import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;

public final class SuffixRule<Node, Error, NodeResult> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> rule;
    private final String suffix;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public SuffixRule(Rule<Node, NodeResult, StringResult> rule, String suffix, ResultFactory<Node, NodeResult, StringResult> factory) {
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
        StringResult stringErrorResult = this.rule.generate(node);
        return switch (stringErrorResult) {
            case StringErr(var error) -> new StringErr(error);
            case StringOk(
                    String value
            ) -> new StringOk(value + this.suffix);
        };
    }
}