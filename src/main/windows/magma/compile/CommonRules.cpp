// Generated transpiled C++ from 'src\main\java\magma\compile\CommonRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CommonRules {};
Rule Identifier_CommonRules() {
	return Tag("", StrippedIdentifier(""));
}
Rule StrippedIdentifier_CommonRules(String key) {
	return Strip(FilterRule.Identifier(String(key)));
}
