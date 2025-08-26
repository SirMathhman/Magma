package magma.ast;

public class Num implements Stored {
	public int value;
	public String suffix;

	public Num(int v, String s) {
		value = v;
		suffix = s;
	}
}
