package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.CompileResult;
import magma.app.compile.rule.truncate.PrefixTruncator;
import magma.app.compile.rule.truncate.StripTruncator;
import magma.app.compile.rule.truncate.SuffixTruncator;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Truncator truncator;

    public TruncateRule(Rule<Node> rule, Truncator truncator) {
        this.rule = rule;
        this.truncator = truncator;
    }

    public static <Node> Rule<Node> Prefix(String prefix, Rule<Node> rule) {
        return new TruncateRule<>(rule, new PrefixTruncator(prefix));
    }

    public static <Node> Rule<Node> Suffix(Rule<Node> rule, String suffix) {
        return new TruncateRule<>(rule, new SuffixTruncator(suffix));
    }

    public static <Node> Rule<Node> createStripRule(Rule<Node> rule) {
        return new TruncateRule<>(rule, new StripTruncator());
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.truncator.truncate(input)
                .map(CompileResult::from)
                .orElseGet(() -> new CompileResult<>(new Err<>(new CompileError("Invalid rule", new StringContext("")))))
                .flatMap(this.rule::lex);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(this.truncator::generate);
    }
}