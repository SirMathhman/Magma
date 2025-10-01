struct SuffixRule<>{};
Rule Suffix_SuffixRule(Rule rule, char* suffix) {/*
		return new SuffixRule(rule, suffix);
	*/}
/*CompileError>*/ lex_SuffixRule(char* input) {/*
		if (!input.endsWith(suffix()))
			return new Err<>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));
		final String slice = input.substring(0, input.length() - suffix().length());
		return getRule().lex(slice);
	*/}
/*CompileError>*/ generate_SuffixRule(Node node) {/*
		return rule.generate(node).mapValue(value -> value + suffix());
	*/}
Rule getRule_SuffixRule() {/*
		return rule;
	*/}
