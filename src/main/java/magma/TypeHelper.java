package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class that provides type mapping functionality.
 */
public class TypeHelper {
	// Keep track of defined struct types
	private static final Map<String, String> structTypes = new HashMap<>();

	/**
	 * Maps integer types from Magma to C.
	 *
	 * @param type The Magma integer type string
	 * @return The corresponding C type string, or null if not an integer type
	 */
	private static String mapIntegerType(String type) {
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			default -> null;
		};
	}

	/**
	 * Maps special types (non-integer) from Magma to C.
	 *
	 * @param type The Magma type string
	 * @return The corresponding C type string, or null if not a special type
	 */
	private static String mapSpecialType(String type) {
		return switch (type) {
			case "Bool" -> "bool";
			case "Void" -> "void";
			case "String" -> "String";
			default -> null;
		};
	}

	/**
	 * Maps Magma types to C types.
	 *
	 * @param type The Magma type string
	 * @param out  The StringBuilder to append any necessary declarations to, or null if not needed
	 * @return The corresponding C type string, or null if not found
	 * @throws CompileException If there is an error mapping the type
	 */
	public static String mapType(String type, StringBuilder out) throws CompileException {
		// Check if it's a generic type reference
		if (type.contains("<")) {
			return handleGenericTypeReference(type, out);
		}
		
		// Check integer types first
		String intType = mapIntegerType(type);
		if (intType != null) {
			return intType;
		}

		// Check special types next
		String specialType = mapSpecialType(type);
		if (specialType != null) {
			return specialType;
		}

		// Check if it's a struct type
		return structTypes.get(type);
	}
	
	/**
	 * Maps Magma types to C types (legacy version).
	 *
	 * @param type The Magma type string
	 * @return The corresponding C type string, or null if not found
	 */
	public static String mapType(String type) {
		try {
			return mapType(type, null);
		} catch (CompileException e) {
			// This should not happen in the legacy version
			return null;
		}
	}
	
	/**
	 * Handles a generic type reference (e.g., MyWrapper<I32>).
	 *
	 * @param typeReference The generic type reference
	 * @param out           The StringBuilder to append any necessary declarations to, or null if not needed
	 * @return The concrete type name
	 * @throws CompileException If the type reference is invalid
	 */
	private static String handleGenericTypeReference(String typeReference, StringBuilder out) throws CompileException {
		try {
			// Extract the struct name and type arguments
			int angleBracketPos = typeReference.indexOf('<');
			String structName = typeReference.substring(0, angleBracketPos).trim();
			
			// Parse the type arguments
			List<String> typeArgs = GenericTypeHelper.parseTypeArguments(typeReference);
			
			// Create the concrete struct name
			String concreteStructName = GenericTypeHelper.createConcreteStructName(structName, typeArgs);
			
			// If out is provided, instantiate the concrete struct if needed
			if (out != null && !structTypes.containsKey(concreteStructName)) {
				concreteStructName = StructHelper.instantiateGenericStruct(structName, typeArgs, out);
			}
			
			return concreteStructName;
		} catch (Exception e) {
			throw new CompileException("Invalid generic type reference: " + typeReference, typeReference);
		}
	}

	/**
	 * Registers a struct type
	 *
	 * @param name The struct name
	 */
	public static void registerStructType(String name) {
		structTypes.put(name, name);
	}

	/**
	 * Checks if a string is a valid identifier.
	 *
	 * @param s The string to check
	 * @return true if the string is a valid identifier, false otherwise
	 */
	public static boolean isIdentifier(String s) {
		if (s.isEmpty()) return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i == 0) {
				if (!(Character.isLetter(c) || c == '_')) return false;
			} else {
				if (!(Character.isLetterOrDigit(c) || c == '_')) return false;
			}
		}
		return true;
	}
}