package magma.app.compile.error.string;

import magma.app.compile.rule.action.CompileError;

import java.util.Optional;

public record StringErr(CompileError error) implements StringResult {
    @Override
    public Optional<String> findValue() {
        return Optional.empty();
    }
}
