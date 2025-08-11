package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InnerFunctionProcessor {

	static class InnerFunctionResult {
		final String innerFunctionDefs;
		final String processedBody;

		InnerFunctionResult(String innerFunctionDefs, String processedBody) {
			this.innerFunctionDefs = innerFunctionDefs;
			this.processedBody = processedBody;
		}
	}

	static class InnerFunctionContext {
		final String body;
		final String outerFunctionName;
		final Map<String, String> typeMapping;
		final boolean isClass;

		InnerFunctionContext(InnerFunctionParams params) {
			this.body = params.body;
			this.outerFunctionName = params.outerFunctionName;
			this.typeMapping = params.typeMapping;
			this.isClass = params.isClass;
		}
	}

	static class InnerFunctionData {
		final String functionName;
		final String params;
		final String returnType;
		final String body;

		InnerFunctionData(InnerFunctionInfo info) {
			this.functionName = info.functionName;
			this.params = info.params;
			this.returnType = info.returnType;
			this.body = info.body;
		}
	}

	static class InnerFunctionParams {
		final String body;
		final String outerFunctionName;
		final Map<String, String> typeMapping;
		boolean isClass;

		InnerFunctionParams(String body, String outerFunctionName) {
			this.body = body;
			this.outerFunctionName = outerFunctionName;
			this.typeMapping = new HashMap<>();
			this.isClass = false;
		}

		void setIsClass(boolean isClass) {
			this.isClass = isClass;
		}
	}

	static class InnerFunctionInfo {
		final String functionName;
		final String params;
		String returnType;
		String body;

		InnerFunctionInfo(String functionName, String params) {
			this.functionName = functionName;
			this.params = params;
			this.returnType = "";
			this.body = "";
		}
	}

	static class InnerFunctionMatch {
		final String fullMatch;
		final String name;
		final String params;
		final String returnType;
		final String body;

		InnerFunctionMatch(MatchData data) {
			this.fullMatch = data.fullMatch;
			this.name = data.name;
			this.params = data.params;
			this.returnType = data.returnType;
			this.body = data.body;
		}
	}

	static class MatchData {
		final String name;
		final String params;
		final String returnType;
		final String body;
		String fullMatch;

		MatchData(String fullMatch, String body) {
			this.fullMatch = fullMatch;
			this.body = body;
			this.name = null;
			this.params = null;
			this.returnType = null;
		}

		MatchData(FunctionComponents components, String body) {
			this.body = body;
			this.name = components.name;
			this.params = components.params;
			this.returnType = components.returnType;
		}

		void setFullMatch(String fullMatch) {
			this.fullMatch = fullMatch;
		}
	}

	static class FunctionComponents {
		final String name;
		final String returnType;
		String params;

		FunctionComponents(String name, String returnType) {
			this.name = name;
			this.returnType = returnType;
		}

		void setParams(String params) {
			this.params = params;
		}
	}

	static InnerFunctionResult extractInnerFunctions(InnerFunctionContext context) throws CompileException {
		List<String> innerFunctionDefs = new ArrayList<>();
		String processedBody = context.body;

		int start = 0;
		while (true) {
			int fnIndex = processedBody.indexOf("fn ", start);
			if (fnIndex == -1) break;

			InnerFunctionMatch match = FunctionDefinitionExtractor.extractFunctionDefinition(processedBody, fnIndex);
			if (match == null) {
				start = fnIndex + 3;
				continue;
			}

			String innerFunctionName = match.name;
			String params = match.params;
			String returnType = match.returnType;
			String innerBody = match.body;

			String uniqueName = innerFunctionName + "_" + context.outerFunctionName;

			InnerFunctionParams nestedParams = new InnerFunctionParams(innerBody, uniqueName);
			nestedParams.typeMapping.putAll(context.typeMapping);
			nestedParams.setIsClass(context.isClass);
			InnerFunctionContext nestedContext = new InnerFunctionContext(nestedParams);
			InnerFunctionResult nestedResult = extractInnerFunctions(nestedContext);

			// Check if inner function accesses outer variables
			String modifiedBody = nestedResult.processedBody;
			String modifiedParams = params;
			
			if (context.isClass) {
				// For class methods, always add this pointer but don't transform variable references
				// Class method parameters should remain as parameters, not be transformed to this->field access
				String structType = "struct " + context.outerFunctionName + "*";
				modifiedParams = params.isEmpty() ? structType + " this" : structType + " this, " + params;
				// Keep the body as-is for class methods - parameters should remain as parameters
				modifiedBody = nestedResult.processedBody;
			} else if (accessesOuterVariables(nestedResult.processedBody, context.outerFunctionName)) {
				// Add struct parameter and transform variable references for regular inner functions
				String structType = "struct " + context.outerFunctionName + "_t*";
				modifiedParams = params.isEmpty() ? structType + " this" : structType + " this, " + params;
				modifiedBody = transformVariableReferences(nestedResult.processedBody);
			}

			InnerFunctionInfo info = new InnerFunctionInfo(uniqueName, modifiedParams);
			info.returnType = returnType;
			info.body = modifiedBody;
			InnerFunctionData functionData = new InnerFunctionData(info);
			Matcher innerMatcher = FunctionMatcherCreator.createInnerFunctionMatcher(functionData);
			String compiledInnerFunction = CompilerUtils.compileFunctionStatement(innerMatcher, context.typeMapping);

			innerFunctionDefs.add(nestedResult.innerFunctionDefs + compiledInnerFunction);

			processedBody = processedBody.replace(match.fullMatch, "");

			processedBody = processedBody.replaceAll("\\b" + innerFunctionName + "\\(", uniqueName + "(");

			start = 0;
		}

		String allInnerFunctionDefs = String.join("", innerFunctionDefs);
		return new InnerFunctionResult(allInnerFunctionDefs, processedBody.trim());
	}

	private static boolean accessesOuterVariables(String body, String outerFunctionName) {
		// Simple heuristic: if the body contains variable references that look like outer variables
		// For now, check if there are variable assignments or references that aren't declarations
		Pattern varUsagePattern = Pattern.compile("\\b[a-zA-Z]\\w*\\b");
		Matcher matcher = varUsagePattern.matcher(body);
		
		while (matcher.find()) {
			String varName = matcher.group();
			// Skip keywords and known function names
			if (!varName.equals("let") && !varName.equals("return") && !varName.equals(outerFunctionName)) {
				// Check if this variable is used but not declared in this scope
				if (body.contains(varName) && !body.contains("let " + varName)) {
					return true;
				}
			}
		}
		return false;
	}

	private static String transformVariableReferences(String body) {
		// Transform variable references to struct field access
		// Pattern to match variable usage that's not a declaration
		Pattern varRefPattern = Pattern.compile("\\b([a-zA-Z]\\w*)\\b(?!\\s*=)");
		
		return varRefPattern.matcher(body).replaceAll(match -> {
			String varName = match.group(1);
			// Skip keywords
			if (varName.equals("let") || varName.equals("return") || varName.equals("this")) {
				return varName;
			}
			// Transform to struct field access
			return "this->" + varName;
		});
	}
}