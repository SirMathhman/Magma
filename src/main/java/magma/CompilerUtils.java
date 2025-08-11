package magma;

import java.util.Map;
import java.util.regex.Matcher;

class CompilerUtils {
	static String compileBlockStatement(Matcher matcher) throws CompileException {
		String content = matcher.group(1).trim();
		if (content.isEmpty()) return "{}";
		return "{" + Compiler.compileCode(content) + "}";
	}

	static String compileIfStatement(Matcher matcher) throws CompileException {
		String condition = matcher.group(1);
		String thenBlock = matcher.group(2);
		String elseBlock = matcher.group(3);

		String result = "if(" + condition + "){" + Compiler.compileCode(thenBlock) + "}";
		if (elseBlock != null) result += " else {" + Compiler.compileCode(elseBlock) + "}";
		return result;
	}

	static String compileWhileStatement(Matcher matcher) throws CompileException {
		String condition = matcher.group(1);
		String body = matcher.group(2);
		return "while(" + condition + "){" + Compiler.compileCode(body) + "}";
	}

	static String compileStructStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		String structName = matcher.group(1);
		String fields = matcher.group(2).trim();

		if (fields.isEmpty()) return "struct " + structName + " {};";

		// Parse fields - format "x : I32" becomes "int32_t x;"
		StringBuilder result = new StringBuilder("struct " + structName + " {");
		String[] fieldArray = fields.split(",");

		for (String field : fieldArray) {
			String trimmedField = field.trim();
			if (!trimmedField.isEmpty()) {
				String[] parts = trimmedField.split("\\s*:\\s*");
				if (parts.length == 2) {
					String fieldName = parts[0].trim();
					String fieldType = parts[1].trim();
					String cType = typeMapping.get(fieldType);
					if (cType == null) throw new CompileException("Unsupported type: " + fieldType);
					result.append(cType).append(" ").append(fieldName).append(";");
				}
			}
		}
		result.append("};");
		return result.toString();
	}

	static String compileFunctionStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		String functionName = matcher.group(1);
		String params = matcher.group(2);
		String returnType = matcher.group(3);
		String body = matcher.group(4);

		// Default return type is void, but try to infer from body
		String cReturnType;
		// Try to infer return type from function body
		if (returnType != null) {
			cReturnType = typeMapping.get(returnType);
			if (cReturnType == null) throw new CompileException("Unsupported type: " + returnType);
		} else cReturnType = inferReturnType(body);

		// Parse parameters - format "value : I32" becomes "int32_t value"
		StringBuilder paramList = new StringBuilder();
		if (params != null && !params.trim().isEmpty()) {
			String[] paramArray = params.split(",");
			for (int i = 0; i < paramArray.length; i++) {
				String param = paramArray[i].trim();
				if (!param.isEmpty()) {
					String[] parts = param.split("\\s*:\\s*");
					if (parts.length == 2) {
						String paramName = parts[0].trim();
						String paramType = parts[1].trim();
						String cType = typeMapping.get(paramType);
						if (cType == null) throw new CompileException("Unsupported type: " + paramType);
						if (i > 0) paramList.append(", ");
						paramList.append(cType).append(" ").append(paramName);
					}
				}
			}
		}

		return cReturnType + " " + functionName + "(" + paramList + "){" + Compiler.compileCode(body) + "}";
	}

	static String compileGenericStructStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		String structName = matcher.group(1);
		String typeParams = matcher.group(2);
		String fields = matcher.group(3).trim();
		
		// Store the generic struct template
		GenericRegistry.StructRegistration registration = new GenericRegistry.StructRegistration(structName, typeParams);
		registration.fields = fields;
		registration.typeMapping.putAll(typeMapping);
		GenericRegistry.registerGenericStruct(registration);
		return ""; // Generic definitions don't generate immediate output
	}
	
	static String compileGenericFunctionStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		String functionName = matcher.group(1);
		String typeParams = matcher.group(2);
		String params = matcher.group(3);
		String returnType = matcher.group(4);
		String body = matcher.group(5);
		
		// Store the generic function template
		GenericRegistry.FunctionData functionData = new GenericRegistry.FunctionData(functionName, typeParams);
		functionData.params = params;
		functionData.returnType = returnType;
		functionData.body = body;
		GenericRegistry.registerGenericFunction(new GenericRegistry.FunctionRegistration(functionData, typeMapping));
		return ""; // Generic definitions don't generate immediate output
	}

	private static String inferReturnType(String body) {
		// Simple type inference based on return statements
		if (body.matches(".*return\\s+\\d+.*")) return "int32_t";
		// Default to void if no clear return type can be inferred
		return "void";
	}
}