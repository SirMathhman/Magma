/**
 * A simple Hello World program for the Magma project.
 */
public class Main {
	/**
	 * The main entry point for the application.
	 *
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
	}

	/**
	 * Compiles Magma code to C code.
	 *
	 * @param input the Magma code to compile
	 * @return the equivalent C code
	 */
	public static String compile(String input) {
		// Handle variable declarations with integer types
		if (input.startsWith("let ") && input.contains(" : ") && input.contains(" = ")) {
			// Check for all supported integer types
			String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"};
			
			for (String type : supportedTypes) {
				String typePattern = " : " + type + " = ";
				if (input.contains(typePattern)) {
					// Extract the variable name
					String varName = input.substring(4, input.indexOf(typePattern));
					
					// Extract the value
					String value = input.substring(input.indexOf(typePattern) + typePattern.length(), input.indexOf(";"));
					
					// Map Magma type to C type
					String cType;
					switch (type) {
						case "I8":
							cType = "int8_t";
							break;
						case "I16":
							cType = "int16_t";
							break;
						case "I32":
							cType = "int32_t";
							break;
						case "I64":
							cType = "int64_t";
							break;
						case "U8":
							cType = "uint8_t";
							break;
						case "U16":
							cType = "uint16_t";
							break;
						case "U32":
							cType = "uint32_t";
							break;
						case "U64":
							cType = "uint64_t";
							break;
						default:
							cType = "int32_t"; // Default to int32_t
					}
					
					return cType + " " + varName + " = " + value + ";";
				}
			}
		}
		
		// Default behavior for other inputs
		return input;
	}
}