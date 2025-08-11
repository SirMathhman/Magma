package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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

		InnerFunctionContext(InnerFunctionParams params) {
			this.body = params.body;
			this.outerFunctionName = params.outerFunctionName;
			this.typeMapping = params.typeMapping;
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

		InnerFunctionParams(String body, String outerFunctionName) {
			this.body = body;
			this.outerFunctionName = outerFunctionName;
			this.typeMapping = new HashMap<>();
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
			InnerFunctionContext nestedContext = new InnerFunctionContext(nestedParams);
			InnerFunctionResult nestedResult = extractInnerFunctions(nestedContext);

			InnerFunctionInfo info = new InnerFunctionInfo(uniqueName, params);
			info.returnType = returnType;
			info.body = nestedResult.processedBody;
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
}