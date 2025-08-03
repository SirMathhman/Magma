package magma;

public class Application {
 public static String run(String value) throws ApplicationException {
 	if (value == null) {
 		throw new ApplicationException();
 	}
 	
 	// Check if the value starts with digits
 	if (value.matches("^\\d+$")) {
 		// Just digits, return as is
 		return value;
 	} else if (value.matches("^\\d+(U8|U16|U32|U64|I8|I16|I32|I64)$")) {
 		// Valid digit with valid suffix, extract just the digit part
 		return value.replaceAll("^(\\d+).*$", "$1");
 	}
 	
 	// Invalid input
 	throw new ApplicationException();
 }
}
