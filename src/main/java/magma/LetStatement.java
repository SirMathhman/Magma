package magma;

import java.util.regex.Matcher;

// Class to hold let statement parameters
class LetStatement {
	private final String varName;
	private final String typeAnnotation;
	private final String numericValue;
	private final String suffix;
	private final String booleanValue;

	LetStatement(Matcher matcher) {
		this.varName = matcher.group(1);
		this.typeAnnotation = matcher.group(2);
		this.numericValue = matcher.group(3);
		this.suffix = matcher.group(4);
		this.booleanValue = matcher.group(5);
	}

	String getVarName() {
		return varName;
	}

	String getTypeAnnotation() {
		return typeAnnotation;
	}

	String getNumericValue() {
		return numericValue;
	}

	String getSuffix() {
		return suffix;
	}

	String getBooleanValue() {
		return booleanValue;
	}

	// Format the declaration
	String formatDeclaration(String type, String value) {
		return type + " " + varName + " = " + value + ";";
	}
}
