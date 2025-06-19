package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.StringContext;
import magma.app.compile.node.attribute.MergingNode;
import magma.app.compile.rule.locate.FirstLocator;
import magma.app.compile.rule.locate.LastLocator;
import magma.app.compile.rule.locate.Locator;

import java.util.Optional;

public final class LocateRule<Node extends MergingNode<Node>> implements Rule<Node> {
    private final Rule<Node> leftRule;
    private final String infix;
    private final Rule<Node> rightRule;
    private final Locator locator;

    public LocateRule(Rule<Node> leftRule, String infix, Rule<Node> rightRule, Locator locator) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.locator = locator;
    }

    public static <Node extends MergingNode<Node>> Rule<Node> Last(Rule<Node> leftRule, String infix, Rule<Node> rightRule) {
        return new LocateRule<>(leftRule, infix, rightRule, new LastLocator());
    }

    public static <Node extends MergingNode<Node>> Rule<Node> First(Rule<Node> leftRule, String infix, Rule<Node> rightRule) {
        return new LocateRule<>(leftRule, infix, rightRule, new FirstLocator());
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optional.of(this.leftRule.generate(node)
                .orElse("") + this.infix + this.rightRule.generate(node)
                .orElse(""));
    }

    private Optional<Node> lex0(String input) {
        final var maybeIndex = this.locator.locate(input, this.infix);
        if (maybeIndex.isEmpty())
            return Optional.empty();

        final int index = maybeIndex.get();
        final var leftSlice = input.substring(0, index);
        final var rightSlice = input.substring(index + this.infix.length());
        return (this.leftRule).lex(leftSlice)
                .findValue()
                .flatMap(leftResult -> (this.rightRule).lex(rightSlice)
                        .findValue()
                        .map(leftResult::merge));
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return this.lex0(input)
                .<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid input", new StringContext(input))));
    }
}