package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.StripModifier;
import magma.app.compile.rule.modify.SuffixModifier;

import java.util.Optional;

public final class ModifyRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Modifier modifier;

    public ModifyRule(Rule<Node> rule, Modifier modifier) {
        this.modifier = modifier;
        this.rule = rule;
    }

    public static <Node> Rule<Node> Prefix(String prefix, Rule<Node> rule) {
        return new ModifyRule<>(rule, new PrefixModifier(prefix));
    }

    public static <Node> Rule<Node> Strip(Rule<Node> rule) {
        return new ModifyRule<>(rule, new StripModifier());
    }

    public static <Node> Rule<Node> Suffix(Rule<Node> rule, String suffix) {
        return new ModifyRule<>(rule, new SuffixModifier(suffix));
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(this.modifier::complete);
    }

    private Optional<Node> lex0(String input) {
        return this.modifier.truncate(input)
                .flatMap(input1 -> (this.rule).lex(input1)
                        .findValue());
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return CompileResults.fromOption(this.lex0(input), input);
    }
}