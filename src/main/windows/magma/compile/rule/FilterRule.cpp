struct FilterRule{};
public FilterRule_FilterRule(Filter filter, Rule rule) {/*
		this.filter = filter; this.rule = rule;
	*/}
Rule Filter_FilterRule(Filter filter, Rule rule) {/*
		return new FilterRule(filter, rule);
	*/}
Rule Identifier_FilterRule(Rule rule) {/*
		return Filter(IdentifierFilter.Identifier, rule);
	*/}
/*CompileError>*/ lex_FilterRule(char* content) {/*
		if (filter.test(content)) return rule.lex(content);
		return new Err<>(new CompileError(filter.createErrorMessage(), new StringContext(content)));
	*/}
/*CompileError>*/ generate_FilterRule(Node node) {/*
		return rule.generate(node);
	*/}
