package magma;

import java.util.regex.Matcher;

// Class to hold let statement parameters
class LetStatement {
	private final String varName;
	private final String typeAnnotation;
	private final String numericValue;
	private final String suffix;
	private final String booleanValue;
	private final String charValue;
	private final String arrayType;
	private final String arraySize;
	private final String arrayValues;

	LetStatement(Matcher matcher) {
		this.varName = matcher.group(1);
		this.typeAnnotation = matcher.group(2);
		this.arrayType = matcher.group(3);
		this.arraySize = matcher.group(4);
		this.numericValue = matcher.group(5);
		this.suffix = matcher.group(6);
		this.booleanValue = matcher.group(7);
		this.charValue = matcher.group(8);
		this.arrayValues = matcher.group(9);
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
	
	String getCharValue() {
		return charValue;
	}
	
	String getArrayType() {
		return arrayType;
	}
	
	String getArraySize() {
		return arraySize;
	}
	
	String getArrayValues() {
		return arrayValues;
	}
	
	boolean isArrayDeclaration() {
		return arrayType != null && arraySize != null;
	}

	// Format the declaration
	String formatDeclaration(String type, String value) {
		return type + " " + varName + " = " + value + ";";
	}
	
	// Format array declaration
	String formatArrayDeclaration(String elementType, String size, String values) {
		return elementType + " " + varName + "[" + size + "] = {" + values + "};";
	}
}
