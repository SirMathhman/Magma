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
		
		// Try to monomorphize the variadic function
		try {
			String monomorphizedFunction = GenericRegistry.monomorphizeVariadicFunction(functionName, args.length);
			String monomorphizedCall = functionName + "_" + args.length + "(" + argsString + ")";
			// Return the monomorphized function definition + the call
			return monomorphizedFunction + " " + monomorphizedCall;
		} catch (CompileException e) {
			// Not a variadic function
			return null;
		}
	}
}