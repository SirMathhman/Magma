package org.example;

final class AssignmentParseResult {
	final String value;
	final int nextIndex;

	AssignmentParseResult(String value, int nextIndex) {
		this.value = value;
		this.nextIndex = nextIndex;
	}
}
