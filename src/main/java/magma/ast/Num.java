package magma.ast;

public class Num implements ArrayElem {
	public int value;
	public String suffix;

	public Num(int v, String s) {
		value = v;
		suffix = s;
	}
}
