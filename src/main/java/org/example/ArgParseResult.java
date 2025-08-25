package org.example;

import java.util.List;

// Small helper to return parsed argument values and next index after ')'
final class ArgParseResult {
	final List<String> args;
	final int nextIndex;

	ArgParseResult(List<String> a, int n) {
		this.args = a;
		this.nextIndex = n;
	}
}
