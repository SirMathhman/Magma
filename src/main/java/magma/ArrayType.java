package magma;

// A simple type descriptor for parsed array types like [I32; 3] or [[I32;2];2]
class ArrayType {
	public final String baseSuffix; // non-null when element is numeric type
	public final ArrayType inner; // non-null when element is an array type
	public final int len;

	public ArrayType(String baseSuffix, ArrayType inner, int len) {
		this.baseSuffix = baseSuffix;
		this.inner = inner;
		this.len = len;
	}
}
