package magma.ast;

public class ArrayVal implements Expression {
  public Expression[] items; // either Num or BoolVal

  public ArrayVal(Expression[] items) {
    this.items = items;
  }
}
