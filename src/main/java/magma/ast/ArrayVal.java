package magma.ast;

public class ArrayVal {
  public ArrayElem[] items; // either Num or BoolVal
  public String elemSuffix; // numeric suffix for numeric arrays, empty otherwise

  public ArrayVal(ArrayElem[] items, String suffix) {
    this.items = items;
    this.elemSuffix = suffix;
  }
}
