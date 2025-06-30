package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record PrefixRule(String prefix, Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private Optional<String> generate0(final EverythingNode node) {
        return this.rule.generate(node).toOptional().map(result -> this.prefix + result);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        if (!input.startsWith(this.prefix)) return NodeErr.create("Prefix '" + this.prefix + "' not present", input);
        final var prefixLength = this.prefix.length();

        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node)
                   .<StringResult>map(StringOk::new)
                   .orElseGet(() -> new StringErr(new CompileError(this.getClass().getName(), "?")));
    }
}