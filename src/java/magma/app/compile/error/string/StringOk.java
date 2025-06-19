package magma.app.compile.error.string;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    @Override
    public Optional<String> findValue() {
        return Optional.of(this.value);
    }

    @Override
    public StringResult appendSlice(String suffix) {
        return new StringOk(this.value + suffix);
    }

    @Override
    public StringResult appendResult(Supplier<StringResult> other) {
        return other.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult prependSlice(String prefix) {
        return new StringOk(prefix + this.value);
    }

    @Override
    public StringResult map(Function<String, String> mapper) {
        return new StringOk(mapper.apply(this.value));
    }
}
