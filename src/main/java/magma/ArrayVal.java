package magma;

class ArrayVal {
  ArrayElem[] items; // either Num or BoolVal
  String elemSuffix; // numeric suffix for numeric arrays, empty otherwise

  ArrayVal(ArrayElem[] items, String suffix) {
    this.items = items;
    this.elemSuffix = suffix;
  }
}
