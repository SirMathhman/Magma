package magma.types;

import magma.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeResolver {
	private final Map<String, TypeDefinition> typeDefinitions;

	public TypeResolver(List<TypeDefinition> typeDefinitions) {
		this.typeDefinitions = new HashMap<>();
		for (TypeDefinition def : typeDefinitions) {
			this.typeDefinitions.put(def.getName(), def);
		}
	}

	public Type resolve(Type type) {
		if (type instanceof NamedType) {
			return resolveNamedType((NamedType) type);
		} else if (type instanceof GenericType) {
			return resolveGenericType((GenericType) type);
		} else if (type instanceof PointerType) {
			PointerType ptr = (PointerType) type;
			Type baseType = resolve(ptr.getBaseType());
			return new PointerType(baseType);
		} else if (type instanceof ArrayType) {
			ArrayType arr = (ArrayType) type;
			Type elementType = resolve(arr.getElementType());
			if (arr.hasStart()) {
				// When start is present, length is actually the end
				return new ArrayType(elementType, arr.getLength(), arr.getStart());
			} else {
				return new ArrayType(elementType, arr.getLength());
			}
		}
		return type;
	}

	private Type resolveNamedType(NamedType namedType) {
		String name = namedType.getName();
		TypeDefinition def = typeDefinitions.get(name);
		if (def != null) {
			// Simple type alias without generics
			if (!def.hasGenericParameters()) {
				return resolve(def.getAliasedType());
			}
			// For now, we can't resolve generic types without type arguments
			// This will be handled when we encounter GenericType
		}
		return namedType;
	}

	private Type resolveGenericType(GenericType genericType) {
		String baseName = genericType.getBaseName();
		TypeDefinition def = typeDefinitions.get(baseName);
		if (def != null && def.hasGenericParameters()) {
			// For now, we do simple substitution
			// This is a simplified version - full generic substitution would be more complex
			// We'll just resolve the aliased type and hope the generic parameters match
			// A full implementation would substitute type parameters
			return resolve(def.getAliasedType());
		}
		// If not a type definition, resolve type arguments recursively
		List<Type> resolvedArgs = genericType.getTypeArguments().stream()
				.map(this::resolve)
				.toList();
		return new GenericType(baseName, resolvedArgs);
	}
}

