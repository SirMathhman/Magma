package magma.ast;

public class BoolVal implements Expression {
  public boolean value;

  public BoolVal(boolean v) {
    this.value = v;
  }
}
