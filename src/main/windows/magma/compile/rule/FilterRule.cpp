// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FilterRule {/*???*/ filter;/*???*/ rule;};
/*???*/ FilterRule_FilterRule(/*???*/ filter, /*???*/ rule) {
	/*???*/ filter;
	/*???*/ rule;
}
/*???*/ Filter_FilterRule(/*???*/ filter, /*???*/ rule) {
	return new_???(filter, rule);
}
/*???*/ Identifier_FilterRule(/*???*/ rule) {
	return Filter(IdentifierFilter.Identifier, rule);
}
/*???*/ Number_FilterRule(/*???*/ rule) {
	return Filter(NumberFilter.Filter, rule);
}
/*???*/ lex_FilterRule(/*???*/ content) {
	if (filter.test(content))return rule.lex(content);
	return new_???(new_???(filter.createErrorMessage(), new_???(content)));
}
/*???*/ generate_FilterRule(/*???*/ node) {
	return rule.generate(node);
}
