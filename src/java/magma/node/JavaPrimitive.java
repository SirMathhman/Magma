package magma.node;

import java.util.Optional;

public enum JavaPrimitive implements JavaType {
    Int(CPrimitive.Int);

    private final CType value;

    JavaPrimitive(final CType value) {
        this.value = value;
    }

    @Override
    public CType toCType() {
        return this.value;
    }

    @Override
    public Optional<String> findBaseName() {
        return Optional.empty();
    }
}
