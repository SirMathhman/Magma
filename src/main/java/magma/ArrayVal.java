package magma;

class ArrayVal {
  Object[] items; // either Num or Boolean
  String elemSuffix; // numeric suffix for numeric arrays, empty otherwise
  boolean isBool;

  ArrayVal(Object[] items, String suffix, boolean isBool) {
    this.items = items;
    this.elemSuffix = suffix;
    this.isBool = isBool;
  }
}
