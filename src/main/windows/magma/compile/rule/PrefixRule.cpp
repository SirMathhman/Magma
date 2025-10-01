struct PrefixRule<>{};
Rule Prefix_PrefixRule(char* prefix, Rule rule) {/*
		return new PrefixRule(prefix, rule);
	*/}
/*CompileError>*/ lex_PrefixRule(char* content) {/*
		if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
		else return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(content)));
	*/}
/*CompileError>*/ generate_PrefixRule(Node node) {/*
		return rule.generate(node).mapValue(inner -> prefix + inner);
	*/}
