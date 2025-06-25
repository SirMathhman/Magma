package magma.node;

import java.util.Optional;

public class JavaStringType implements JavaType {
    @Override
    public CType toCType() {
        return new Pointer(CPrimitive.Char);
    }

    @Override
    public Optional<String> findBaseName() {
        return Optional.empty();
    }
}
