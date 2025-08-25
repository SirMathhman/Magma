package org.example;

// Small helper used to return a function/block body and the next index.
// This centralizes the common pattern of accepting either a brace-delimited
// block or scanning to a top-level terminator (skipping nested
// parens/braces/brackets).
final class BodyParseResult {
	final String body;
	final int nextIndex;

	BodyParseResult(String b, int n) {
		this.body = b;
		this.nextIndex = n;
	}
}
