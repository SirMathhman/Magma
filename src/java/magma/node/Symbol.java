package magma.node;

import java.util.Optional;

public record Symbol(String value) implements Value, JavaType {
    @Override
    public String generate() {
        return this.value;
    }

    @Override
    public CType toCType() {
        return new Struct(this.value);
    }

    @Override
    public Optional<String> findBaseName() {
        return Optional.of(this.value);
    }
}
