package magma.ir;

/** IR node representing a binary addition of two integer literals. */
public class IrBinary extends IrNode {
    public final IrLiteral left;
    public final IrLiteral right;

    public IrBinary(IrLiteral left, IrLiteral right) {
        this.left = left;
        this.right = right;
    }
}
