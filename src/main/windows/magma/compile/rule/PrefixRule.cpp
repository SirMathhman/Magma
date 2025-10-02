// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PrefixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct PrefixRule{char* prefix;, Rule rule;};
template<>
Rule Prefix_PrefixRule(char* prefix, Rule rule) {/*
		return new PrefixRule(prefix, rule);
	*/}
template<>
Result<Node, CompileError> lex_PrefixRule(char* content) {/*
		if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
		else return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(content)));
	*/}
template<>
Result<String, CompileError> generate_PrefixRule(Node node) {/*
		return rule.generate(node).mapValue(inner -> prefix + inner);
	*/}
