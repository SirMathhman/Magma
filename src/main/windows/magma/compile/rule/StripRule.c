struct StripRule(String leftKey, Rule rule, String rightKey) implements Rule{};
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Rule rule) {/*
		return new StripRule("?", rule, "?");
	*/}
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(char* left, Rule rule, char* right) {/*
		return new StripRule(left, rule, right);
	*/}
/*CompileError>*/ lex_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(char* content) {/*
		return rule.lex(content.strip());
	*/}
/*CompileError>*/ generate_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Node node) {/*
		return rule.generate(node).mapValue(generated -> {
			final String leftString = node.findString(leftKey).orElse("");
			final String rightString = node.findString(rightKey).orElse(""); return leftString + generated + rightString;
		});
	*/}
