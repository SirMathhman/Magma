// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FilterRule {Filter filter;Rule rule;};
public FilterRule_FilterRule(Filter filter, Rule rule) {
	this.filter=filter;
	this.rule=rule;
}
Rule Filter_FilterRule(Filter filter, Rule rule) {
	return new_???(filter, rule);
}
Rule Identifier_FilterRule(Rule rule) {
	return Filter(IdentifierFilter.Identifier, rule);
}
Rule Number_FilterRule(Rule rule) {
	return Filter(NumberFilter.Filter, rule);
}
Result<> lex_FilterRule(char* content) {
	if (filter.test(content))return rule.lex(content);
	return new_???(new_???(filter.createErrorMessage(), new_???(content)));
}
Result<> generate_FilterRule(Node node) {
	return rule.generate(node);
}
