package magma;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

class StatementCompilerUtils {
	static class StatementContext {
		final Set<String> mutableVars;
		final Map<String, String> typeMapping;

		StatementContext(Set<String> mutableVars, Map<String, String> typeMapping) {
			this.mutableVars = mutableVars;
			this.typeMapping = typeMapping;
		}
	}

	static class SpecialArrayContext {
		final StatementContext context;
		final boolean is2DArray;

		SpecialArrayContext(StatementContext context, boolean is2DArray) {
			this.context = context;
			this.is2DArray = is2DArray;
		}
	}

	static class AssignContext {
		final StatementContext context;
		final boolean isArrayIndex;

		AssignContext(StatementContext context, boolean isArrayIndex) {
			this.context = context;
			this.isArrayIndex = isArrayIndex;
		}
	}

	static class TypeResolutionParams {
		final String declaredType;
		final String typeSuffix;
		final StatementContext context;

		TypeResolutionParams(TypeInput input) {
			this.declaredType = input.declaredType;
			this.typeSuffix = input.typeSuffix;
			this.context = input.context;
		}
	}

	static class TypeInput {
		final String declaredType;
		final String typeSuffix;
		final StatementContext context;

		TypeInput(TypeData data, StatementContext context) {
			this.declaredType = data.declaredType;
			this.typeSuffix = data.typeSuffix;
			this.context = context;
		}
	}

	static class TypeData {
		final String declaredType;
		final String typeSuffix;

		TypeData(String declaredType, String typeSuffix) {
			this.declaredType = declaredType;
			this.typeSuffix = typeSuffix;
		}
	}

	static String compileArrayStatement(Matcher matcher, StatementContext context) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elementType = matcher.group(3);
		String size = matcher.group(4);
		String elements = matcher.group(5);

		if (mutKeyword != null) context.mutableVars.add(variableName);

		String cType = mapType(elementType, context.typeMapping);
		String cleanElements = elements.replaceAll("\\s+", " ").trim();

		return cType + " " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	static String compileSpecialArrayStatement(Matcher matcher, SpecialArrayContext specialContext)
			throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elementType = matcher.group(3);

		if (mutKeyword != null) specialContext.context.mutableVars.add(variableName);
		String cType = mapType(elementType, specialContext.context.typeMapping);

		if (specialContext.is2DArray) {
			String rows = matcher.group(4);
			String cols = matcher.group(5);
			String elements = matcher.group(6);
			String processedElements = elements.replace("[", "{ ").replace("]", " }").replaceAll("\\s+", " ").trim();
			return cType + " " + variableName + "[" + rows + "][" + cols + "] = " + processedElements;
		} else {
			String size = matcher.group(4);
			String value = matcher.group(5);
			return cType + " " + variableName + "[" + size + "] = \"" + value + "\"";
		}
	}

	static String compileArrayLiteralStatement(Matcher matcher, StatementContext context) {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elements = matcher.group(3);

		if (mutKeyword != null) context.mutableVars.add(variableName);

		String[] elementArray = elements.split(",");
		int size = elementArray.length;
		String cleanElements = elements.replaceAll("\\s+", " ").trim();

		return "uint8_t " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	static String compileAssignStatement(Matcher matcher, AssignContext assignContext) throws CompileException {
		if (assignContext.isArrayIndex) {
			String variableName = matcher.group(1);
			String index = matcher.group(2);
			String value = matcher.group(3);
			return variableName + "[" + index + "] = " + value;
		} else {
			String variableName = matcher.group(1);
			String value = matcher.group(2);

			if (!assignContext.context.mutableVars.contains(variableName))
				throw new CompileException("Cannot assign to immutable variable: " + variableName);

			return variableName + " = " + value;
		}
	}

	static String resolveType(TypeResolutionParams params) throws CompileException {
		if (params.typeSuffix != null && params.declaredType != null) {
			if (!params.typeSuffix.equals(params.declaredType)) throw new CompileException(
					"Type conflict: declared type " + params.declaredType + " does not match suffix type " + params.typeSuffix);
			return mapType(params.typeSuffix, params.context.typeMapping);
		}

		if (params.typeSuffix != null) return mapType(params.typeSuffix, params.context.typeMapping);
		if (params.declaredType != null) return mapType(params.declaredType, params.context.typeMapping);
		return "int32_t";
	}

	static String compileConstructorStatement(Matcher matcher, StatementContext context) throws CompileException {
		String variableName = matcher.group(1);
		String typeName = matcher.group(2);
		String constructorName = matcher.group(3);

		String cType = mapType(typeName, context.typeMapping);
		return cType + " " + variableName + " = {}";
	}

	static String compileFunctionCallStatement(Matcher matcher) {
		String functionName = matcher.group(1);
		return functionName + "()";
	}

	static String mapType(String type, Map<String, String> typeMapping) throws CompileException {
		if (type == null) return "int32_t";

		// Handle pointer types like *I32
		if (type.startsWith("*")) {
			String baseType = type.substring(1);
			String cType = typeMapping.get(baseType);
			if (cType == null) throw new CompileException("Unsupported type: " + baseType);
			return cType + "*";
		}

		String cType = typeMapping.get(type);
		// Assume it's a user-defined type (struct)
		if (cType == null) return "struct " + type;
		return cType;
	}
}