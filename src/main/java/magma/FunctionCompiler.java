package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FunctionCompiler {

	private static class FunctionComponents {
		final String functionName;
		final String params;
		String returnType;
		String body;

		FunctionComponents(String functionName, String params) {
			this.functionName = functionName;
			this.params = params;
		}

		void setReturnTypeAndBody(String returnType, String body) {
			this.returnType = returnType;
			this.body = body;
		}
	}

	private static class ParameterContext {
		final StringBuilder paramList;
		final String param;
		boolean needsComma;
		Map<String, String> typeMapping;

		ParameterContext(StringBuilder paramList, String param) {
			this.paramList = paramList;
			this.param = param;
		}

		void setCommaAndMapping(boolean needsComma, Map<String, String> typeMapping) {
			this.needsComma = needsComma;
			this.typeMapping = typeMapping;
		}
	}

	static String compileFunctionStatement(Matcher matcher, Map<String, String> typeMapping) throws CompileException {
		if (!matcher.matches()) throw new CompileException("Function statement did not match expected pattern.");
		FunctionComponents components = extractComponents(matcher);
		return buildFunction(components, typeMapping);
	}

	private static FunctionComponents extractComponents(Matcher matcher) {
		String functionName = matcher.group(1);
		String params = matcher.group(2);
		String returnType = matcher.group(3);
		String body = matcher.group(4);
		FunctionComponents components = new FunctionComponents(functionName, params);
		components.setReturnTypeAndBody(returnType, body);
		return components;
	}

	private static String buildFunction(FunctionComponents components, Map<String, String> typeMapping)
			throws CompileException {
		String cReturnType = determineReturnType(components, typeMapping);
		StringBuilder paramList = parseParameters(components.params, typeMapping);
		InnerFunctionProcessor.InnerFunctionResult innerResult = processInnerFunctions(components, typeMapping);

		// Check if we need struct generation for local variables or parameters with inner functions
		String structDef = "";
		String compiledBody = Compiler.compileCode(innerResult.processedBody);
		
		if (!innerResult.innerFunctionDefs.isEmpty() && 
			(hasLocalVariables(innerResult.processedBody) || (components.params != null && !components.params.trim().isEmpty()))) {
			StructGenerationParams structParams = new StructGenerationParams(components.functionName, 
				innerResult.processedBody);
			structParams.setTypeMapping(typeMapping);
			structParams.setFunctionParams(components.params);
			StructGenerationResult structResult = generateStructForLocalVariables(structParams);
			structDef = structResult.structDefinition + " ";
			compiledBody = structResult.modifiedBody;
		}

		String innerDefs = innerResult.innerFunctionDefs.isEmpty() ? "" : innerResult.innerFunctionDefs + " ";
		return structDef + innerDefs + cReturnType + " " + components.functionName + "(" + paramList + "){" +
					 compiledBody + "}";
	}

	private static String determineReturnType(FunctionComponents components, Map<String, String> typeMapping)
			throws CompileException {
		if (components.returnType != null) {
			String cReturnType = typeMapping.get(components.returnType);
			if (cReturnType == null) throw new CompileException("Unsupported type: " + components.returnType);
			return cReturnType;
		} else return CompilerUtils.inferReturnType(components.body);
	}

	private static StringBuilder parseParameters(String params, Map<String, String> typeMapping) throws CompileException {
		StringBuilder paramList = new StringBuilder();
		if (params != null && !params.trim().isEmpty()) {
			String[] paramArray = params.split(",");
			for (int i = 0; i < paramArray.length; i++) {
				String param = paramArray[i].trim();
				if (!param.isEmpty()) {
					ParameterContext context = new ParameterContext(paramList, param);
					context.setCommaAndMapping(i > 0, typeMapping);
					appendParameter(context);
				}
			}
		}
		return paramList;
	}

	private static void appendParameter(ParameterContext context) throws CompileException {
		// Check if this is already a C-style parameter (contains spaces but no colon)
		if (!context.param.contains(":") && context.param.contains(" ")) {
			// Already formatted C parameter, use as-is
			if (context.needsComma) context.paramList.append(", ");
			context.paramList.append(context.param);
		} else {
			// Magma-style parameter, convert to C
			String[] parts = context.param.split("\\s*:\\s*");
			if (parts.length == 2) {
				String paramName = parts[0].trim();
				String paramType = parts[1].trim();
				String cType = context.typeMapping.get(paramType);
				if (cType == null) throw new CompileException("Unsupported type: " + paramType);
				if (context.needsComma) context.paramList.append(", ");
				context.paramList.append(cType).append(" ").append(paramName);
			}
		}
	}

	private static InnerFunctionProcessor.InnerFunctionResult processInnerFunctions(FunctionComponents components,
																																									Map<String, String> typeMapping)
			throws CompileException {
		InnerFunctionProcessor.InnerFunctionParams innerParams =
				new InnerFunctionProcessor.InnerFunctionParams(components.body, components.functionName);
		innerParams.typeMapping.putAll(typeMapping);
		InnerFunctionProcessor.InnerFunctionContext context = new InnerFunctionProcessor.InnerFunctionContext(innerParams);
		return InnerFunctionProcessor.extractInnerFunctions(context);
	}

	private static class StructGenerationResult {
		final String structDefinition;
		final String modifiedBody;

		StructGenerationResult(String structDefinition, String modifiedBody) {
			this.structDefinition = structDefinition;
			this.modifiedBody = modifiedBody;
		}
	}

	private static class StructGenerationParams {
		final String functionName;
		final String body;
		final Map<String, String> typeMapping;
		private String functionParams;

		StructGenerationParams(String functionName, String body) {
			this.functionName = functionName;
			this.body = body;
			this.typeMapping = new HashMap<>();
			this.functionParams = "";
		}

		void setTypeMapping(Map<String, String> typeMapping) {
			this.typeMapping.putAll(typeMapping);
		}

		void setFunctionParams(String functionParams) {
			this.functionParams = functionParams != null ? functionParams : "";
		}

		String getFunctionParams() {
			return functionParams;
		}
	}

	private static boolean hasLocalVariables(String body) {
		return body.contains("let ");
	}


	private static StructGenerationResult generateStructForLocalVariables(StructGenerationParams params) 
		throws CompileException {
		
		StringBuilder structFields = new StringBuilder();
		String modifiedBody = params.body;
		StringBuilder paramInitialization = new StringBuilder();
		
		// Process function parameters
		ParameterProcessingResult paramResult = processParametersForStruct(params);
		structFields.append(paramResult.structFields);
		paramInitialization.append(paramResult.initialization);
		
		// Process local variables
		Pattern letPattern = Pattern.compile("let\\s+(\\w+)\\s*=\\s*(\\d+)");
		Matcher matcher = letPattern.matcher(params.body);
		
		while (matcher.find()) {
			String varName = matcher.group(1);
			String value = matcher.group(2);
			String cType = "int32_t";
			
			if (structFields.length() > 0) structFields.append(" ");
			structFields.append(cType).append(" ").append(varName).append(";");
			
			String originalDecl = "let " + varName + " = " + value + ";";
			String replacement = "this." + varName + " = " + value + ";";
			modifiedBody = modifiedBody.replace(originalDecl, replacement);
		}
		
		String structDef = "struct " + params.functionName + "_t {" + structFields + "};";
		
		String structInit = "struct " + params.functionName + "_t this;";
		if (paramInitialization.length() > 0) structInit += " " + paramInitialization;
		modifiedBody = structInit + (modifiedBody.isEmpty() ? "" : " " + modifiedBody);
		
		return new StructGenerationResult(structDef, modifiedBody);
	}

	private static class ParameterProcessingResult {
		final String structFields;
		final String initialization;

		ParameterProcessingResult(String structFields, String initialization) {
			this.structFields = structFields;
			this.initialization = initialization;
		}
	}

	private static ParameterProcessingResult processParametersForStruct(StructGenerationParams params) 
		throws CompileException {
		StringBuilder structFields = new StringBuilder();
		StringBuilder paramInitialization = new StringBuilder();
		
		String functionParams = params.getFunctionParams();
		if (functionParams == null || functionParams.trim().isEmpty()) {
			return new ParameterProcessingResult("", "");
		}
		
		String[] paramArray = functionParams.split(",");
		for (String param : paramArray) {
			param = param.trim();
			if (param.isEmpty()) continue;
			
			String[] parts = param.split("\\s*:\\s*");
			if (parts.length != 2) continue;
			
			String paramName = parts[0].trim();
			String paramType = parts[1].trim();
			String cType = params.typeMapping.get(paramType);
			if (cType == null) throw new CompileException("Unsupported type: " + paramType);
			
			if (structFields.length() > 0) structFields.append(" ");
			structFields.append(cType).append(" ").append(paramName).append(";");
			
			if (paramInitialization.length() > 0) paramInitialization.append(" ");
			paramInitialization.append("this.").append(paramName).append(" = ").append(paramName).append(";");
		}
		
		return new ParameterProcessingResult(structFields.toString(), paramInitialization.toString());
	}
}