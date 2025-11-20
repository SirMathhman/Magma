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

	// Interface for generating C expressions from AST nodes
	public interface CTypeGenerator {
		String generateExpression(Node node);
	}
}

