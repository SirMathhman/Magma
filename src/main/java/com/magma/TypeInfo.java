package com.magma;

/**
 * Helper class to hold type information, processed input, and mutability
 */
class TypeInfo {
	final String cType;
	final String processedInput;
	final boolean isMutable;
	final boolean isPointer;
	final boolean isPointerMutable;

	TypeInfo(String cType, String processedInput) {
		this(cType, processedInput, false);
	}

	TypeInfo(String cType, String processedInput, boolean isMutable) {
		this(cType, processedInput, isMutable, false, false);
	}
	
	TypeInfo(String cType, String processedInput, boolean isMutable, boolean isPointer, boolean isPointerMutable) {
		this.cType = cType;
		this.processedInput = processedInput;
		this.isMutable = isMutable;
		this.isPointer = isPointer;
		this.isPointerMutable = isPointerMutable;
	}
}
