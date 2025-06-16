package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.rule.truncate.PrefixTruncator;
import magma.app.compile.rule.truncate.StripTruncator;
import magma.app.compile.rule.truncate.SuffixTruncator;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Truncator truncator;
    private final CompileResultFactory<Node> resultFactory;

    public TruncateRule(Rule<Node> rule, Truncator truncator, CompileResultFactory<Node> resultFactory) {
        this.rule = rule;
        this.truncator = truncator;
        this.resultFactory = resultFactory;
    }

    public static <Node> Rule<Node> Prefix(String prefix, Rule<Node> rule, CompileResultFactory<Node> factory) {
        return new TruncateRule<>(rule, new PrefixTruncator(prefix), factory);
    }

    public static <Node> Rule<Node> Suffix(Rule<Node> rule, String suffix, CompileResultFactory<Node> factory) {
        return new TruncateRule<>(rule, new SuffixTruncator(suffix), factory);
    }

    public static <Node> Rule<Node> createStripRule(Rule<Node> rule, CompileResultFactory<Node> factory) {
        return new TruncateRule<>(rule, new StripTruncator(), factory);
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.truncator.truncate(input)
                .map(this.rule::lex)
                .orElseGet(() -> this.resultFactory.fromStringError(this.truncator.createErrorMessage(), input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(this.truncator::generate);
    }
}