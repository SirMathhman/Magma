package magma;

import java.util.Map;
import java.util.regex.Matcher;

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

		String innerDefs = innerResult.innerFunctionDefs.isEmpty() ? "" : innerResult.innerFunctionDefs + " ";
		return innerDefs + cReturnType + " " + components.functionName + "(" + paramList + "){" +
					 Compiler.compileCode(innerResult.processedBody) + "}";
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

	private static InnerFunctionProcessor.InnerFunctionResult processInnerFunctions(FunctionComponents components,
																																									Map<String, String> typeMapping)
			throws CompileException {
		InnerFunctionProcessor.InnerFunctionParams innerParams =
				new InnerFunctionProcessor.InnerFunctionParams(components.body, components.functionName);
		innerParams.typeMapping.putAll(typeMapping);
		InnerFunctionProcessor.InnerFunctionContext context = new InnerFunctionProcessor.InnerFunctionContext(innerParams);
		return InnerFunctionProcessor.extractInnerFunctions(context);
	}
}