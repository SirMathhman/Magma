package magma.app.compile.rule;

import magma.app.compile.rule.modify.Modifier;

import java.util.Optional;

public final class ModifyRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Modifier modifier;

    public ModifyRule(Rule<Node> rule, Modifier modifier) {
        this.modifier = modifier;
        this.rule = rule;
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(this.modifier::complete);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.modifier.truncate(input)
                .flatMap(this.rule::lex);
    }
}