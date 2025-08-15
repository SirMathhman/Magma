package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		String result = tryCompilePackage(input);
		if (result != null) return result;

		result = tryCompileClass(input);
		if (result != null) return result;

		result = tryCompileMethodOrStatement(input);
		if (result != null) return result;

		throw new CompileException();
	}

	private static String tryCompilePackage(String input) {
		if (input.startsWith("package ") && input.endsWith(";")) {
			return "";
		}
		return null;
	}

	private static String tryCompileClass(String input) {
		if (input.startsWith("class ") && input.endsWith("}")) {
			String className = input.substring(6, input.indexOf(" {"));
			return "struct " + className + " {};";
		}
		return null;
	}

	private static String tryCompileMethodOrStatement(String input) {
		if (input.startsWith("void ") && input.endsWith("{}")) {
			return input;
		}
		if (input.startsWith("if (") && input.endsWith("}")) {
			return input;
		}
		if (input.equals("return;")) {
			return input;
		}
		return null;
	}
}
