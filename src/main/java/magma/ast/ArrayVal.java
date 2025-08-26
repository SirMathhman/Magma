package magma.ast;

public class ArrayVal implements Stored {
  public Stored[] items; // either Num or BoolVal
  public String elemSuffix; // numeric suffix for numeric arrays, empty otherwise

  public ArrayVal(Stored[] items, String suffix) {
    this.items = items;
    this.elemSuffix = suffix;
  }
}
