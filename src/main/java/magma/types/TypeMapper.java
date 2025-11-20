package magma.types;

import magma.ast.*;

public class TypeMapper {
	public String mapToCType(Type type, CTypeGenerator generator) {
		if (type instanceof NamedType) {
			return mapNamedType((NamedType) type);
		} else if (type instanceof PointerType) {
			return mapPointerType((PointerType) type, generator);
		} else if (type instanceof ArrayType) {
			return mapArrayType((ArrayType) type, generator);
		} else if (type instanceof GenericType) {
			return mapGenericType((GenericType) type);
		} else {
			throw new RuntimeException("Unknown type: " + type.getClass().getSimpleName());
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

