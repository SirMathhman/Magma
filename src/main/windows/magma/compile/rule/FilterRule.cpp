struct FilterRule implements Rule{};
public FilterRule_FilterRule implements Rule(Filter filter, Rule rule) {/*
		this.filter = filter; this.rule = rule;
	*/}
Rule Filter_FilterRule implements Rule(Filter filter, Rule rule) {/*
		return new FilterRule(filter, rule);
	*/}
Rule Identifier_FilterRule implements Rule(Rule rule) {/*
		return Filter(IdentifierFilter.Identifier, rule);
	*/}
/*CompileError>*/ lex_FilterRule implements Rule(char* content) {/*
		if (filter.test(content)) return rule.lex(content);
		return new Err<>(new CompileError(filter.createErrorMessage(), new StringContext(content)));
	*/}
/*CompileError>*/ generate_FilterRule implements Rule(Node node) {/*
		return rule.generate(node);
	*/}
