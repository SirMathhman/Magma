package magma;

import java.util.HashMap;
import java.util.Map;

class GenericRegistry {
	static class GenericStruct {
		final String name;
		final String typeParams;
		final String fields;
		final Map<String, String> typeMapping;

		GenericStruct(StructTemplate template) {
			this.name = template.name;
			this.typeParams = template.typeParams;
			this.fields = template.fields;
			this.typeMapping = new HashMap<>(template.typeMapping);
		}
	}

	static class GenericFunction {
		final String name;
		final String typeParams;
		final String params;
		final String returnType;
		final String body;
		final Map<String, String> typeMapping;

		GenericFunction(FunctionTemplate template) {
			this.name = template.name;
			this.typeParams = template.typeParams;
			this.params = template.params;
			this.returnType = template.returnType;
			this.body = template.body;
			this.typeMapping = new HashMap<>(template.typeMapping);
		}
	}

	static class GenericClass {
		final String name;
		final String typeParams;
		final String params;
		final String body;
		final Map<String, String> typeMapping;

		GenericClass(ClassTemplate template) {
			this.name = template.name;
			this.typeParams = template.typeParams;
			this.params = template.params;
			this.body = template.body;
			this.typeMapping = new HashMap<>(template.typeMapping);
		}
	}

	static class StructTemplate {
		final String name;
		final String typeParams;
		final Map<String, String> typeMapping;
		String fields;

		StructTemplate(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.fields = "";
			this.typeMapping = new HashMap<>();
		}
	}

	static class FunctionTemplate {
		final String name;
		final String typeParams;
		final Map<String, String> typeMapping;
		String params;
		String returnType;
		String body;

		FunctionTemplate(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.params = "";
			this.returnType = "";
			this.body = "";
			this.typeMapping = new HashMap<>();
		}
	}

	static class ClassTemplate {
		final String name;
		final String typeParams;
		final Map<String, String> typeMapping;
		String params;
		String body;

		ClassTemplate(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.params = "";
			this.body = "";
			this.typeMapping = new HashMap<>();
		}
	}

	static class FunctionData {
		final String name;
		final String typeParams;
		String params;
		String returnType;
		String body;

		FunctionData(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.params = "";
			this.returnType = "";
			this.body = "";
		}
	}

	static class StructRegistration {
		final String name;
		final String typeParams;
		final Map<String, String> typeMapping;
		String fields;

		StructRegistration(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.fields = "";
			this.typeMapping = new HashMap<>();
		}
	}

	static class FunctionRegistration {
		final FunctionData functionData;
		final Map<String, String> typeMapping;

		FunctionRegistration(FunctionData data, Map<String, String> typeMapping) {
			this.functionData = data;
			this.typeMapping = typeMapping;
		}
	}

	static class ClassData {
		final String name;
		final String typeParams;
		String params;
		String body;

		ClassData(String name, String typeParams) {
			this.name = name;
			this.typeParams = typeParams;
			this.params = "";
			this.body = "";
		}
	}

	static class ClassRegistration {
		final ClassData classData;
		final Map<String, String> typeMapping;

		ClassRegistration(ClassData data, Map<String, String> typeMapping) {
			this.classData = data;
			this.typeMapping = typeMapping;
		}
	}

	static class StructCodeParams {
		final String structName;
		final String fields;
		final Map<String, String> typeMapping;

		StructCodeParams(String structName, String fields) {
			this.structName = structName;
			this.fields = fields;
			this.typeMapping = new HashMap<>();
		}
	}

	static class FunctionCodeParams {
		final String functionName;
		final String params;
		final Map<String, String> typeMapping;
		String returnType;
		String body;

		FunctionCodeParams(String functionName, String params) {
			this.functionName = functionName;
			this.params = params;
			this.returnType = "";
			this.body = "";
			this.typeMapping = new HashMap<>();
		}
	}

	private static final Map<String, GenericStruct> genericStructs = new HashMap<>();
	private static final Map<String, GenericFunction> genericFunctions = new HashMap<>();
	private static final Map<String, GenericClass> genericClasses = new HashMap<>();
	private static final Map<String, String> monomorphizedStructs = new HashMap<>();
	private static final Map<String, String> monomorphizedFunctions = new HashMap<>();
	private static final Map<String, String> monomorphizedClasses = new HashMap<>();

	static void registerGenericStruct(StructRegistration registration) {
		// Copy additional data
		StructTemplate fullTemplate = new StructTemplate(registration.name, registration.typeParams) {
			{
				this.fields = registration.fields;
				this.typeMapping.putAll(registration.typeMapping);
			}
		};
		genericStructs.put(fullTemplate.name, new GenericStruct(fullTemplate));
	}

	static void registerGenericFunction(FunctionRegistration registration) {
		FunctionTemplate template =
				new FunctionTemplate(registration.functionData.name, registration.functionData.typeParams) {
					{
						this.params = registration.functionData.params;
						this.returnType = registration.functionData.returnType;
						this.body = registration.functionData.body;
						this.typeMapping.putAll(registration.typeMapping);
					}
				};
		genericFunctions.put(template.name, new GenericFunction(template));
	}

	static void registerGenericClass(ClassRegistration registration) {
		ClassTemplate template = new ClassTemplate(registration.classData.name, registration.classData.typeParams) {
			{
				this.params = registration.classData.params;
				this.body = registration.classData.body;
				this.typeMapping.putAll(registration.typeMapping);
			}
		};
		genericClasses.put(template.name, new GenericClass(template));
	}

	static String monomorphizeStruct(String structName, String typeArg) throws CompileException {
		GenericStruct template = genericStructs.get(structName);
		if (template == null) throw new CompileException("Generic struct not found: " + structName);

		// Get the C type for proper mangling
		String cTypeArg = getCType(typeArg, template.typeMapping);
		String key = structName + "_" + getMangledTypeName(cTypeArg);

		// Return already monomorphized version if exists
		if (monomorphizedStructs.containsKey(key)) return monomorphizedStructs.get(key);

		// Replace type parameter with actual type
		String monomorphizedName = structName + "_" + getMangledTypeName(cTypeArg);
		String monomorphizedFields = template.fields.replaceAll("\\b" + template.typeParams + "\\b", typeArg);

		// Generate the monomorphized struct
		StructCodeParams structParams = new StructCodeParams(monomorphizedName, monomorphizedFields);
		structParams.typeMapping.putAll(template.typeMapping);
		String result = generateStructCode(structParams);
		monomorphizedStructs.put(key, result);
		return result;
	}

	static String monomorphizeFunction(String functionName, String typeArg) throws CompileException {
		GenericFunction template = genericFunctions.get(functionName);
		if (template == null) throw new CompileException("Generic function not found: " + functionName);

		// Get the C type for proper mangling
		String cTypeArg = getCType(typeArg, template.typeMapping);
		String key = functionName + "_" + getMangledTypeName(cTypeArg);

		// Return already monomorphized version if exists
		if (monomorphizedFunctions.containsKey(key)) return monomorphizedFunctions.get(key);

		// Replace type parameter with actual type
		String monomorphizedName = functionName + "_" + getMangledTypeName(cTypeArg);
		String monomorphizedParams = template.params.replaceAll("\\b" + template.typeParams + "\\b", typeArg);
		String monomorphizedReturnType =
				template.returnType != null ? template.returnType.replaceAll("\\b" + template.typeParams + "\\b", typeArg)
																		: null;
		String monomorphizedBody = template.body.replaceAll("\\b" + template.typeParams + "\\b", typeArg);

		// Generate the monomorphized function
		FunctionCodeParams funcParams = new FunctionCodeParams(monomorphizedName, monomorphizedParams);
		funcParams.returnType = monomorphizedReturnType;
		funcParams.body = monomorphizedBody;
		funcParams.typeMapping.putAll(template.typeMapping);
		String result = generateFunctionCode(funcParams);
		monomorphizedFunctions.put(key, result);
		return result;
	}

	static String monomorphizeClass(String className, String typeArg) throws CompileException {
		GenericClass template = genericClasses.get(className);
		if (template == null) throw new CompileException("Generic class not found: " + className);

		// Get the C type for proper mangling
		String cTypeArg = getCType(typeArg, template.typeMapping);
		String key = className + "_" + getMangledTypeName(cTypeArg);

		// Return already monomorphized version if exists
		if (monomorphizedClasses.containsKey(key)) return monomorphizedClasses.get(key);

		// Replace type parameter with actual type
		String monomorphizedName = className + "_" + getMangledTypeName(cTypeArg);
		String monomorphizedParams = template.params.replaceAll("\\b" + template.typeParams + "\\b", typeArg);
		String monomorphizedBody = template.body.replaceAll("\\b" + template.typeParams + "\\b", typeArg);

		// Generate the monomorphized class using the existing class compilation logic
		// Create a class-like construct that can be compiled normally
		String classConstruct = "class fn " + monomorphizedName + "(" + monomorphizedParams + ") => {" + monomorphizedBody + "}";
		
		try {
			String result = Compiler.compileCode(classConstruct);
			monomorphizedClasses.put(key, result);
			return result;
		} catch (CompileException e) {
			throw new CompileException("Failed to monomorphize class " + className + ": " + e.getMessage());
		}
	}

	private static String getMangledTypeName(String type) {
		// Convert type names to valid C identifiers
		return type.replaceAll("\\*", "ptr_").replaceAll("\\W", "_");
	}

	private static String generateStructCode(StructCodeParams params) {
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
					String cType = getCType(fieldType, params.typeMapping);
					result.append(cType).append(" ").append(fieldName).append("; ");
				}
			}
		}
		result.append("};");
		return result.toString();
	}

	private static String generateFunctionCode(FunctionCodeParams params) throws CompileException {
		// Infer return type if not specified
		String cReturnType;
		// Simple inference - if body contains "return \\d+", assume int32_t
		if (params.returnType != null) cReturnType = getCType(params.returnType, params.typeMapping);
		else if (params.body.matches(".*return\\s+\\d+.*")) cReturnType = "int32_t";
		else // If returning a variable, try to infer from parameter types
			// For now, assume int32_t for simple cases
			if (params.body.matches(".*return\\s+\\w+.*")) cReturnType = "int32_t";
			else cReturnType = "void";

		// Parse parameters - format "value : I32" becomes "int32_t value"
		StringBuilder paramList = new StringBuilder();
		if (params.params != null && !params.params.trim().isEmpty()) {
			String[] paramArray = params.params.split(",");
			for (int i = 0; i < paramArray.length; i++) {
				String param = paramArray[i].trim();
				if (!param.isEmpty()) {
					String[] parts = param.split("\\s*:\\s*");
					if (parts.length == 2) {
						String paramName = parts[0].trim();
						String paramType = parts[1].trim();
						String cType = getCType(paramType, params.typeMapping);
						if (i > 0) paramList.append(", ");
						paramList.append(cType).append(" ").append(paramName);
					}
				}
			}
		}

		return cReturnType + " " + params.functionName + "(" + paramList + "){" + Compiler.compileCode(params.body) + "}";
	}

	private static String getCType(String type, Map<String, String> typeMapping) {
		String cType = typeMapping.get(type);
		// Assume it's a user-defined type (struct)
		if (cType == null) return "struct " + type;
		return cType;
	}
}