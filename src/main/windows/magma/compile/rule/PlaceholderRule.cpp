// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>\nstruct PlaceholderRule<>{Rule rule;};
char* wrap_PlaceholderRule(char* input) {/*
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	*/}
Rule Placeholder_PlaceholderRule(Rule rule) {/*
		return new PlaceholderRule(rule);
	*/}
/*CompileError>*/ lex_PlaceholderRule(char* content) {/*
		return rule.lex(content);
	*/}
/*CompileError>*/ generate_PlaceholderRule(Node node) {/*
		return rule.generate(node).mapValue(PlaceholderRule::wrap);
	*/}
