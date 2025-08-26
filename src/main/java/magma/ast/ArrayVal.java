package magma.ast;

public class ArrayVal implements Expression {
  public Expression[] items; // either Num or BoolVal
  public String elemSuffix; // numeric suffix for numeric arrays, empty otherwise

  public ArrayVal(Expression[] items, String suffix) {
    this.items = items;
    this.elemSuffix = suffix;
  }
}
