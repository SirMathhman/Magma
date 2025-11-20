package magma.codegen;

import magma.ast.BinaryExpression;
import magma.ast.Node;
import magma.ast.NumberLiteral;
import magma.ast.Visitor;

public class CCodeGenerator implements Visitor<String> {
	public String generate(Node node) {
		return node.accept(this);
	}

	@Override
	public String visitNumberLiteral(NumberLiteral node) {
		return String.valueOf(node.getValue());
	}

	@Override
	public String visitBinaryExpression(BinaryExpression node) {
		String left = node.getLeft().accept(this);
		String operator = node.getOperator().lexeme();
		String right = node.getRight().accept(this);
		return "(" + left + " " + operator + " " + right + ")";
	}
}
