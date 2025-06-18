package magma.app.compile.rule;

import magma.app.compile.locate.FirstLocator;
import magma.app.compile.locate.LastLocator;
import magma.app.compile.locate.Locator;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public final class LocateRule implements Rule {
    private final Rule leftRule;
    private final String infix;
    private final Rule rightRule;
    private final Locator locator;

    public LocateRule(Rule leftRule, String infix, Rule rightRule, Locator locator) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.locator = locator;
    }

    public static Rule Last(Rule leftRule, String infix, Rule rightRule) {
        return new LocateRule(leftRule, infix, rightRule, new LastLocator());
    }

    public static Rule First(Rule leftRule, String infix, Rule rightRule) {
        return new LocateRule(leftRule, infix, rightRule, new FirstLocator());
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return Optional.of(this.leftRule.generate(node)
                .orElse("") + this.infix + this.rightRule.generate(node)
                .orElse(""));
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        final var maybeIndex = this.locator.locate(input, this.infix);
        if (maybeIndex.isEmpty())
            return Optional.empty();

        final int index = maybeIndex.get();
        final var leftSlice = input.substring(0, index);
        final var rightSlice = input.substring(index + this.infix.length());
        return this.leftRule.lex(leftSlice)
                .flatMap(leftResult -> this.rightRule.lex(rightSlice)
                        .map(leftResult::merge));
    }
}