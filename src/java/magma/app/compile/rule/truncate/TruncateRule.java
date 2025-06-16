package magma.app.compile.rule.truncate;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.StringContext;
import magma.app.compile.rule.Rule;

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
    public Result<Node, CompileError> lex(String input) {
        return this.truncator.truncate(input)
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new StringContext(""))))
                .flatMap(this.rule::lex);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(this.truncator::generate);
    }
}