package magma;

import magma.node.StructMember;
import magma.node.VarInfo;

import java.util.List;
import java.util.Map;

/**
 * Helper class for working with struct members in the Magma compiler.
 */
public class StructMemberHelper {
	/**
	 * Parse a struct member declaration.
	 *
	 * @param member        The member declaration string (e.g., "age: I32")
	 * @param structMembers Map of struct members for type validation
	 * @param typeParams    List of type parameters, or null if not in a generic struct
	 * @return A StructMember object with the parsed information
	 * @throws CompileException If the member declaration is invalid
	 */
	public static StructMember parseStructMember(String member, Map<String, String[]> structMembers, 
	        List<String> typeParams) throws CompileException {
		// Parse member: name : type
		String[] parts = member.split(":");
		if (parts.length != 2) {
			throw new CompileException("Invalid struct member format", member);
		}

		String memberName = parts[0].trim();
		String memberType = parts[1].trim();

		// Validate member name
		if (!TypeHelper.isIdentifier(memberName)) {
			throw new CompileException("Invalid member name", member);
		}

		// Convert Magma type to C type
		String cType = convertType(memberType, structMembers, typeParams);

		return new StructMember(memberName, cType);
	}
	
	/**
	 * Parse a struct member declaration (legacy version for non-generic structs).
	 *
	 * @param member        The member declaration string (e.g., "age: I32")
	 * @param structMembers Map of struct members for type validation
	 * @return A StructMember object with the parsed information
	 * @throws CompileException If the member declaration is invalid
	 */
	public static StructMember parseStructMember(String member, Map<String, String[]> structMembers)
			throws CompileException {
		return parseStructMember(member, structMembers, null);
	}

	/**
	 * Validates that there is no trailing comma in the struct body.
	 *
	 * @param members    The array of split members
	 * @param structBody The original struct body for error reporting
	 * @throws CompileException If there is a trailing comma
	 */
	public static void validateNoTrailingComma(String[] members, String structBody) throws CompileException {
		if (members.length > 0 && members[members.length - 1].trim().isEmpty()) {
			throw new CompileException("Trailing comma not allowed in struct declaration", structBody);
		}
	}

	/**
	 * Convert a Magma type to its C equivalent.
	 *
	 * @param magmaType     The Magma type to convert
	 * @param structMembers Map of struct members for type validation
	 * @param typeParams    List of type parameters, or null if not in a generic struct
	 * @return The C equivalent type
	 * @throws CompileException If the type is unknown
	 */
	public static String convertType(String magmaType, Map<String, String[]> structMembers, 
	        List<String> typeParams) throws CompileException {
		// Check if it's a type parameter
		if (typeParams != null && typeParams.contains(magmaType)) {
			// It's a type parameter, keep as is for now (will be replaced when instantiated)
			return magmaType;
		}
		
		// Check if it's a generic type reference (e.g., MyWrapper<I32>)
		if (magmaType.contains("<")) {
			return handleGenericTypeReference(magmaType, structMembers);
		}
		
		// Handle primitive types
		String cType = convertPrimitiveType(magmaType);
		if (cType != null) {
			return cType;
		}

		// Check if it's a struct type
		if (structMembers.containsKey(magmaType)) {
			return magmaType;
		}

		throw new CompileException("Unknown type: " + magmaType, magmaType);
	}
	
	/**
	 * Convert a Magma type to its C equivalent (legacy version for non-generic structs).
	 *
	 * @param magmaType     The Magma type to convert
	 * @param structMembers Map of struct members for type validation
	 * @return The C equivalent type
	 * @throws CompileException If the type is unknown
	 */
	public static String convertType(String magmaType, Map<String, String[]> structMembers) throws CompileException {
		return convertType(magmaType, structMembers, null);
	}
	
	/**
	 * Handle a generic type reference (e.g., MyWrapper<I32>).
	 *
	 * @param typeReference The generic type reference
	 * @param structMembers Map of struct members for type validation
	 * @return The concrete type name
	 * @throws CompileException If the type reference is invalid
	 */
	private static String handleGenericTypeReference(String typeReference, 
	        Map<String, String[]> structMembers) throws CompileException {
		try {
			// Extract the struct name and type arguments
			int angleBracketPos = typeReference.indexOf('<');
			String structName = typeReference.substring(0, angleBracketPos).trim();
			
			// Parse the type arguments
			List<String> typeArgs = GenericTypeHelper.parseTypeArguments(typeReference);
			
			// Create the concrete struct name
			String concreteStructName = GenericTypeHelper.createConcreteStructName(structName, typeArgs);
			
			// If the concrete struct doesn't exist yet, instantiate it
			if (!structMembers.containsKey(concreteStructName)) {
				// This would be handled during variable declaration/initialization
				// We just need to return the concrete name here
			}
			
			return concreteStructName;
		} catch (Exception e) {
			throw new CompileException("Invalid generic type reference: " + typeReference, typeReference);
		}
	}

	/**
	 * Convert a primitive Magma type to its C equivalent.
	 *
	 * @param magmaType The Magma type to convert
	 * @return The C equivalent type, or null if not a primitive type
	 */
	private static String convertPrimitiveType(String magmaType) {
		switch (magmaType) {
			case "I8":
				return "int8_t";
			case "I16":
				return "int16_t";
			case "I32":
				return "int32_t";
			case "I64":
				return "int64_t";
			case "U8":
				return "uint8_t";
			case "U16":
				return "uint16_t";
			case "U32":
				return "uint32_t";
			case "U64":
				return "uint64_t";
			case "Bool":
				return "bool";
			default:
				return null;
		}
	}

	/**
	 * Process a value based on its member type.
	 *
	 * @param value      The value to process
	 * @param memberType The type of the member
	 * @param env        The environment with variable information
	 * @param stmt       The original statement for error reporting
	 * @return The processed value
	 * @throws CompileException If the value is invalid for the member type
	 */
	public static String processValueForType(String value, String memberType, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		if (memberType.equals("Bool")) {
			return processBooleanValue(value, stmt);
		} else if (memberType.startsWith("I") || memberType.startsWith("U")) {
			return processNumericValue(value, env, stmt);
		} else {
			// Custom struct types not supported for now
			throw new CompileException("Nested struct initialization not supported", stmt);
		}
	}

	/**
	 * Process a boolean value.
	 *
	 * @param value The value to process
	 * @param stmt  The original statement for error reporting
	 * @return The processed value
	 * @throws CompileException If the value is not a valid boolean
	 */
	private static String processBooleanValue(String value, String stmt) throws CompileException {
		if (value.equals("true") || value.equals("false")) {
			return value;
		} else {
			throw new CompileException("Invalid boolean value: " + value, stmt);
		}
	}

	/**
	 * Process a numeric value.
	 *
	 * @param value The value to process
	 * @param env   The environment with variable information
	 * @param stmt  The original statement for error reporting
	 * @return The processed value
	 * @throws CompileException If the value is not a valid number or variable
	 */
	private static String processNumericValue(String value, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		if (value.matches("\\d+")) {
			return value;
		} else if (TypeHelper.isIdentifier(value)) {
			VarInfo varInfo = env.get(value);
			if (varInfo == null) {
				throw new CompileException("Undefined variable: " + value, stmt);
			}
			return value;
		} else {
			throw new CompileException("Invalid numeric value: " + value, stmt);
		}
	}
}