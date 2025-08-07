package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		
		// Pattern to match "let x = 100;" or "let x : TYPE = 100;" format
		// where TYPE can be U8, U16, U32, U64, I8, I16, I32, I64
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64)\\s*)?=\\s*(\\d+)\\s*;");
		Matcher matcher = letPattern.matcher(input);
		
		if (matcher.matches()) {
			String variableName = matcher.group(1);
			String type = matcher.group(2);
			String value = matcher.group(3);
			
			// Default to I32 if no type is specified
			if (type == null) {
				type = "I32";
			}
			
			// Map Magma types to C++ types
			String cppType;
			switch (type) {
				case "U8":
					cppType = "uint8_t";
					break;
				case "U16":
					cppType = "uint16_t";
					break;
				case "U32":
					cppType = "uint32_t";
					break;
				case "U64":
					cppType = "uint64_t";
					break;
				case "I8":
					cppType = "int8_t";
					break;
				case "I16":
					cppType = "int16_t";
					break;
				case "I32":
					cppType = "int32_t";
					break;
				case "I64":
					cppType = "int64_t";
					break;
				default:
					throw new CompileException();
			}
			
			return cppType + " " + variableName + " = " + value + ";";
		}
		
		throw new CompileException();
	}
}
