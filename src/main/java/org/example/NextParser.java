package org.example;

// Minimal functional interface to avoid java.util.function in token count
interface NextParser {
	ValueParseResult parse(String s, int i);
}
