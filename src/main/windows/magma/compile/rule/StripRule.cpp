// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StripRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct StripRule<>{char* leftKey;, Rule rule;, char* rightKey;};
Rule Strip_StripRule(Rule rule) {/*
		return new StripRule("?", rule, "?");
	*/}
Rule Strip_StripRule(char* left, Rule rule, char* right) {/*
		return new StripRule(left, rule, right);
	*/}
/*CompileError>*/ lex_StripRule(char* content) {/*
		return rule.lex(content.strip());
	*/}
/*CompileError>*/ generate_StripRule(Node node) {/*
		return rule.generate(node).mapValue(generated -> {
			final String leftString = node.findString(leftKey).orElse("");
			final String rightString = node.findString(rightKey).orElse(""); return leftString + generated + rightString;
		});
	*/}
