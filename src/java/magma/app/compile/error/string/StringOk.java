package magma.app.compile.error.string;

import java.util.Optional;

public record StringOk(String value) implements StringResult {
    @Override
    public Optional<String> findValue() {
        return Optional.of(this.value);
    }
}
