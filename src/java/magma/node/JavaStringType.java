package magma.node;

public class JavaStringType implements JavaType {
    @Override
    public CType toCType() {
        return new Pointer(CPrimitive.Char);
    }
}
