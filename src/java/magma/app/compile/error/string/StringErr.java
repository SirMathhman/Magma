package magma.app.compile.error.string;

import magma.app.compile.rule.action.CompileError;

import java.util.Optional;
import java.util.function.Supplier;

public record StringErr(CompileError error) implements StringResult {
    @Override
    public Optional<String> findValue() {
        return Optional.empty();
    }

    @Override
    public StringResult appendSlice(String suffix) {
        return this;
    }

    @Override
    public StringResult appendResult(Supplier<StringResult> other) {
        return this;
    }

    @Override
    public StringResult prependSlice(String prefix) {
        return this;
    }
}
