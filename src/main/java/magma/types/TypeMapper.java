package magma.types;

import magma.ast.*;

public class TypeMapper {
	private TypeResolver typeResolver;

	public TypeMapper() {
		this.typeResolver = null;
	}

	public void setTypeResolver(TypeResolver resolver) {
		this.typeResolver = resolver;
	}

	public TypeResolver getTypeResolver() {
		return typeResolver;
	}

	public String mapToCType(Type type, CTypeGenerator generator) {
		// Resolve type aliases first
		Type resolvedType = type;
		if (typeResolver != null) {
			resolvedType = typeResolver.resolve(type);
		}

		if (resolvedType instanceof NamedType) {
			return mapNamedType((NamedType) resolvedType);
		} else if (resolvedType instanceof PointerType) {
			return mapPointerType((PointerType) resolvedType, generator);
		} else if (resolvedType instanceof ArrayType) {
			return mapArrayType((ArrayType) resolvedType, generator);
		} else if (resolvedType instanceof GenericType) {
			return mapGenericType((GenericType) resolvedType);
		} else if (resolvedType instanceof UnionType) {
			return mapUnionType((UnionType) resolvedType, generator);
		} else if (resolvedType instanceof SizeOfType) {
			return mapSizeOfType((SizeOfType) resolvedType, generator);
		} else if (resolvedType instanceof BinaryType) {
			return mapBinaryType((BinaryType) resolvedType, generator);
		} else {
			throw new RuntimeException("Unknown type: " + resolvedType.getClass().getSimpleName());
		}
	}

	private String mapNamedType(NamedType node) {
		String name = node.getName();
		switch (name) {
			case "I32":
				return "int32_t";
			case "USize":
				return "size_t";
			case "U8":
				return "uint8_t";
			case "Void":
				return "void";
			default:
				// For unknown types, use the name as-is (might be a typedef)
				return name;
		}
	}

	private String mapPointerType(PointerType node, CTypeGenerator generator) {
		String baseType = mapToCType(node.getBaseType(), generator);
		return baseType + "*";
	}

	private String mapArrayType(ArrayType node, CTypeGenerator generator) {
		String elementType = mapToCType(node.getElementType(), generator);
		if (node.hasStart()) {
			// [Type; Start; End] - dynamic array, use pointer
			return elementType + "*";
		} else {
			// [Type; Length] - fixed-size array
			String length = generator.generateExpression(node.getLength());
			return elementType + "[" + length + "]";
		}
	}

	private String mapGenericType(GenericType node) {
		// For now, generic types are not fully supported
		// Return a placeholder or the base name
		String baseName = node.getBaseName();
		// Could generate something like "Allocated_int32_t_size_t" but for now just use base name
		return baseName;
	}

	private String mapUnionType(UnionType node, CTypeGenerator generator) {
		// Special case: PointerType | 0 simplifies to just the pointer type
		if (node.getVariants().size() == 2) {
			Type variant1 = node.getVariants().get(0);
			Type variant2 = node.getVariants().get(1);
			
			// Check if one is a pointer and the other is "0"
			if (variant1 instanceof PointerType && isZeroType(variant2)) {
				return mapPointerType((PointerType) variant1, generator);
			} else if (variant2 instanceof PointerType && isZeroType(variant1)) {
				return mapPointerType((PointerType) variant2, generator);
			}
		}
		
		// Generate unique name for union type
		StringBuilder nameBuilder = new StringBuilder("Union_");
		for (Type variant : node.getVariants()) {
			String variantName = mapToCType(variant, generator);
			String sanitized = sanitizeTypeName(variantName);
			nameBuilder.append(sanitized).append("_");
		}
		// Remove trailing underscore
		if (nameBuilder.length() > 0 && nameBuilder.charAt(nameBuilder.length() - 1) == '_') {
			nameBuilder.setLength(nameBuilder.length() - 1);
		}
		return nameBuilder.toString();
	}

	private boolean isZeroType(Type type) {
		// Check if type is the literal "0" (could be NamedType with name "0")
		if (type instanceof NamedType) {
			return "0".equals(((NamedType) type).getName());
		}
		return false;
	}

	private String mapSizeOfType(SizeOfType node, CTypeGenerator generator) {
		// SizeOf<Type> maps to sizeof(type) expression
		String innerType = mapToCType(node.getType(), generator);
		return "sizeof(" + innerType + ")";
	}

	private String mapBinaryType(BinaryType node, CTypeGenerator generator) {
		// BinaryType (e.g., SizeOf<Type> * Length) maps to (left * right) expression
		String left = mapToCType(node.getLeft(), generator);
		String right = mapToCType(node.getRight(), generator);
		if (node.getOperator().type() == magma.lexer.TokenType.STAR) {
			return "(" + left + " * " + right + ")";
		}
		throw new RuntimeException("Unsupported type operator: " + node.getOperator().type());
	}

	private String sanitizeTypeName(String typeName) {
		// Replace special characters with underscores
		return typeName.replaceAll("[<>,\\*\\[\\]\\s]", "_");
	}

	// Interface for generating C expressions from AST nodes
	public interface CTypeGenerator {
		String generateExpression(Node node);
	}
}

