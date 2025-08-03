package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 	
 	// Check for variable declaration and reference
 	Pattern letPattern = Pattern.compile("let\\s+(\\w+)\\s*=\\s*(\\d+);\\s*(\\w+)");
 	Matcher matcher = letPattern.matcher(value);
 	
 	if (matcher.matches()) {
 		String varName = matcher.group(1);
 		String varValue = matcher.group(2);
 		String reference = matcher.group(3);
 		
 		// Check if the reference matches the variable name
 		if (varName.equals(reference)) {
 			return varValue;
 		}
 	}
 	
 	// Invalid input
 	throw new ApplicationException();
 }
}
