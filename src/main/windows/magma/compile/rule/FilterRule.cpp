// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FilterRule {Filter filter;Rule rule;};
public FilterRule_FilterRule(Filter filter, Rule rule) {
	this.filter=filter;
	this.rule=rule;
}
Rule Filter_FilterRule(Filter filter, Rule rule) {
	return new_FilterRule((filter, rule);
}
Rule Identifier_FilterRule(Rule rule) {
	return Filter((IdentifierFilter.Identifier, rule);
}
@Override
	public Result<> lex_FilterRule(char* content) {
	return rule.lex(content);
	return new_Err_((new_CompileError((filter.createErrorMessage(), new_StringContext((content)));
}
@Override
	public Result<> generate_FilterRule(Node node) {
	return rule.generate(node);
}
