package magma;

import java.util.HashMap;
import java.util.Map;

class VariadicFunctionRegistry {
	private static final Map<String, GenericRegistry.VariadicFunctionData> variadicFunctions = new HashMap<>();
	private static final Map<String, String> monomorphizedVariadicFunctions = new HashMap<>();
	private static final Map<String, String> variadicTypeMapping = new HashMap<>();

	static void registerVariadicFunction(GenericRegistry.VariadicFunctionData variadicData, Map<String, String> typeMapping) {
		variadicFunctions.put(variadicData.name, variadicData);
		variadicTypeMapping.putAll(typeMapping);
	}

	static String monomorphizeVariadicFunction(String functionName, int length) throws CompileException {
		GenericRegistry.VariadicFunctionData template = variadicFunctions.get(functionName);
		if (template == null) throw new CompileException("Variadic function not found: " + functionName);

		String key = functionName + "_" + length;
		
		// Return already monomorphized version if exists
		if (monomorphizedVariadicFunctions.containsKey(key)) return monomorphizedVariadicFunctions.get(key);

		// Generate parameters for the monomorphized function
		StringBuilder paramList = new StringBuilder();
		String elementCType = GenericRegistry.getCType(template.elementType, variadicTypeMapping);
		
		for (int i = 0; i < length; i++) {
			if (i > 0) paramList.append(", ");
			paramList.append(elementCType).append(" arg").append(i);
		}

		// Generate the monomorphized function
		String monomorphizedName = functionName + "_" + length;
		GenericRegistry.FunctionCodeParams funcParams = new GenericRegistry.FunctionCodeParams(monomorphizedName, paramList.toString());
		funcParams.returnType = template.returnType;
		funcParams.body = template.body;
		funcParams.typeMapping.putAll(variadicTypeMapping);
		
		String result = GenericRegistry.generateFunctionCode(funcParams);
		monomorphizedVariadicFunctions.put(key, result);
		return result;
	}
}