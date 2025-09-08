package magma.ir;

/** IR node representing an integer literal. */
public class IrLiteral extends IrNode {
    public final int value;

    public IrLiteral(int value) {
        this.value = value;
    }
}
