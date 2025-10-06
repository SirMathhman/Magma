// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule {/*???*/ rule;/*???*/ suffix;};
/*???*/ Suffix_SuffixRule(/*???*/ rule, /*???*/ suffix) {
	return new_???(rule, suffix);
}
Result<> lex_SuffixRule(/*???*/ input) {
	if (/*???*/.endsWith(suffix()))return new_???(new_???(""+suffix+"", new_???(input)));
	/*???*/ slice=input.substring(/*???*/, /*???*/);
	return getRule().lex(slice);
}
Result<> generate_SuffixRule(/*???*/ node) {
	return rule.generate(node).mapValue(/*???*/+suffix());
}
/*???*/ getRule_SuffixRule() {
	/*???*/ rule;
}
