package magma;

import java.util.Map;
import java.util.regex.Matcher;

class VariadicCompilerUtils {
	static String compileVariadicFunctionStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		String functionName = matcher.group(1);
		String typeParams = matcher.group(2);
		String arrayName = matcher.group(3);
		String elementType = matcher.group(4);
		String lengthParam = matcher.group(5);
		String returnType = matcher.group(6);
		String body = matcher.group(7);
		
		// Store the variadic function template for later monomorphization
		GenericRegistry.ArrayTypeInfo typeInfo = new GenericRegistry.ArrayTypeInfo(elementType, lengthParam);
		GenericRegistry.VariadicArrayInfo arrayInfo = new GenericRegistry.VariadicArrayInfo(arrayName, typeInfo);
		GenericRegistry.FunctionNameInfo nameInfo = new GenericRegistry.FunctionNameInfo(functionName, typeParams);
		GenericRegistry.VariadicFunctionSignature signature = new GenericRegistry.VariadicFunctionSignature(nameInfo, arrayInfo);
		GenericRegistry.VariadicFunctionBody functionBody = new GenericRegistry.VariadicFunctionBody(returnType, body);
		GenericRegistry.VariadicFunctionParams params = new GenericRegistry.VariadicFunctionParams(signature, functionBody);
		GenericRegistry.VariadicFunctionData variadicData = new GenericRegistry.VariadicFunctionData(params);
		GenericRegistry.registerVariadicFunction(variadicData, typeMapping);
		return ""; // Generic definitions don't generate immediate output
	}

	static String inferReturnType(String body) {
		if (body.matches(".*return\\s+\\d+.*")) return "int32_t";
		if (body.matches(".*return\\s+[\\w\\s+\\-*/]+;?.*")) return "int32_t"; // Handle expressions like "x + y"
		return "void";
	}

	static String getMangledTypeName(String type) {
		// Convert type names to valid C identifiers
		return type.replaceAll("\\*", "ptr_").replaceAll("\\W", "_");
	}

	static String generateStructCode(GenericRegistry.StructCodeParams params) {
		if (params.fields.isEmpty()) return "struct " + params.structName + " {};";

		// Parse fields - format "x : I32" becomes "int32_t x;"
		StringBuilder result = new StringBuilder("struct " + params.structName + " { ");
		String[] fieldArray = params.fields.split(",");

		for (String field : fieldArray) {
			String trimmedField = field.trim();
			if (!trimmedField.isEmpty()) {
				String[] parts = trimmedField.split("\\s*:\\s*");
				if (parts.length == 2) {
					String fieldName = parts[0].trim();
					String fieldType = parts[1].trim();
					String cType = GenericRegistry.getCType(fieldType, params.typeMapping);
					result.append(cType).append(" ").append(fieldName).append("; ");
				}
			}
		}
		result.append("};");
		return result.toString();
	}
}