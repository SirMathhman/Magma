package magma.app.compile.error.node;

import magma.app.compile.rule.action.CompileError;

import java.util.Optional;

public record NodeErr<Node>(CompileError compileError) implements NodeResult<Node> {
    @Override
    public Optional<Node> findValue() {
        return Optional.empty();
    }
}
