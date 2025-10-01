struct PrefixRule(String prefix, Rule rule) implements Rule{};
Rule Prefix_PrefixRule(String prefix, Rule rule) implements Rule(char* prefix, Rule rule) {/*
		return new PrefixRule(prefix, rule);
	*/}
/*CompileError>*/ lex_PrefixRule(String prefix, Rule rule) implements Rule(char* content) {/*
		if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
		else return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(content)));
	*/}
/*CompileError>*/ generate_PrefixRule(String prefix, Rule rule) implements Rule(Node node) {/*
		return rule.generate(node).mapValue(inner -> prefix + inner);
	*/}
