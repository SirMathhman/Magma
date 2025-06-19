package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.StripModifier;
import magma.app.compile.rule.modify.SuffixModifier;

import java.util.Optional;

public final class ModifyRule<Node> implements Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> {
    private final Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule;
    private final Modifier modifier;

    public ModifyRule(Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule, Modifier modifier) {
        this.modifier = modifier;
        this.rule = rule;
    }

    public static <Node> Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> Prefix(String prefix, Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule) {
        return new ModifyRule<>(rule, new PrefixModifier(prefix));
    }

    public static <Node> Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> Strip(Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule) {
        return new ModifyRule<>(rule, new StripModifier());
    }

    public static <Node> Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> Suffix(Rule<Node, Result<Node, CompileError>, Result<String, CompileError>> rule, String suffix) {
        return new ModifyRule<>(rule, new SuffixModifier(suffix));
    }

    private Optional<String> generate0(Node node) {
        return this.rule.generate(node)
                .findValue()
                .map(this.modifier::complete);
    }

    private Optional<Node> lex0(String input) {
        return this.modifier.truncate(input)
                .flatMap(input1 -> (this.rule).lex(input1)
                        .findValue());
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}