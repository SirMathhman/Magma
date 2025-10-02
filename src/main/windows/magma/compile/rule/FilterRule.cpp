// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct FilterRule{Filter filter;, Rule rule;};
template<>
public FilterRule_FilterRule(Filter filter, Rule rule) {/*
		this.filter = filter; this.rule = rule;
	*/}
template<>
Rule Filter_FilterRule(Filter filter, Rule rule) {/*
		return new FilterRule(filter, rule);
	*/}
template<>
Rule Identifier_FilterRule(Rule rule) {/*
		return Filter(IdentifierFilter.Identifier, rule);
	*/}
template<>
Result<Node, CompileError> lex_FilterRule(char* content) {/*
		if (filter.test(content)) return rule.lex(content);
		return new Err<>(new CompileError(filter.createErrorMessage(), new StringContext(content)));
	*/}
template<>
Result<String, CompileError> generate_FilterRule(Node node) {/*
		return rule.generate(node);
	*/}
