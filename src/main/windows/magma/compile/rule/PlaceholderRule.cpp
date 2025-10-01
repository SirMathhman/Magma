struct PlaceholderRule(Rule rule) implements Rule{};
char* wrap_PlaceholderRule(Rule rule) implements Rule(char* input) {/*
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	*/}
Rule Placeholder_PlaceholderRule(Rule rule) implements Rule(Rule rule) {/*
		return new PlaceholderRule(rule);
	*/}
/*CompileError>*/ lex_PlaceholderRule(Rule rule) implements Rule(char* content) {/*
		return rule.lex(content);
	*/}
/*CompileError>*/ generate_PlaceholderRule(Rule rule) implements Rule(Node node) {/*
		return rule.generate(node).mapValue(PlaceholderRule::wrap);
	*/}
