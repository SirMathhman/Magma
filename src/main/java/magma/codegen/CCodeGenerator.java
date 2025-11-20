package magma.codegen;

import magma.ast.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CCodeGenerator implements Visitor<String> {
	private final Set<String> includes = new HashSet<>();
	private int indentLevel = 0;
	private static final String INDENT = "    ";

	public String generate(Program program) {
		// Generate includes from imports
		for (ImportStatement imp : program.getImports()) {
			if (imp.isExtern()) {
				String module = imp.getModule();
				if (module.equals("stdio")) {
					includes.add("#include <stdio.h>");
				} else if (module.equals("stdlib")) {
					includes.add("#include <stdlib.h>");
				} else {
					includes.add("#include <" + module + ".h>");
				}
			}
		}

		// Generate code
		StringBuilder code = new StringBuilder();
		
		// Add includes
		for (String include : includes) {
			code.append(include).append("\n");
		}
		if (!includes.isEmpty()) {
			code.append("\n");
		}

		// Generate main function
		code.append("int main(void) {\n");
		indentLevel++;
		
		// Generate statements
		for (Statement stmt : program.getStatements()) {
			code.append(indent()).append(stmt.accept(this)).append("\n");
		}
		
		indentLevel--;
		code.append("}\n");

		return code.toString();
	}

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

	@Override
	public String visitIdentifier(Identifier node) {
		return node.getName();
	}

	@Override
	public String visitStringLiteral(StringLiteral node) {
		// Escape the string for C
		String value = node.getValue()
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\t", "\\t")
				.replace("\r", "\\r");
		return "\"" + value + "\"";
	}

	@Override
	public String visitVariableDeclaration(VariableDeclaration node) {
		// For now, assume int type for all variables
		// In the future, we'd need type inference or type annotations
		String type = "int";
		String name = node.getName();
		String initializer = node.getInitializer().accept(this);
		return type + " " + name + " = " + initializer + ";";
	}

	@Override
	public String visitAssignment(Assignment node) {
		String target = node.getTarget().accept(this);
		String value = node.getValue().accept(this);
		return target + " = " + value + ";";
	}

	@Override
	public String visitFunctionCall(FunctionCall node) {
		String name = node.getName();
		List<String> args = node.getArguments().stream()
				.map(arg -> arg.accept(this))
				.collect(Collectors.toList());
		return name + "(" + String.join(", ", args) + ")";
	}

	@Override
	public String visitArrayIndex(ArrayIndex node) {
		String array = node.getArray().accept(this);
		String index = node.getIndex().accept(this);
		return array + "[" + index + "]";
	}

	@Override
	public String visitForLoop(ForLoop node) {
		StringBuilder code = new StringBuilder();
		String varName = node.getVariableName();
		String start = node.getStart().accept(this);
		String end = node.getEnd().accept(this);
		
		code.append("for (int ").append(varName).append(" = ").append(start)
			.append("; ").append(varName).append(" < ").append(end)
			.append("; ").append(varName).append("++) {\n");
		
		indentLevel++;
		for (Statement stmt : node.getBody().getStatements()) {
			code.append(indent()).append(stmt.accept(this)).append("\n");
		}
		indentLevel--;
		code.append(indent()).append("}");
		
		return code.toString();
	}

	@Override
	public String visitBlock(Block node) {
		StringBuilder code = new StringBuilder();
		code.append("{\n");
		indentLevel++;
		for (Statement stmt : node.getStatements()) {
			code.append(indent()).append(stmt.accept(this)).append("\n");
		}
		indentLevel--;
		code.append(indent()).append("}");
		return code.toString();
	}

	@Override
	public String visitProgram(Program node) {
		return generate(node);
	}

	@Override
	public String visitImportStatement(ImportStatement node) {
		// Imports are handled in generate(Program)
		return "";
	}

	@Override
	public String visitExpressionStatement(ExpressionStatement node) {
		return node.getExpression().accept(this) + ";";
	}

	private String indent() {
		return INDENT.repeat(indentLevel);
	}
}
