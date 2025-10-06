// Generated transpiled C++ from 'src\main\java\magma\compile\CommonRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CommonRules {};
/*???*/ Identifier_CommonRules() {
	return Tag("", StrippedIdentifier(""));
}
/*???*/ StrippedIdentifier_CommonRules(/*???*/ key) {
	return Strip(FilterRule.Identifier(String(key)));
}
