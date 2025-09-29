package magma.compiler.ast;

public class StringLiteral implements Expr {
	private final String value;

	public StringLiteral(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
