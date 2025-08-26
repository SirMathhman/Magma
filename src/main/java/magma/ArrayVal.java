package magma;

class ArrayVal {
  Object[] items; // either Num or Boolean
  String elemSuffix; // numeric suffix when numeric, empty otherwise
  boolean isBool;

  ArrayVal(Object[] items, String suffix, boolean isBool) {
    this.items = items;
    this.elemSuffix = suffix;
    this.isBool = isBool;
  }
}
