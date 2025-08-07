package com.magma;

/**
 * Helper class to hold type information, processed input, and mutability
 */
class TypeInfo {
	final String cType;
	final String processedInput;
	final boolean isMutable;

	TypeInfo(String cType, String processedInput) {
		this(cType, processedInput, false);
	}
	
	TypeInfo(String cType, String processedInput, boolean isMutable) {
		this.cType = cType;
		this.processedInput = processedInput;
		this.isMutable = isMutable;
	}
}
