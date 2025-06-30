package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record SuffixRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule, String suffix) implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private Optional<EverythingNode> lex0(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix)) return Optional.empty();
        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule().lex(substring).toOptional();
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rule.generate(node).toOptional().map(result -> this.suffix + result);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(
                () -> new NodeErr<EverythingNode>(new CompileError()));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(() -> new StringErr(new CompileError()));
    }
}