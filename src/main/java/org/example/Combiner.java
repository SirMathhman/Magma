package org.example;

interface Combiner {
	ValueParseResult combine(ValueParseResult left,
													 ValueParseResult right,
													 char op,
													 String s);
}
