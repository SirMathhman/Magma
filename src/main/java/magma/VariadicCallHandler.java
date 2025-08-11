package magma;

import java.util.regex.Matcher;

class VariadicCallHandler {
	static String tryCompileVariadicFunctionCall(Matcher matcher) throws CompileException {
		String functionName = matcher.group(1);
		String argsString = matcher.group(2);
		
		// Count the arguments to determine the length for monomorphization
		String[] args = argsString.trim().isEmpty() ? new String[0] : argsString.split(",");
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].trim();
		}
		
		// Try to monomorphize the variadic function first
		try {
			String monomorphizedFunction = GenericRegistry.monomorphizeVariadicFunction(functionName, args.length);
			String monomorphizedCall = functionName + "_" + args.length + "(" + argsString + ")";
			// Return the monomorphized function definition + the call
			return monomorphizedFunction + " " + monomorphizedCall;
		} catch (CompileException e) {
			// Not a user-defined variadic function, check if it's a system function
			if (isSystemFunction(functionName)) {
				return functionName + "(" + argsString + ")";
			}
			// Not a variadic function at all
			return null;
		}
	}
	
	private static boolean isSystemFunction(String functionName) {
		// List of common system functions that should be passed through directly
		switch (functionName) {
			case "printf":
			case "scanf":
			case "fprintf":
			case "sprintf":
			case "puts":
				return true;
			default:
				return false;
		}
	}
}