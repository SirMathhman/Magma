package org.example;

public final class ValueParseResult {
	final String value;
	final int nextIndex;

	ValueParseResult(String value, int nextIndex) {
		this.value = value;
		this.nextIndex = nextIndex;
	}
}
