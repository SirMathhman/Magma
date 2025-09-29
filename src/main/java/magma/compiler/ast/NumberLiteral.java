package magma.compiler.ast;

public class NumberLiteral implements Expr {
	private final double value;

	public NumberLiteral(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
