package magma.codegen;

import magma.ast.*;
import magma.types.TypeMapper;
import magma.types.TypeResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CCodeGenerator implements Visitor<String> {
	private final Set<String> includes = new HashSet<>();
	private final TypeMapper typeMapper;
	private final Set<String> generatedUnions = new HashSet<>();
	private final List<String> unionDefinitions = new ArrayList<>();
	private List<TraitDefinition> traits = new ArrayList<>();
	private List<TraitImplementation> traitImplementations = new ArrayList<>();
	private List<FunctionDefinition> functions = new ArrayList<>();
	private java.util.Map<String, Type> variableTypes = new java.util.HashMap<>();
	private int indentLevel = 0;
	private static final String INDENT = "    ";

	public CCodeGenerator() {
		this.typeMapper = new TypeMapper();
	}

	public String generate(Program program) {
		// Initialize type resolver with type definitions
		TypeResolver typeResolver = new TypeResolver(program.getTypeDefinitions());
		typeMapper.setTypeResolver(typeResolver);

		// Store traits, implementations, and functions for use in code generation
		this.traits = program.getTraits();
		this.traitImplementations = program.getTraitImplementations();
		this.functions = program.getFunctions();
		
		// Reset variable type tracking for this program
		this.variableTypes.clear();

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

		// Collect union types from function signatures and variable declarations
		collectUnionTypes(program);

		// Generate union type definitions (before trait definitions)
		for (String unionDef : unionDefinitions) {
			code.append(unionDef).append("\n\n");
		}

		// Generate vtable structs for traits (before trait implementations)
		for (TraitDefinition trait : program.getTraits()) {
			code.append(generateVTableStruct(trait)).append("\n\n");
		}

		// Generate trait object structs (after vtable structs, before implementations)
		for (TraitDefinition trait : program.getTraits()) {
			code.append(generateTraitObjectStruct(trait)).append("\n\n");
		}

		// Generate trait implementation methods as C functions (before wrappers)
		for (TraitImplementation impl : program.getTraitImplementations()) {
			for (FunctionDefinition method : impl.getMethods()) {
				code.append(generateTraitMethodFunction(impl, method)).append("\n\n");
			}
		}

		// Generate trait implementations (vtable instances and wrapper functions)
		for (TraitImplementation impl : program.getTraitImplementations()) {
			code.append(generateTraitImplementation(impl)).append("\n\n");
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
			
			// Check if this is a trait type - if so, generate trait object
			if (isTraitType(typeAnnotation)) {
				return generateTraitObjectDeclaration(node);
			}
			
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
		
		// Track variable type for method call dispatch
		if (node.hasTypeAnnotation()) {
			variableTypes.put(name, node.getTypeAnnotation());
		}
		
		return type + " " + name + arraySuffix + " = " + initializer + ";";
	}

	private String generateTraitObjectDeclaration(VariableDeclaration node) {
		// Generate trait object creation: TraitName_Object var = { .box = (void*)&value, .vtable = &TraitName_Type_VTable };
		NamedType traitType = (NamedType) node.getTypeAnnotation();
		String traitName = traitType.getName();
		String objectTypeName = getTraitObjectTypeName(traitName);
		String varName = node.getName();
		String initializerExpr = node.getInitializer().accept(this);
		
		// Find the implementation for the initializer's type
		// For now, we'll try to infer the type from the initializer expression
		Type implementingType = inferTypeFromExpression(node.getInitializer());
		TraitImplementation impl = null;
		
		if (implementingType != null) {
			impl = findTraitImplementation(traitName, implementingType);
		}
		
		// If we can't find the implementation, try all implementations (simplified)
		if (impl == null) {
			// Use the first implementation we find (in a real compiler, this would be an error)
			for (TraitImplementation candidate : traitImplementations) {
				if (candidate.getTraitName().equals(traitName)) {
					impl = candidate;
					break;
				}
			}
		}
		
		if (impl == null) {
			// No implementation found - this is an error, but for now generate a placeholder
			return objectTypeName + " " + varName + " = { .box = (void*)&(" + initializerExpr + "), .vtable = NULL };";
		}
		
		String vtableName = getVTableInstanceName(traitName, impl.getImplementingType());
		String implementingTypeName = getTypeName(impl.getImplementingType());
		
		// Generate: TraitName_Object var = { .box = (void*)&value, .vtable = &TraitName_Type_VTable };
		// Track that this variable is a trait object
		variableTypes.put(varName, traitType);
		return objectTypeName + " " + varName + " = { .box = (void*)&(" + initializerExpr + "), .vtable = &" + vtableName + " };";
	}

	private Type inferTypeFromExpression(Node expression) {
		// Simplified type inference - try to determine the type of an expression
		if (expression instanceof Identifier) {
			// Look up variable type from our tracking map
			Identifier id = (Identifier) expression;
			return variableTypes.get(id.getName());
		} else if (expression instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) expression;
			// Try to find the function definition and get its return type
			for (FunctionDefinition fn : functions) {
				if (fn.getName().equals(call.getName())) {
					if (fn.hasReturnType()) {
						return fn.getReturnType();
					}
				}
			}
			// Also check trait implementation methods
			for (TraitImplementation impl : traitImplementations) {
				for (FunctionDefinition method : impl.getMethods()) {
					if (method.getName().equals(call.getName())) {
						if (method.hasReturnType()) {
							return method.getReturnType();
						}
					}
				}
			}
			return null;
		}
		
		return null;
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
		List<Node> argNodes = node.getArguments();
		
		// Check if this is a trait method call
		TraitDefinition trait = findTraitWithMethod(name);
		if (trait != null && !argNodes.isEmpty()) {
			// This might be a trait method call
			Node receiver = argNodes.get(0);
			
			// Check if receiver is a trait object
			if (receiver instanceof Identifier) {
				Identifier receiverId = (Identifier) receiver;
				Type receiverType = variableTypes.get(receiverId.getName());
				
				if (receiverType != null && isTraitType(receiverType)) {
					// Trait object - use vtable dispatch
					return generateTraitMethodVTableCall(trait, name, node);
				}
			}
			
			// Try direct call - find implementation for receiver type
			Type receiverType = inferTypeFromExpression(receiver);
			if (receiverType != null) {
				TraitImplementation impl = findTraitImplementation(trait.getName(), receiverType);
				if (impl != null) {
					// Direct call
					return generateTraitMethodDirectCall(impl, name, node);
				}
			}
		}
		
		// Regular function call
		List<String> args = argNodes.stream()
				.map(arg -> arg.accept(this))
				.collect(Collectors.toList());
		return name + "(" + String.join(", ", args) + ")";
	}

	private TraitDefinition findTraitWithMethod(String methodName) {
		// Find a trait that has a method with this name
		for (TraitDefinition trait : traits) {
			for (FunctionDefinition method : trait.getMethods()) {
				if (method.getName().equals(methodName)) {
					return trait;
				}
			}
		}
		return null;
	}

	private String generateTraitMethodVTableCall(TraitDefinition trait, String methodName, FunctionCall node) {
		// Generate: obj->vtable->methodName(obj->box, ...)
		if (node.getArguments().isEmpty()) {
			return methodName + "()"; // Should not happen
		}
		
		Node receiver = node.getArguments().get(0);
		String receiverExpr = receiver.accept(this);
		
		StringBuilder code = new StringBuilder();
		code.append(receiverExpr).append("->vtable->").append(methodName).append("(").append(receiverExpr).append("->box");
		
		// Add remaining arguments
		for (int i = 1; i < node.getArguments().size(); i++) {
			code.append(", ").append(node.getArguments().get(i).accept(this));
		}
		code.append(")");
		
		return code.toString();
	}

	private String generateTraitMethodDirectCall(TraitImplementation impl, String methodName, FunctionCall node) {
		// Generate: TypeName_methodName(&value, ...)
		String implementingTypeName = getTypeName(impl.getImplementingType());
		String functionName = implementingTypeName + "_" + methodName;
		
		StringBuilder code = new StringBuilder();
		code.append(functionName).append("(");
		
		// Add all arguments
		List<String> args = node.getArguments().stream()
				.map(arg -> arg.accept(this))
				.collect(Collectors.toList());
		code.append(String.join(", ", args));
		code.append(")");
		
		return code.toString();
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

	@Override
	public String visitIfExpression(IfExpression node) {
		// Generate ternary operator: condition ? thenExpr : elseExpr
		String condition = node.getCondition().accept(this);
		String thenExpr = node.getThenExpr().accept(this);
		String elseExpr = node.getElseExpr().accept(this);
		return "(" + condition + ") ? (" + thenExpr + ") : (" + elseExpr + ")";
	}

	@Override
	public String visitIfStatement(IfStatement node) {
		StringBuilder code = new StringBuilder();
		String condition = node.getCondition().accept(this);
		
		code.append("if (").append(condition).append(") {\n");
		indentLevel++;
		
		// Generate then statement
		String thenCode = node.getThenStmt().accept(this);
		// If the statement already ends with a semicolon, don't add another
		if (!thenCode.endsWith(";")) {
			thenCode += ";";
		}
		code.append(indent()).append(thenCode).append("\n");
		
		indentLevel--;
		code.append(indent()).append("}");
		
		// Generate else clause if present
		if (node.hasElse()) {
			code.append(" else {\n");
			indentLevel++;
			
			String elseCode = node.getElseStmt().accept(this);
			if (!elseCode.endsWith(";")) {
				elseCode += ";";
			}
			code.append(indent()).append(elseCode).append("\n");
			
			indentLevel--;
			code.append(indent()).append("}");
		}
		
		return code.toString();
	}

	@Override
	public String visitUnionType(UnionType node) {
		// Check for special case: PointerType | 0 simplifies to pointer type
		if (node.getVariants().size() == 2) {
			Type variant1 = node.getVariants().get(0);
			Type variant2 = node.getVariants().get(1);
			
			if (variant1 instanceof PointerType && isZeroType(variant2)) {
				return typeMapper.mapToCType(variant1, this::generateExpression);
			} else if (variant2 instanceof PointerType && isZeroType(variant1)) {
				return typeMapper.mapToCType(variant2, this::generateExpression);
			}
		}
		
		// Generate union type name
		String unionName = typeMapper.mapToCType(node, this::generateExpression);
		
		// Generate union definition if not already generated
		if (!generatedUnions.contains(unionName)) {
			generatedUnions.add(unionName);
			String unionDef = generateUnionDefinition(node, unionName);
			unionDefinitions.add(unionDef);
		}
		
		return unionName;
	}

	private boolean isZeroType(Type type) {
		if (type instanceof NamedType) {
			return "0".equals(((NamedType) type).getName());
		}
		return false;
	}

	private String generateUnionDefinition(UnionType node, String unionName) {
		StringBuilder code = new StringBuilder();
		
		// Generate enum for tags
		code.append("typedef enum {\n");
		for (int i = 0; i < node.getVariants().size(); i++) {
			code.append("    ").append(unionName).append("_TAG_VARIANT").append(i);
			if (i < node.getVariants().size() - 1) {
				code.append(",");
			}
			code.append("\n");
		}
		code.append("} ").append(unionName).append("_Tag;\n\n");
		
		// Generate union for data
		code.append("typedef union {\n");
		for (int i = 0; i < node.getVariants().size(); i++) {
			Type variant = node.getVariants().get(i);
			String variantType = typeMapper.mapToCType(variant, this::generateExpression);
			code.append("    ").append(variantType).append(" variant").append(i).append(";\n");
		}
		code.append("} ").append(unionName).append("_Data;\n\n");
		
		// Generate struct combining tag and data
		code.append("typedef struct {\n");
		code.append("    ").append(unionName).append("_Tag tag;\n");
		code.append("    ").append(unionName).append("_Data data;\n");
		code.append("} ").append(unionName).append(";");
		
		return code.toString();
	}

	private void collectUnionTypes(Program program) {
		// Collect union types from function return types and parameters
		for (FunctionDefinition fn : program.getFunctions()) {
			if (fn.hasReturnType() && fn.getReturnType() instanceof UnionType) {
				visitUnionType((UnionType) fn.getReturnType());
			}
			for (FunctionParameter param : fn.getParameters()) {
				if (param.hasType() && param.getType() instanceof UnionType) {
					visitUnionType((UnionType) param.getType());
				}
			}
		}
		
		// Collect union types from variable declarations
		for (Statement stmt : program.getStatements()) {
			if (stmt instanceof VariableDeclaration) {
				VariableDeclaration decl = (VariableDeclaration) stmt;
				if (decl.hasTypeAnnotation() && decl.getTypeAnnotation() instanceof UnionType) {
					visitUnionType((UnionType) decl.getTypeAnnotation());
				}
			}
		}
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

	@Override
	public String visitTraitDefinition(TraitDefinition node) {
		// Trait definitions are handled in generate() method
		return "";
	}

	@Override
	public String visitTraitImplementation(TraitImplementation node) {
		// Trait implementations are handled in generate() method
		return "";
	}

	private String generateVTableStruct(TraitDefinition trait) {
		StringBuilder code = new StringBuilder();
		String vtableName = trait.getName() + "_VTable";
		
		code.append("typedef struct {\n");
		for (FunctionDefinition method : trait.getMethods()) {
			// Generate function pointer: ReturnType (*methodName)(void* this, ...);
			String returnType = "void";
			if (method.hasReturnType()) {
				returnType = typeMapper.mapToCType(method.getReturnType(), this::generateExpression);
			}
			
			code.append("    ").append(returnType).append(" (*").append(method.getName()).append(")(void* this");
			
			// Add parameters (skip first parameter if it's 'this')
			boolean firstParam = true;
			for (FunctionParameter param : method.getParameters()) {
				if (firstParam && param.getName().equals("this")) {
					firstParam = false;
					continue; // Skip 'this' parameter, already added as void*
				}
				firstParam = false;
				code.append(", ");
				if (param.hasType()) {
					code.append(typeMapper.mapToCType(param.getType(), this::generateExpression));
				} else {
					code.append("int"); // Default type
				}
				code.append(" ").append(param.getName());
			}
			code.append(");\n");
		}
		code.append("} ").append(vtableName).append(";");
		
		return code.toString();
	}

	private String generateTraitObjectStruct(TraitDefinition trait) {
		StringBuilder code = new StringBuilder();
		String objectName = trait.getName() + "_Object";
		String vtableName = trait.getName() + "_VTable";
		
		code.append("typedef struct {\n");
		code.append("    void* box;\n");
		code.append("    ").append(vtableName).append("* vtable;\n");
		code.append("} ").append(objectName).append(";");
		
		return code.toString();
	}

	private String generateTraitImplementation(TraitImplementation impl) {
		StringBuilder code = new StringBuilder();
		String traitName = impl.getTraitName();
		String implementingTypeName = getTypeName(impl.getImplementingType());
		String vtableName = traitName + "_" + implementingTypeName + "_VTable";
		
		// Generate wrapper functions for each method
		for (FunctionDefinition method : impl.getMethods()) {
			code.append(generateWrapperFunction(impl, method)).append("\n\n");
		}
		
		// Generate vtable instance
		code.append("static ").append(traitName).append("_VTable ").append(vtableName).append(" = {\n");
		for (int i = 0; i < impl.getMethods().size(); i++) {
			FunctionDefinition method = impl.getMethods().get(i);
			String wrapperName = getWrapperFunctionName(impl, method);
			code.append("    .").append(method.getName()).append(" = &").append(wrapperName);
			if (i < impl.getMethods().size() - 1) {
				code.append(",");
			}
			code.append("\n");
		}
		code.append("};");
		
		return code.toString();
	}

	private String generateTraitMethodFunction(TraitImplementation impl, FunctionDefinition method) {
		// Generate the actual implementation function with name: TypeName_methodName
		StringBuilder code = new StringBuilder();
		String implementingTypeName = getTypeName(impl.getImplementingType());
		String functionName = implementingTypeName + "_" + method.getName();
		
		// Generate return type
		String returnType = "void";
		if (method.hasReturnType()) {
			returnType = typeMapper.mapToCType(method.getReturnType(), this::generateExpression);
		}
		
		// Generate function signature
		code.append("static ").append(returnType).append(" ").append(functionName).append("(");
		
		// Generate parameters (first parameter is the implementing type, not void*)
		List<String> params = new ArrayList<>();
		boolean firstParam = true;
		for (FunctionParameter param : method.getParameters()) {
			if (firstParam && param.getName().equals("this")) {
				// First parameter is 'this', use implementing type
				params.add(implementingTypeName + "* " + param.getName());
				firstParam = false;
			} else {
				firstParam = false;
				if (param.hasType()) {
					params.add(typeMapper.mapToCType(param.getType(), this::generateExpression) + " " + param.getName());
				} else {
					params.add("int " + param.getName());
				}
			}
		}
		code.append(String.join(", ", params));
		code.append(") {\n");
		
		// Generate function body
		indentLevel++;
		if (method.getBody() != null) {
			Block body = (Block) method.getBody();
			for (Statement stmt : body.getStatements()) {
				code.append(indent()).append(stmt.accept(this)).append("\n");
			}
		}
		indentLevel--;
		code.append("}");
		
		return code.toString();
	}

	private String generateWrapperFunction(TraitImplementation impl, FunctionDefinition method) {
		StringBuilder code = new StringBuilder();
		String wrapperName = getWrapperFunctionName(impl, method);
		String implementingTypeName = getTypeName(impl.getImplementingType());
		String actualFunctionName = implementingTypeName + "_" + method.getName();
		
		// Function signature: static ReturnType wrapperName(void* this, ...)
		String returnType = "void";
		if (method.hasReturnType()) {
			returnType = typeMapper.mapToCType(method.getReturnType(), this::generateExpression);
		}
		
		code.append("static ").append(returnType).append(" ").append(wrapperName).append("(void* this");
		
		// Add parameters (skip first parameter if it's 'this')
		boolean firstParam = true;
		for (FunctionParameter param : method.getParameters()) {
			if (firstParam && param.getName().equals("this")) {
				firstParam = false;
				continue; // Skip 'this' parameter
			}
			firstParam = false;
			code.append(", ");
			if (param.hasType()) {
				code.append(typeMapper.mapToCType(param.getType(), this::generateExpression));
			} else {
				code.append("int"); // Default type
			}
			code.append(" ").append(param.getName());
		}
		code.append(") {\n");
		
		// Cast void* to concrete type
		code.append("    ").append(implementingTypeName).append("* self = (").append(implementingTypeName).append("*)this;\n");
		
		// Call the actual implementation
		indentLevel++;
		if (!returnType.equals("void")) {
			code.append(indent()).append("return ");
		} else {
			code.append(indent());
		}
		code.append(actualFunctionName).append("(self");
		
		// Add remaining parameters
		firstParam = true;
		for (FunctionParameter param : method.getParameters()) {
			if (firstParam && param.getName().equals("this")) {
				firstParam = false;
				continue;
			}
			firstParam = false;
			code.append(", ").append(param.getName());
		}
		code.append(");\n");
		indentLevel--;
		
		code.append("}");
		
		return code.toString();
	}

	private String getWrapperFunctionName(TraitImplementation impl, FunctionDefinition method) {
		String implementingTypeName = getTypeName(impl.getImplementingType());
		return implementingTypeName + "_" + method.getName() + "_wrapper";
	}

	private String getTypeName(Type type) {
		// Convert type to a string suitable for C identifiers
		if (type instanceof NamedType) {
			return ((NamedType) type).getName();
		} else if (type instanceof PointerType) {
			PointerType ptr = (PointerType) type;
			return getTypeName(ptr.getBaseType()) + "_ptr";
		} else if (type instanceof ArrayType) {
			return "Array";
		} else if (type instanceof GenericType) {
			GenericType gen = (GenericType) type;
			StringBuilder name = new StringBuilder(gen.getBaseName());
			for (Type arg : gen.getTypeArguments()) {
				name.append("_").append(getTypeName(arg));
			}
			return name.toString();
		}
		return "Unknown";
	}

	private boolean isTraitType(Type type) {
		// Check if a NamedType matches a trait name
		if (type instanceof NamedType) {
			String name = ((NamedType) type).getName();
			for (TraitDefinition trait : traits) {
				if (trait.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private TraitImplementation findTraitImplementation(String traitName, Type implementingType) {
		// Find the trait implementation for a given type
		for (TraitImplementation impl : traitImplementations) {
			if (impl.getTraitName().equals(traitName)) {
				// Compare implementing types - simplified comparison
				if (typesMatch(impl.getImplementingType(), implementingType)) {
					return impl;
				}
			}
		}
		return null;
	}

	private boolean typesMatch(Type type1, Type type2) {
		// Simple type matching - for now, just compare string representations
		// This is a simplified approach; a full implementation would need proper type resolution
		String name1 = getTypeName(type1);
		String name2 = getTypeName(type2);
		return name1.equals(name2);
	}

	private TraitImplementation findTraitImplementationForExpression(String traitName, Node expression) {
		// Try to infer the type of an expression and find the implementation
		// This is simplified - in a full compiler, we'd need proper type inference
		// For now, we'll look for common patterns:
		// - Identifier: check if it's a variable with known type
		// - Function call: check return type
		// - For now, return null and let the caller handle it
		return null;
	}

	private String getTraitObjectTypeName(String traitName) {
		return traitName + "_Object";
	}

	private String getVTableInstanceName(String traitName, Type implementingType) {
		String implementingTypeName = getTypeName(implementingType);
		return traitName + "_" + implementingTypeName + "_VTable";
	}

	private String indent() {
		return INDENT.repeat(indentLevel);
	}
}
