package magma.codegen;

import magma.ast.*;
import magma.types.TypeMapper;
import magma.types.TypeResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CCodeGenerator implements Visitor<String> {
	private final Set<String> includes = new HashSet<>();
	private final TypeMapper typeMapper;
	private int indentLevel = 0;
	private static final String INDENT = "    ";

	public CCodeGenerator() {
		this.typeMapper = new TypeMapper();
	}

	public String generate(Program program) {
		// Initialize type resolver with type definitions
		TypeResolver typeResolver = new TypeResolver(program.getTypeDefinitions());
		typeMapper.setTypeResolver(typeResolver);

		// Add standard type includes
		includes.add("#include <stdint.h>");
		includes.add("#include <stddef.h>");

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
		
		// Add includes (sorted for consistency)
		List<String> sortedIncludes = includes.stream().sorted().collect(Collectors.toList());
		for (String include : sortedIncludes) {
			code.append(include).append("\n");
		}
		if (!includes.isEmpty()) {
			code.append("\n");
		}

		// Generate extern function declarations (emit whitespace only)
		for (ExternFunctionDeclaration externFn : program.getExternFunctions()) {
			code.append(externFn.accept(this));
		}

		// Generate function definitions (before main)
		for (FunctionDefinition fn : program.getFunctions()) {
			code.append(fn.accept(this)).append("\n\n");
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
		String type;
		String arraySuffix = "";
		if (node.hasTypeAnnotation()) {
			Type typeAnnotation = node.getTypeAnnotation();
			if (typeAnnotation instanceof ArrayType) {
				ArrayType arrayType = (ArrayType) typeAnnotation;
				String elementType = typeMapper.mapToCType(arrayType.getElementType(), this::generateExpression);
				if (arrayType.hasStart()) {
					// Dynamic array - use pointer
					type = elementType + "*";
				} else {
					// Fixed-size array - type is element type, brackets go after name
					type = elementType;
					arraySuffix = "[" + generateExpression(arrayType.getLength()) + "]";
				}
			} else {
				type = typeMapper.mapToCType(typeAnnotation, this::generateExpression);
			}
		} else {
			// Type inference: default to int for now
			type = "int";
		}
		String name = node.getName();
		String initializer = node.getInitializer().accept(this);
		return type + " " + name + arraySuffix + " = " + initializer + ";";
	}

	private String generateExpression(Node node) {
		return node.accept(this);
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

	@Override
	public String visitNamedType(NamedType node) {
		return typeMapper.mapToCType(node, this::generateExpression);
	}

	@Override
	public String visitPointerType(PointerType node) {
		return typeMapper.mapToCType(node, this::generateExpression);
	}

	@Override
	public String visitArrayType(ArrayType node) {
		return typeMapper.mapToCType(node, this::generateExpression);
	}

	@Override
	public String visitGenericType(GenericType node) {
		return typeMapper.mapToCType(node, this::generateExpression);
	}

	@Override
	public String visitSizeOfExpression(SizeOfExpression node) {
		String cType = typeMapper.mapToCType(node.getType(), this::generateExpression);
		return "sizeof(" + cType + ")";
	}

	@Override
	public String visitFunctionDefinition(FunctionDefinition node) {
		StringBuilder code = new StringBuilder();
		
		// Generate return type
		String returnType;
		if (node.hasReturnType()) {
			returnType = typeMapper.mapToCType(node.getReturnType(), this::generateExpression);
		} else {
			returnType = "void";
		}
		
		// Generate function signature
		code.append(returnType).append(" ").append(node.getName()).append("(");
		
		// Generate parameters
		List<String> params = node.getParameters().stream()
				.map(this::visitFunctionParameter)
				.collect(Collectors.toList());
		code.append(String.join(", ", params));
		
		code.append(") {\n");
		
		// Generate function body (always a Block)
		indentLevel++;
		Block body = (Block) node.getBody();
		for (Statement stmt : body.getStatements()) {
			code.append(indent()).append(stmt.accept(this)).append("\n");
		}
		indentLevel--;
		code.append("}");
		
		return code.toString();
	}

	@Override
	public String visitExternFunctionDeclaration(ExternFunctionDeclaration node) {
		// Emit whitespace only as requested
		return " ";
	}

	@Override
	public String visitReturnStatement(ReturnStatement node) {
		if (node.hasValue()) {
			return "return " + node.getValue().accept(this) + ";";
		} else {
			return "return;";
		}
	}

	@Override
	public String visitTypeDefinition(TypeDefinition node) {
		// Type definitions are compile-time aliases, they don't generate C code
		return "";
	}

	private String visitFunctionParameter(FunctionParameter param) {
		String type;
		if (param.hasType()) {
			type = typeMapper.mapToCType(param.getType(), this::generateExpression);
		} else {
			// Default to int if no type annotation
			type = "int";
		}
		return type + " " + param.getName();
	}

	private String indent() {
		return INDENT.repeat(indentLevel);
	}
}
