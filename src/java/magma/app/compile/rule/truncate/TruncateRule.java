package magma.app.compile.rule.truncate;

import magma.app.compile.rule.Rule;

import java.util.Optional;

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
    public Optional<Node> lex(String input) {
        return this.truncator.truncate(input)
                .flatMap(this.rule::lex);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(this.truncator::generate);
    }
}