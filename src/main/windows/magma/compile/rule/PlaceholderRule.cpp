// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct PlaceholderRule{Rule rule;};
template<>
char* wrap_PlaceholderRule(char* input) {/*
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	*/}
template<>
Rule Placeholder_PlaceholderRule(Rule rule) {/*
		return new PlaceholderRule(rule);
	*/}
template<>
/*CompileError>*/ lex_PlaceholderRule(char* content) {/*
		return rule.lex(content);
	*/}
template<>
/*CompileError>*/ generate_PlaceholderRule(Node node) {/*
		return rule.generate(node).mapValue(PlaceholderRule::wrap);
	*/}
