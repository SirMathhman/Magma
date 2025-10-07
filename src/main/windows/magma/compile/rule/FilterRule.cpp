// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FilterRule {};
public FilterRule_FilterRule() {
	this.filter=filter;
	this.rule=rule;
}
Rule Filter_FilterRule() {
	return new_???();
}
Rule Identifier_FilterRule() {
	return Filter();
}
Rule Number_FilterRule() {
	return Filter();
}
Result<Node, CompileError> lex_FilterRule() {
	if (filter.test())
	{
	return rule.lex();}
	return new_???();
}
Result<String, CompileError> generate_FilterRule() {
	return rule.generate();
}
